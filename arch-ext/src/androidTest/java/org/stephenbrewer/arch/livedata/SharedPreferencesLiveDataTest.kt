/*
 * Copyright 2019 Stephen Brewer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.stephenbrewer.arch.livedata

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesLiveDataTest {

    private val key = "key"
    private val defaultValue = "defaultValue"
    private val newValue = "newValue"

    private val appContext = InstrumentationRegistry.getInstrumentation().context
    private val sharedPreferences = appContext.getSharedPreferences("Testing", Context.MODE_PRIVATE)
    private val testLifecycleOwner:LifecycleOwner = LifecycleOwner { lifecycleRegistry }
    private val lifecycleRegistry = LifecycleRegistry(testLifecycleOwner)

    @Before
    fun before() {
        sharedPreferences.edit().clear().apply()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().context
        assertEquals("The app context is correct", "org.stephenbrewer.arch.test", appContext.packageName)
    }

    @UiThreadTest
    @Test
    fun testSharedPrefsEmptyInitialisation() {
        val sharedPreferencesLiveData = SharedPreferencesLiveData(sharedPreferences, key, defaultValue)
        assertEquals("On creation the value is the default", defaultValue, sharedPreferencesLiveData.value)
    }

    @UiThreadTest
    @Test
    fun testSharedPrefsPopulatedInitialisation() {
        sharedPreferences.edit().putString(key, newValue).apply()
        val sharedPreferencesLiveData = SharedPreferencesLiveData(sharedPreferences, key, defaultValue)

        assertEquals("On creation the value is the default", newValue, sharedPreferencesLiveData.value)
    }

    @UiThreadTest
    @Test
    fun testSharedPrefsValueChangesWhenSetWithAnActiveObserver() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        val sharedPreferencesLiveData = SharedPreferencesLiveData(sharedPreferences, key, defaultValue)
        var observedValue: String? = null
        sharedPreferencesLiveData.observe(testLifecycleOwner, Observer {
            observedValue = it
        })

        sharedPreferences.edit().putString(key, newValue).apply()

        assertEquals("On creation the value is the default", newValue, sharedPreferencesLiveData.value)
        assertEquals("On creation the observer is called with the newValue", newValue, observedValue)
    }

    @UiThreadTest
    @Test
    fun testSharedPrefsValueChangesWhenSetWithAnObserverBecomesActive() {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED

        val sharedPreferencesLiveData = SharedPreferencesLiveData(sharedPreferences, key, defaultValue)
        var observedValue: String? = null
        sharedPreferencesLiveData.observe(testLifecycleOwner, Observer {
            observedValue = it
        })

        sharedPreferences.edit().putString(key, newValue).apply()
        assertEquals("On creation the value is the default", defaultValue, sharedPreferencesLiveData.value)
        assertEquals("On creation the observer is not executed", null, observedValue)

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        assertEquals("After resuming the value is becomes the new value", newValue, sharedPreferencesLiveData.value)
        assertEquals("After resuming the observer sets receives the new value", newValue, observedValue)
    }

    @UiThreadTest
    @Test
    fun testSharedPrefsValueIsNull() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        val sharedPreferencesLiveData = SharedPreferencesLiveData(sharedPreferences, key, defaultValue)
        var observedValue: String? = null
        sharedPreferencesLiveData.observe(testLifecycleOwner, Observer {
            observedValue = it
        })

        sharedPreferences.edit().putString(key, newValue).apply()
        assertEquals("On creation when resumed the value is the new value", newValue, sharedPreferencesLiveData.value)
        assertEquals("On creation when resumed the observer receives the new value", newValue, observedValue)

        sharedPreferences.edit().remove(key).apply()
        assertNull("After removal the live data contains null", sharedPreferencesLiveData.value)
        assertNull("After removal the observer receives null", observedValue)
        assertEquals("After removal the source preferences does not contain the key", false, sharedPreferences.contains(key))
    }

    @UiThreadTest
    @Test
    fun testSharedPrefsValueIsNull2() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED

        val sharedPreferencesLiveData = SharedPreferencesLiveData(sharedPreferences, key, defaultValue)
        var observedValue: String? = null
        sharedPreferencesLiveData.observe(testLifecycleOwner, Observer {
            observedValue = it
        })

        sharedPreferencesLiveData.value = newValue
        assertEquals("On creation when resumed the value is the new value", newValue, sharedPreferencesLiveData.value)
        assertEquals("On creation when resumed the observer receives the new value", newValue, observedValue)

        sharedPreferencesLiveData.value = null
        assertNull("After removal the live data contains null", sharedPreferencesLiveData.value)
        assertNull("After removal the observer receives null", observedValue)
        assertEquals("After removal the source preferences does not contain the key", false, sharedPreferences.contains(key))
    }
}
