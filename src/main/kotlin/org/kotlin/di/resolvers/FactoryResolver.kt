package org.kotlin.di.resolvers

/**
 * Разрешает зависимость для фабричной функции
 */
class FactoryResolver<T>(
    private val _factory: () -> T
): Resolver<T> {
    override fun resolve(): T {
        return _factory.invoke()
    }
}