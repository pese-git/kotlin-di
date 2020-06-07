package org.kotlin.di

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.kotlin.di.resolvers.Resolver
import kotlin.test.assertFails
import kotlin.test.expect

internal class DiContainerTest {
    @Before
    fun onStart() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Container bind() throws state error if it's already has resolver`() {
        val container = DiContainer()
        container.bind<Int>(Int::class).toResolver(makeResolver(5))
        assertFails {
            container.bind<Int>(Int::class).toResolver(makeResolver(3))
        }
    }

    @Test
    fun `Container resolves value after adding a dependency`() {
        val expectedValue = 3
        val container = DiContainer()
        container.bind<Int>(Int::class).toResolver(makeResolver(expectedValue))
        expect(expectedValue, {container.resolve<Int>(Int::class)})
    }

    @Test
    fun `Container throws state error if the value can't be resolved`() {
        val container = DiContainer()
        assertFails {
            container.resolve<Int>(Int::class)
        }
    }

    @Test
    fun `Container has() returns true if it has resolver`() {
        val container = DiContainer()
        container.bind<Int>(Int::class).toResolver(makeResolver(5))
        expect(true, {
            container.has<Int>(Int::class)
        })
    }

    @Test
    fun `Container has() returns false if it hasn't resolver`() {
        val container = DiContainer()
        expect(false, {
            container.has<Int>(Int::class)
        })
    }

    @Test
    fun `Container hasInTree() returns true if it has resolver`() {
        val container = DiContainer()
        container.bind<Int>(Int::class).toResolver(makeResolver(5))
        expect(true, {
            container.hasInTree<Int>(Int::class)
        })
    }

    @Test
    fun `Container hasInTree() returns false if it hasn't resolver`() {
        val container = DiContainer()
        expect(false, {
            container.hasInTree<Int>(Int::class)
        })
    }


    @Test
    fun `Container bind() throws state error (if it's parent already has a resolver)`() {
        val parentContainer = DiContainer()
        val container = DiContainer(parentContainer)

        parentContainer.bind<Int>(Int::class).toResolver(makeResolver(5))

        assertFails {
            container.bind<Int>(Int::class).toResolver(makeResolver(3))
        }
    }

    @Test
    fun `Container resolve() returns a value from parent container`() {
        val expectedValue = 5
        val parentContainer = DiContainer()
        val container = DiContainer(parentContainer)

        parentContainer.bind<Int>(Int::class).toResolver(makeResolver(expectedValue))

        expect(expectedValue, {
            container.resolve<Int>(Int::class)
        })
    }

    @Test
    fun `Container resolve() returns a  several value from parent container`() {
        val expectedIntValue = 5
        val expectedStringValue = "Hello world"
        val parentContainer = DiContainer()
        val container = DiContainer(parentContainer)

        parentContainer.bind<Int>(Int::class).toResolver(makeResolver(expectedIntValue))
        parentContainer.bind<String>(String::class).toResolver(makeResolver(expectedStringValue))

        expect(expectedIntValue, {
            container.resolve<Int>(Int::class)
        })

        expect(expectedStringValue, {
            container.resolve<String>(String::class)
        })
    }

    @Test
    fun `Container resolve() throws a state error if parent hasn't value too`() {
        val container = DiContainer(DiContainer())
        assertFails {
            container.resolve<Int>(Int::class)
        }
    }

    @Test
    fun `Container has() returns false if parent has a resolver`() {
        val parentContainer = DiContainer()
        val container = DiContainer(parentContainer)

        parentContainer.bind<Int>(Int::class).toResolver(makeResolver(5))

        expect(false, {
            container.has<Int>(Int::class)
        })
    }


    @Test
    fun `Container has() returns false if parent hasn't a resolver`() {
        val container = DiContainer(DiContainer())
        expect(false, {
            container.has<Int>(Int::class)
        })
    }

    @Test
    fun `Container hasInTree() returns true if parent has a resolver`() {
        val parentContainer = DiContainer()
        val container = DiContainer(parentContainer)

        parentContainer.bind<Int>(Int::class).toResolver(makeResolver(5))

        expect(true, {
            container.hasInTree<Int>(Int::class)
        })
    }


    private fun <T> makeResolver(expectedValue: T): Resolver<T> {
        val resolver = mockk<Resolver<T>>()
        every { resolver.resolve() } returns expectedValue
        return resolver
    }
}

