package org.kotlin.di.resolvers

import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFails
import kotlin.test.expect

internal class FactoryResolverTest {

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `Factory resolver resolves with factory`() {
        val expectedValue = 3
        val factoryResolver = FactoryResolver<Int> {
            expectedValue
        }
        expect(expectedValue, {
            factoryResolver.resolve()
        })
    }

    @Test
    fun `Factory creates value only after resolve() call`() {
        val spy = makeSpyMock()
        val factoryResolver = FactoryResolver {
            spy.onFactory()
        }

        verify(inverse = true){
            spy.onFactory()
        }
        factoryResolver.resolve()
        verify {
            spy.onFactory()
        }
    }

    @Test
    fun `Not singleton resolver resolves different values after multiple resolve() calls`() {
        val callCount = 3
        val spy = makeSpyMock()
        val factoryResolver = FactoryResolver {
            spy.apply {
                onFactory()
            }
        }

        for (i in 0..callCount) {
            factoryResolver.resolve()
        }

        verify(atLeast = callCount) {
            spy.onFactory()
        }
    }

    private fun makeSpyMock(): SpyMock {
        val resolver: SpyMock = mockk<SpyMock>()
        every { resolver.onFactory() } returns Unit
        every { resolver.dispose() } returns Unit
        return resolver
    }
}

internal abstract class SpyMock {
    abstract fun onFactory()
    abstract fun dispose()
}
