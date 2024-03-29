package com.kaiwolfram.nozzle.data.provider.impl

import android.util.Log
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nozzle.data.getDefaultRelays
import com.kaiwolfram.nozzle.data.nostr.INostrSubscriber
import com.kaiwolfram.nozzle.data.provider.IProfileWithAdditionalInfoProvider
import com.kaiwolfram.nozzle.data.provider.IPubkeyProvider
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.EventRelayDao
import com.kaiwolfram.nozzle.data.room.dao.Nip65Dao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.utils.hexToNpub
import com.kaiwolfram.nozzle.model.ProfileWithAdditionalInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

private const val TAG = "ProfileWithAdditionalInfoProvider"

class ProfileWithAdditionalInfoProvider(
    private val pubkeyProvider: IPubkeyProvider,
    private val nostrSubscriber: INostrSubscriber,
    private val profileDao: ProfileDao,
    private val contactDao: ContactDao,
    private val eventRelayDao: EventRelayDao,
    private val nip65Dao: Nip65Dao,
) : IProfileWithAdditionalInfoProvider {

    override fun getProfileFlow(pubkey: String): Flow<ProfileWithAdditionalInfo> {
        Log.i(TAG, "Get profile $pubkey")
        val npub = hexToNpub(pubkey)
        val profileFlow = profileDao.getProfileFlow(pubkey).distinctUntilChanged()
        val relaysFlow = eventRelayDao.listUsedRelaysFlow(pubkey).distinctUntilChanged()
        val numOfFollowingFlow = contactDao.getNumberOfFollowingFlow(pubkey).distinctUntilChanged()
        val numOfFollowersFlow = contactDao.getNumberOfFollowersFlow(pubkey).distinctUntilChanged()
        val isFollowedByMeFlow = contactDao.isFollowedFlow(
            pubkey = pubkeyProvider.getPubkey(),
            contactPubkey = pubkey
        ).distinctUntilChanged()
        val mainFlow = flow {
            emit(
                ProfileWithAdditionalInfo(
                    pubkey = pubkey,
                    npub = npub,
                    metadata = Metadata(name = npub),
                    numOfFollowing = 0,
                    numOfFollowers = 0,
                    relays = listOf(),
                    isOneself = isOneself(pubkey = pubkey),
                    isFollowedByMe = false,
                )
            )
            nostrSubscriber.unsubscribeToNip65()
            nostrSubscriber.unsubscribeProfiles()
            nostrSubscriber.subscribeToNip65(listOf(pubkey))
            delay(1000)
            nostrSubscriber.subscribeToProfileMetadataAndContactList(
                pubkeys = listContactPubkeysIfIsOneself(pubkey = pubkey),
                relays = nip65Dao.getWriteRelaysOfPubkey(pubkey = pubkey)
                    .ifEmpty {
                        relaysFlow.first().ifEmpty { getDefaultRelays() }
                    }.shuffled()
                    .take(10)  // Don't ask more than 10 relays
            )
        }
        return mainFlow
            .combine(profileFlow) { main, profile ->
                profile?.let { main.copy(metadata = profile.getMetadata()) } ?: main
            }
            .combine(relaysFlow) { main, relays ->
                main.copy(relays = relays)
            }
            .combine(numOfFollowingFlow) { main, numOfFollowing ->
                main.copy(numOfFollowing = numOfFollowing)
            }
            .combine(numOfFollowersFlow) { main, numOfFollowers ->
                main.copy(numOfFollowers = numOfFollowers)
            }
            .combine(isFollowedByMeFlow) { main, isFollowedByMe ->
                main.copy(isFollowedByMe = isFollowedByMe)
            }
    }


    private fun isOneself(pubkey: String) = pubkey == pubkeyProvider.getPubkey()

    private suspend fun listContactPubkeysIfIsOneself(pubkey: String): List<String> {
        return if (isOneself(pubkey = pubkey)) {
            contactDao.listContactPubkeys(pubkey) + pubkey
        } else listOf(pubkey)
    }
}
