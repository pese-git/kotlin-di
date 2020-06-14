package org.kotlin.di.resolvers

import org.kotlin.di.DiContainer
import java.lang.RuntimeException

class ResolvingContext<T>(
        val _container: DiContainer
) : Resolver<T>() {
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
    fun <TImpl : T> toResolver(resolver: Resolver<TImpl>): ResolvingContext<T> {
        _resolver = resolver
        return this
    }

    /**
     *  Создать резолвер значения
     */
    fun <TImpl : T> toValue(value: TImpl): ResolvingContext<T> {
        val resolver: Resolver<TImpl> = ValueResolver<TImpl>(value)
        return toResolver<TImpl>(resolver = resolver)
    }

    /**
     * Преобразователь в сингелтон
     */
    fun <TImpl : T> asSingleton(): ResolvingContext<T> {
        val resolver: Resolver<TImpl> = SingletonResolver<TImpl>(
                _decoratedResolver = _resolver as Resolver<TImpl>
        )
        return toResolver<TImpl>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver без каких-либо зависимостей
     */
    fun <TImpl : T> from(factory: () -> TImpl): ResolvingContext<T> {
        val resolver: Resolver<TImpl> = FactoryResolver<TImpl>(factory)
        return toResolver<TImpl>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver с 1 зависимостью от контейнера
     */
    inline fun <reified T1: Any> from1(crossinline factory: (T1) -> T): ResolvingContext<T> {
        val resolver = FactoryResolver {
            factory(_container.resolve(T1::class))
        }
        return toResolver<T>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver с 2 зависимостью от контейнера
     */
    inline fun <reified T1: Any, reified T2: Any> from2(crossinline factory: (T1, T2) -> T): ResolvingContext<T> {
        val resolver = FactoryResolver {
            factory(
                    _container.resolve(T1::class),
                    _container.resolve(T2::class)
            )
        }
        return toResolver<T>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver с 3 зависимостью от контейнера
     */
    inline fun <reified T1: Any, reified T2: Any, reified T3: Any> from3(crossinline factory: (T1, T2, T3) -> T): ResolvingContext<T> {
        val resolver = FactoryResolver {
            factory(
                    _container.resolve(T1::class),
                    _container.resolve(T2::class),
                    _container.resolve(T3::class)
            )
        }
        return toResolver<T>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver с 4 зависимостью от контейнера
     */
    inline fun <reified T1: Any, reified T2: Any, reified T3: Any, reified T4: Any> from4(crossinline factory: (T1, T2, T3, T4) -> T): ResolvingContext<T> {
        val resolver = FactoryResolver {
            factory(
                    _container.resolve(T1::class),
                    _container.resolve(T2::class),
                    _container.resolve(T3::class),
                    _container.resolve(T4::class)
            )
        }
        return toResolver<T>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver с 5 зависимостью от контейнера
     */
    inline fun <reified T1: Any, reified T2: Any, reified T3: Any, reified T4: Any, reified T5: Any> from5(crossinline factory: (T1, T2, T3, T4, T5) -> T): ResolvingContext<T> {
        val resolver = FactoryResolver {
            factory(
                    _container.resolve(T1::class),
                    _container.resolve(T2::class),
                    _container.resolve(T3::class),
                    _container.resolve(T4::class),
                    _container.resolve(T5::class)
            )
        }
        return toResolver<T>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver с 6 зависимостью от контейнера
     */
    inline fun <reified T1: Any, reified T2: Any, reified T3: Any, reified T4: Any, reified T5: Any, reified T6: Any> from6(crossinline factory: (T1, T2, T3, T4, T5, T6) -> T): ResolvingContext<T> {
        val resolver = FactoryResolver {
            factory(
                    _container.resolve(T1::class),
                    _container.resolve(T2::class),
                    _container.resolve(T3::class),
                    _container.resolve(T4::class),
                    _container.resolve(T5::class),
                    _container.resolve(T6::class)
            )
        }
        return toResolver<T>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver с 7 зависимостью от контейнера
     */
    inline fun <reified T1: Any, reified T2: Any, reified T3: Any, reified T4: Any, reified T5: Any, reified T6: Any, reified T7: Any> from7(crossinline factory: (T1, T2, T3, T4, T5, T6, T7) -> T): ResolvingContext<T> {
        val resolver = FactoryResolver {
            factory(
                    _container.resolve(T1::class),
                    _container.resolve(T2::class),
                    _container.resolve(T3::class),
                    _container.resolve(T4::class),
                    _container.resolve(T5::class),
                    _container.resolve(T6::class),
                    _container.resolve(T7::class)
            )
        }
        return toResolver<T>(resolver = resolver)
    }

    /**
     * Создать фабричный resolver с 8 зависимостью от контейнера
     */
    inline fun <reified T1: Any, reified T2: Any, reified T3: Any, reified T4: Any, reified T5: Any, reified T6: Any, reified T7: Any, reified T8: Any> from8(crossinline factory: (T1, T2, T3, T4, T5, T6, T7, T8) -> T): ResolvingContext<T> {
        val resolver = FactoryResolver {
            factory(
                    _container.resolve(T1::class),
                    _container.resolve(T2::class),
                    _container.resolve(T3::class),
                    _container.resolve(T4::class),
                    _container.resolve(T5::class),
                    _container.resolve(T6::class),
                    _container.resolve(T7::class),
                    _container.resolve(T8::class)
            )
        }
        return toResolver<T>(resolver = resolver)
    }

    private fun verify() {
        if (_resolver == null) {
            throw RuntimeException("Can\'t resolve T without any resolvers. " +
                    "Please check, may be you didn\'t do anything after bind()")
        }
    }
}