package com.notgodzilla.lookingforgroup.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


//TODO Prepare for tracking
class PreferencesRepository private constructor(
    private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>

) {
    val isTracking: Flow<Boolean> = dataStore.data.map {
        it[PREF_IS_TRACKING] ?: false
    }.distinctUntilChanged()

    val lastSlotsFilledCount: Flow<String> = dataStore.data.map {
        it[PREF_LAST_SLOTS_FILLED_COUNT] ?: ""
    }.distinctUntilChanged()

    suspend fun setTracking(isTracking: Boolean) {
        dataStore.edit {
            it[PREF_IS_TRACKING] = isTracking
        }
    }

    suspend fun setSlotsFilledCount(slotsFilled: String) {
        dataStore.edit {
            it[PREF_LAST_SLOTS_FILLED_COUNT] = slotsFilled
        }
    }

    companion object {
        private val PREF_IS_TRACKING = booleanPreferencesKey("isTracking")
        private val PREF_LAST_SLOTS_FILLED_COUNT = stringPreferencesKey("lastSlotsFilledCount")
        private var INSTANCE: PreferencesRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("settings")
                }
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun get(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException(
                "PreferencesRepository must be initialized"
            )
        }

    }
}