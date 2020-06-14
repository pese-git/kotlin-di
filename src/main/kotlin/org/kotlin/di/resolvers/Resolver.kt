package org.kotlin.di.resolvers

/**
 * Resolver - это абстракция, которая определяет,
 * как контейнер будет разрешать зависимость
 */
interface Resolver<T> {
    /**
     * Разрешает зависимость типа [T]
     * @return - возвращает объект типа [T]
     */
    fun resolve(): T
}