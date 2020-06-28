package org.kotlin.di

import org.kotlin.di.resolvers.ResolvingContext
import kotlin.reflect.KClass

/**
 * Контейнер - это объект, которой хранит все резолверы зависимостей.
 */
class DiContainer(
        private val _parent: DiContainer? = null
) {
    private val _resolvers = mutableMapOf<KClass<*>, ResolvingContext<*>?>()

    /**
     * Добавляет resolver зависимостей типа [T] в контейнер.
     * Обратите внимание, что перезапись значений внутри одного контейнера запрещена.
     * @return - возвращает [ResolvingContext] или [RuntimeException]
     */
    fun <T : Any> bind(cType: KClass<T>): ResolvingContext<T> {
        val context = ResolvingContext<T>(this)
        if (hasInTree<T>(cType)) {
            throw RuntimeException("Dependency of type `${cType}` is already exist in containers tree")
        }

        _resolvers[cType] = context
        return context
    }

    /**
     * Возвращает разрешенную зависимость, определенную параметром типа [T].
     * Выдает [RuntimeException], если зависимость не может быть разрешена.
     * Если вы хотите получить [null], если зависимость не может быть разрешена,
     * то используйте вместо этого [tryResolve]
     * @return - возвращает объект типа [T]  или [RuntimeException]
     */
    fun <T : Any> resolve(cType: KClass<T>): T {
        val resolved = tryResolve<T>(cType)
        if (resolved != null) {
            return resolved
        } else {
            throw RuntimeException("Can\\'t resolve dependency `${cType}`. \n" + "Maybe you forget register it?")
        }
    }


    /**
     * Возвращает разрешенную зависимость типа [T] или null, если она не может быть разрешена.
     */
    fun <T : Any> tryResolve(cType: KClass<T>): T? {
        val resolver = _resolvers[cType]
        return if (resolver != null) {
            resolver.resolve() as T?
        } else {
            _parent?.tryResolve(cType)
        }
    }

    /**
     * Возвращает true, если у этого контейнера есть средство разрешения зависимостей для типа [T].
     * Если вы хотите проверить его для всего дерева контейнеров, используйте вместо него [hasInTree].
     * @return - возвращает булево значение
     */
    fun <T : Any> has(cType: KClass<T>): Boolean {
        return _resolvers.containsKey(cType)
    }

    /**
     * Возвращает true, если контейнер или его родители содержат средство разрешения зависимостей для типа [T].
     * Если вы хотите проверить его только для этого контейнера, используйте вместо него [has].
     * @return - возвращает булево значение
     */
    fun <T : Any> hasInTree(cType: KClass<T>): Boolean {
        return has<T>(cType) || (_parent != null && _parent.hasInTree<T>(cType))
    }
}