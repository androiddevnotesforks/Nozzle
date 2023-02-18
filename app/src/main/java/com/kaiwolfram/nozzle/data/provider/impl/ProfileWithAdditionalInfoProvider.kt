package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.provider.IProfileWithAdditionalInfoProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.EventRelayDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.utils.hexToNpub
import com.kaiwolfram.nozzle.model.ProfileWithAdditionalInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

private const val TAG = "ProfileWithAdditionalInfoProvider"
private const val EMIT_INTERVAL_TIME = 2000L

class ProfileWithAdditionalInfoProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val nostrSubscriber: INostrSubscriber,
    private val profileDao: ProfileDao,
    private val contactDao: ContactDao,
    private val eventRelayDao: EventRelayDao,
) : IProfileWithAdditionalInfoProvider {

    override fun getProfile(pubkey: String): Flow<ProfileWithAdditionalInfo> {
        Log.i(TAG, "Get profile $pubkey")
        return flow {
            nostrSubscriber.unsubscribeProfiles()
            val contactPubkeys = listContactPubkeysIfIsOneself(pubkey = pubkey)
            nostrSubscriber.subscribeToProfileMetadataAndContactList(
                pubkeys = contactPubkeys
            )
            while (true) {
                getAndEmitProfile(flowCollector = this, pubkey = pubkey)
                delay(EMIT_INTERVAL_TIME)
            }
        }
    }

    private suspend fun getAndEmitProfile(
        flowCollector: FlowCollector<ProfileWithAdditionalInfo>,
        pubkey: String,
    ) {
        val npub = hexToNpub(pubkey)
        with(flowCollector) {
            profileDao.getProfile(pubkey).let { profile ->
                // TODO: One single god flow?
                val metadata = profile?.getMetadata() ?: Metadata(name = npub)
                val numOfFollowing = contactDao.getNumberOfFollowing(pubkey)
                val numOfFollowers = contactDao.getNumberOfFollowers(pubkey)
                val relays = eventRelayDao.listUsedRelays(pubkey)
                val isFollowedByMe = contactDao.isFollowed(
                    pubkey = pubkeyProvider.getPubkey(),
                    contactPubkey = pubkey
                )
                emit(
                    ProfileWithAdditionalInfo(
                        pubkey = pubkey,
                        npub = npub,
                        metadata = metadata,
                        numOfFollowing = numOfFollowing,
                        numOfFollowers = numOfFollowers,
                        relays = relays,
                        isOneself = isOneself(pubkey = pubkey),
                        isFollowedByMe = isFollowedByMe,
                    )
                )
            }
        }
    }

    private fun isOneself(pubkey: String) = pubkey == pubkeyProvider.getPubkey()

    private suspend fun listContactPubkeysIfIsOneself(pubkey: String): List<String> {
        return if (isOneself(pubkey = pubkey)) {
            contactDao.listContactPubkeys(pubkey) + pubkey
        } else listOf(pubkey)
    }
}
