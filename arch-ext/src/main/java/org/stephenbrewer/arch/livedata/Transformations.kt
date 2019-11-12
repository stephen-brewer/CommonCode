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

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * LiveData that only triggers updates when the change different from the previous value
 */
@MainThread
fun <T> LiveData<T?>.distinct(): LiveData<T?> {
    val distinctLD = object: MediatorLiveData<T?>() {
        override fun getValue(): T? {
            return this@distinct.value
        }
    }
    distinctLD.addSource(this, object : Observer<T?> {
        private val NOT_SET = Object()
        @Suppress("UNCHECKED_CAST")
        private var previous: T? = NOT_SET as T

        override fun onChanged(it: T?) {
            if ((it != previous) || (previous == NOT_SET)) {
                previous = it
                distinctLD.value = it
            }
        }
    })

    return distinctLD
}

/**
 * LiveData that only triggers updates when the change is non null
 */
@MainThread
fun <T> LiveData<T?>.nonNull(): LiveData<T> {
    val nonNullLD = object: MediatorLiveData<T>() {
        override fun getValue(): T? {
            return this@nonNull.value
        }
    }
    nonNullLD.addSource(this) {
        if (it != null) {
            nonNullLD.value = it
        }
    }

    return nonNullLD
}

/**
 * LiveData that emits log statements
 */
@MainThread
fun <T> LiveData<T?>.log(logTag: String): LiveData<T?> {
    val loggingLD = object: LoggingLiveData<T?>(logTag) {
        override fun getValue(): T? {
            return this@log.value
        }
    }
    loggingLD.addSource(this) {
        Log.d(logTag, "onChanged       : [$it].")
        loggingLD.value = it
    }

    return loggingLD
}

/**
 * LiveData that emits log statements
 */
open class LoggingLiveData<T>(private val logTag: String): MediatorLiveData<T>() {
    private var observerCounter = 0

    override fun onActive() {
        Log.d(logTag, "onActive        : [${this.value}]")
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
        Log.d(logTag, "onInactive      : [${this.value}]")
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observerCounter++
        Log.d(logTag, "observe         : [$observerCounter] [${owner.lifecycle.currentState}]")
        super.observe(owner, observer)
    }

    override fun observeForever(observer: Observer<in T>) {
        observerCounter++
        Log.d(logTag, "observeForever  : [$observerCounter]")
        super.observeForever(observer)
    }

    override fun removeObserver(observer: Observer<in T>) {
        super.removeObserver(observer)
        observerCounter--
        Log.d(logTag, "removeObserver  : [$observerCounter]")
    }
}