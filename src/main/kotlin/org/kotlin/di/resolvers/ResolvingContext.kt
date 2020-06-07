package org.kotlin.di.resolvers

import org.kotlin.di.DiContainer
import java.lang.RuntimeException

class ResolvingContext<T>(
        private val _container: DiContainer
): Resolver<T>() {
    // Корневой резолвер
    private var _resolver: Resolver<*>? = null


    /**
     * Разрешает зависимость типа [T]
     * @return - возвращает объект типа [T]
     */
    override fun resolve(): T {
        verify()
        return _resolver?.resolve() as T
    }

    /**
     * Добавляет резолвер в качестве корневого резолвера
     * С помощью этого метода вы можете добавить любой
     * пользовательский резолвер
     */
    fun <TImpl: T> toResolver(resolver: Resolver<TImpl>): ResolvingContext<T> {
        _resolver = resolver
        return this
    }

    private fun verify() {
        if (_resolver == null) {
            throw RuntimeException("Can\'t resolve T without any resolvers. " +
                    "Please check, may be you didn\'t do anything after bind()")
        }
    }
}