package org.kotlin.di.resolvers

import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Test
import kotlin.test.expect

internal class ValueResolverTest {
    @Before
    fun onStart() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `Value resolver resolves with selected value`() {
        val a = 3
        val valueResolver = ValueResolver<Int>(a)
        expect(a, {
            valueResolver.resolve()
        })
    }
}