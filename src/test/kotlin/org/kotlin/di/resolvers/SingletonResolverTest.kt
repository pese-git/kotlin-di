package org.kotlin.di.resolvers

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class SingletonResolverTest {

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `Singleton resolver resolves same value after multiple resolve() calls`() {
        val callCount = 3
        val spy = makeSpyMock()
        val singletonResolver = SingletonResolver(
            FactoryResolver{
                spy.apply {
                    onFactory()
                }
            }
        )

        for (i in 0..callCount) {
            singletonResolver.resolve()
        }

        verify(atLeast = 1) {
            spy.onFactory()
        }
    }

    private fun makeSpyMock(): SSpyMock {
        val resolver: SSpyMock = mockk<SSpyMock>()
        every { resolver.onFactory() } returns Unit
        every { resolver.dispose() } returns Unit
        return resolver
    }
}

internal abstract class SSpyMock {
    abstract fun onFactory()
    abstract fun dispose()
}