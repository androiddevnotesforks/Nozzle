package com.kaiwolfram.nozzle.model

class UserRelayType {
    companion object {
        /**
         * Manually added by Nozzle user
         */
        const val CUSTOM = 0

        /**
         * Defined by the user himself. For example:
         * - nip05 relay field
         * - kind 2 relay recommendations
         * - kind 10_002 'r' tags
         */
        const val FIRST = 1

        /**
         * Other hints that can come from 3rd parties. For example:
         * - hint in reply tag
         * - nprofile relays
         *
         * Relays where a user's events have been seen on
         * can be queried by joining eventRelay and contact list hints in contact
         */
        const val SECOND = 2

    }
}
