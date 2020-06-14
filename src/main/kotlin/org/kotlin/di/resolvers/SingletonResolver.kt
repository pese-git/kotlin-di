package org.kotlin.di.resolvers

class SingletonResolver<T>(
        private val _decoratedResolver: Resolver<T>
): Resolver<T> {
    private var _value: T? = null

    override fun resolve(): T {
        if (_value == null) {
            _value = _decoratedResolver.resolve()
        }
        return _value!!
    }
}