package org.kotlin.di.resolvers


/**
 * Разрешает зависимость для значения
 */
class ValueResolver<T>(
    private val _value: T
): Resolver<T>() {

    override fun resolve(): T {
        return _value
    }
}