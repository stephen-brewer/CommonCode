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

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.mockk.*
import org.junit.Test

class LifecycleAwareCommandTest {

    private class ParameterType {
        fun test() {
        }
        fun otherTest() {
        }
    }

    @Test
    fun testObserverIsNotCalledWhenInactive() {
        val lifecycleOwner = mockk<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        every { lifecycleOwner.lifecycle } returns lifecycle

        val command = LifecycleAwareCommand<ParameterType>()

        lifecycle.currentState = Lifecycle.State.INITIALIZED
        command.observe(lifecycleOwner) { parameter: ParameterType? ->
            parameter?.test()
        }

        val data = mockk<ParameterType>()
        every { data.test() } just Runs

        command.execute(data)

        verify(exactly = 0) { data.test() }
    }

    @Test
    fun testObserverIsCalledWhenActive() {
        val lifecycleOwner = mockk<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        every { lifecycleOwner.lifecycle } returns lifecycle

        val command = LifecycleAwareCommand<ParameterType>()

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        command.observe(lifecycleOwner) { parameter: ParameterType? ->
            parameter?.test()
        }

        val data = mockk<ParameterType>()
        every { data.test() } just Runs

        command.execute(data)

        verify(exactly = 1) { data.test() }
    }

    @Test
    fun testObserversAreAddedAndRemoved() {
        val lifecycleOwner = mockk<LifecycleOwner>()
        val lifecycle = spyk(LifecycleRegistry(lifecycleOwner))
        every { lifecycleOwner.lifecycle } returns lifecycle

        val command = LifecycleAwareCommand<ParameterType>()

        command.observe(lifecycleOwner) { parameter: ParameterType? ->
            parameter?.test()
        }

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

        verify(exactly = 1) { lifecycle.addObserver(any()) }
        verify(exactly = 1) { lifecycle.removeObserver(any()) }
    }

    @Test
    fun testObserverIsCalledForEveryActiveExecute() {
        val lifecycleOwner = mockk<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        every { lifecycleOwner.lifecycle } returns lifecycle

        val command = LifecycleAwareCommand<ParameterType>()

        command.observe(lifecycleOwner) { parameter: ParameterType? ->
            parameter?.test()
        }

        val data = mockk<ParameterType>()
        every { data.test() } just Runs

        lifecycle.currentState = Lifecycle.State.RESUMED
        command.execute(data)
        lifecycle.currentState = Lifecycle.State.CREATED
        command.execute(data)
        lifecycle.currentState = Lifecycle.State.RESUMED
        command.execute(data)

        verify(exactly = 2) { data.test() }
    }

    @Test
    fun testMultipleObservers() {
        val lifecycleOwner = mockk<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        every { lifecycleOwner.lifecycle } returns lifecycle

        val command = LifecycleAwareCommand<ParameterType>()

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        command.observe(lifecycleOwner) { parameter: ParameterType? ->
            parameter?.test()
        }
        command.observe(lifecycleOwner) { parameter: ParameterType? ->
            parameter?.otherTest()
        }

        val data = mockk<ParameterType>()
        every { data.test() } just Runs
        every { data.otherTest() } just Runs

        command.execute(data)

        verify(exactly = 1) { data.test() }
        verify(exactly = 1) { data.otherTest() }
    }

    @Test
    fun testMultipleEvents() {
        val lifecycleOwner = mockk<LifecycleOwner>()
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        every { lifecycleOwner.lifecycle } returns lifecycle

        val command = LifecycleAwareCommand<ParameterType>()

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        command.observe(lifecycleOwner) { parameter: ParameterType? ->
            parameter?.test()
        }

        val data = mockk<ParameterType>()
        every { data.test() } just Runs

        command.execute(data)
        command.execute(data)

        verify(exactly = 2) { data.test() }
    }
}