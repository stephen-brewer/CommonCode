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
 *
 */

package org.stephenbrewer.arch.livedata

import androidx.lifecycle.*
import org.junit.Assert
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule

class TransformationsTest {

    @JvmField
    @Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun distinct_isSetToNullMultipleTimesSettingValueBeforeObserving() {
        val number = MutableLiveData<Int?>()
        val distinctNumber: LiveData<Int?> = number.distinct()
        val observerCallCounter = ObserverCallCounter<Int?>()

        number.value = null

        distinctNumber.observeForever(observerCallCounter)

        Assert.assertEquals("Observer should only be called once.", 1, observerCallCounter.callCounter)

        number.value = null
        number.value = null
        number.value = null

        Assert.assertEquals("Observer should only be called once.", 1, observerCallCounter.callCounter)

    }

    @Test
    fun distinct_isSetToNullMultipleTimesSettingValueAfterObserving() {
        val number = MutableLiveData<Int?>()
        val distinctNumber: LiveData<Int?> = number.distinct()
        val observerCallCounter = ObserverCallCounter<Int?>()

        distinctNumber.observeForever(observerCallCounter)

        Assert.assertEquals("Observer should not be called.", 0, observerCallCounter.callCounter)

        number.value = null
        number.value = null
        number.value = null

        Assert.assertEquals("Observer should only be called once.", 1, observerCallCounter.callCounter)

    }

    @Test
    fun distinct_isSetToSameObjectMultipleTimesSettingValueBeforeObserving() {
        val number = MutableLiveData<Int?>()
        val distinctNumber: LiveData<Int?> = number.distinct()
        val observerCallCounter = ObserverCallCounter<Int?>()

        val three = 3
        number.value = three

        distinctNumber.observeForever(observerCallCounter)

        number.value = three
        number.value = three
        number.value = three

        Assert.assertEquals("Observer should only be called once.", 1, observerCallCounter.callCounter)
    }

    @Test
    fun distinct_isSetToSameObjectMultipleTimesSettingValueAfterObserving() {
        val number = MutableLiveData<Int?>()
        val distinctNumber: LiveData<Int?> = number.distinct()
        val observerCallCounter = ObserverCallCounter<Int?>()

        distinctNumber.observeForever(observerCallCounter)

        val three = 3
        number.value = three
        number.value = three
        number.value = three

        Assert.assertEquals("Observer should only be called once.", 1, observerCallCounter.callCounter)
    }

    @Test
    fun distinct_isSetToDifferentValuesObjectMultipleTimesSettingValueBeforeObserving() {
        val number = MutableLiveData<Int?>()
        val distinctNumber: LiveData<Int?> = number.distinct()
        val observerCallCounter = ObserverCallCounter<Int?>()

        number.value = 1

        distinctNumber.observeForever(observerCallCounter)

        number.value = 1
        number.value = null
        number.value = null
        number.value = 2
        number.value = 2
        number.value = 3

        Assert.assertEquals("Observer should only be called when changing value.", 4, observerCallCounter.callCounter)
    }

    @Test
    fun distinct_isSetToDifferentValuesObjectMultipleTimesSettingValueAfterObserving() {
        val number = MutableLiveData<Int?>()
        val distinctNumber: LiveData<Int?> = number.distinct()
        val observerCallCounter = ObserverCallCounter<Int?>()

        distinctNumber.observeForever(observerCallCounter)

        number.value = null
        number.value = 1
        number.value = null
        number.value = null
        number.value = 2
        number.value = 2
        number.value = 3

        Assert.assertEquals("Observer should only be called when changing value.", 5, observerCallCounter.callCounter)
    }
}