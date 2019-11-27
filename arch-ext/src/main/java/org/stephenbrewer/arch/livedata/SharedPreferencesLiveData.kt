package org.stephenbrewer.arch.livedata

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

class SharedPreferencesLiveData<T: Any>(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: T
): LiveData<T?>() {

    init {
        if (sharedPreferences.contains(key)) {
            updateValue()
        } else {
            super.setValue(defaultValue)
        }
    }

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { listenerPreferences, listenerKey ->
        if ((listenerPreferences != sharedPreferences) || (listenerKey != key)) return@OnSharedPreferenceChangeListener

        updateValue()
    }

    public override fun setValue(value: T?) {
        when (value){
            is Boolean -> sharedPreferences.edit().putBoolean(key, value).apply()
            is Float -> sharedPreferences.edit().putFloat(key, value).apply()
            is Int -> sharedPreferences.edit().putInt(key, value).apply()
            is Long -> sharedPreferences.edit().putLong(key, value).apply()
            is String -> sharedPreferences.edit().putString(key, value).apply()
            null -> sharedPreferences.edit().remove(key).apply()
            else -> throw IllegalArgumentException("Don't know how to process $value")
        }

        super.setValue(value)
    }

    override fun onActive() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        updateValue()
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    private fun updateValue() {
        val nextValue: T? = if (sharedPreferences.contains(key)) {
            @Suppress("UNCHECKED_CAST")
            when (defaultValue) {
                is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
                is Float -> sharedPreferences.getFloat(key, defaultValue) as T
                is Int -> sharedPreferences.getInt(key, defaultValue) as T
                is Long -> sharedPreferences.getLong(key, defaultValue) as T
                is String -> sharedPreferences.getString(key, defaultValue) as T
                else -> throw IllegalArgumentException("Don't know how to process ${defaultValue::class}")
            }
        } else {
            null
        }

        if (nextValue != value) {
            super.setValue(nextValue)
        }
    }
}
