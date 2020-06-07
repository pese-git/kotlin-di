package org.kotlin.di.resolvers

/**
 * Resolver - это абстракция, которая определяет,
 * как контейнер будет разрешать зависимость
 */
abstract class Resolver<T> {
    /**
     * Разрешает зависимость типа [T]
     * @return - возвращает объект типа [T]
     */
    abstract fun resolve(): T
}