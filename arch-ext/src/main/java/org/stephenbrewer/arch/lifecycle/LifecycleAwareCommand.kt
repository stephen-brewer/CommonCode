/*
 * Copyright 2018 Stephen Brewer
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

package org.stephenbrewer.arch.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.annotation.MainThread

class LifecycleAwareCommand<T> {

    private val observers = ArrayList<CommandLifecycleObserver>(1)

    @MainThread
    fun observe(lifecycleOwner: LifecycleOwner, code: (parameter: T?) -> Unit) {
        val commandLifecycleObserver = CommandLifecycleObserver(lifecycleOwner, code)
        lifecycleOwner.lifecycle.addObserver(commandLifecycleObserver)
        observers.add(commandLifecycleObserver)
    }

    // DataBinding does not recognise `execute(parameter: T? = null)` when using the default value
    // so defining this
    @MainThread
    fun execute() {
        execute(null)
    }

    @MainThread
    fun execute(parameter: T?) {
        for (it in observers) {
            it.executeIfActive(parameter)
        }
    }

    private inner class CommandLifecycleObserver(
        private val lifecycleOwner: LifecycleOwner,
        private val code: (parameter: T?) -> Unit
    ) : DefaultLifecycleObserver {

        private fun isActive() = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

        fun executeIfActive(parameter: T?) {
            if (isActive()) {
                code.invoke(parameter)
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            observers.remove(this@CommandLifecycleObserver)
            owner.lifecycle.removeObserver(this@CommandLifecycleObserver)
        }
    }
}