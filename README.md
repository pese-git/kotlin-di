# kotlin-di

Экспериментальная разработка DI на ЯП Kotlin

## Документация

### Быстрый старт

Основным классом для всех операций является `DiContainer`. Вы можете зарегистрировать свои зависимости,
получив `ResolvingContext` через метод `bind<T>()` и используя его различные методы разрешений.
Далее вы можете получить зависимости с помощью `resolve<T>()`.

Пример:

```kotlin
val container = DiContainer()
container.bind<SomeService>(SomeService::class).toValue(SomeServiceImpl())
/*
...
 */

// Метод `resolve` просто возвращает зарегистрированный ранее экземпляр
val someService = container.resolve<SomeService>(SomeService::class)
```

### Ленивая инициализация

Если вам нужно создать объект в момент резолвинга, вы можете использовать ленивую (другими словами, по запросу) инициализацию объекта 
с помощью метода `toFactoryN()`.

Пример:

```kotlin
val container = DiContainer()
// В методе `toFactory` вы просто определяете, как построить экземпляр через фабричную лямбду
container.bind<SomeService>(SomeService::class).toFactory{ SomeServiceImpl() }
/*
...
 */
// Метод `resolve()` будет создавать экземпляр через зарегистрированную фабричную лямбду каждый раз, когда вы вызываете его
val someService = container.resolve<SomeService>(SomeService::class)
val anotherSomeService = container.resolve<SomeService>(SomeService::class)
assert(someService != anotherSomeService);
```

Но обычно у вас есть много типов с разными зависимостями, которые образуют граф зависимостей.

Пример:

```kotlin
class A {}
class B {}

class C(val a: A, val b: B)
```


Если вам нужно зарегистрировать некоторый тип, зависящий от других типов из контейнера,
вы можете использовать методы `toFactory1<T1>` - `toFactory8<T1 ... T8>`, где число в конце,
является количеством запрошенных через аргументы типов зависимостей.
(Обратите внимание, что вам нужно определить все зависимости в аргументах - `toFactory2<A1, A2>`).


Пример:

```kotlin
class SomeService(
  val a :A,
  val b :B
)

val container = DiContainer()
container.bind<A>(A::class).toFactory {A()}
container.bind<B>(B::class).toFactory {B()}

/// В фабричной лямбде вы определяете, как построить зависимость от других зависимостей
/// (Порядок разрешенных экземпляров соответствует порядку типов аргументов)
container.bind<SomeService>(SomeService::class).toFactory2<A, B>{a, b -> SomeService(a, b)}

/*
...
 */

/// Получаем экземпляр `SomeService` через resolve своих зависимостей.
/// В нашем случае - это resolve A и B
/// Внимание!!! То, что он будет создавать новые экземпляры A и B каждый раз, когда вы вызываете `resolve` SomeService
val someService = container.resolve<SomeService>(SomeService::class)
```

### Время жизни экземпляров и контроль области видимости

Если вы хотите создать экземпляр зарегистрированной зависимости только один раз,
и вам нужно получить/разрешить зависимость много раз в контейнере, то вы можете зарегистрировать
свою зависимость с добавлением `asSingeton()`. Например:

```kotlin
val container = DiContainer()
container.bind<A>(A::class)
  .toFactory{ A() }
  .asSingleton()

container
  .bind<B>(B::class)
  .toFactory{ B() }
  .asSingleton()

container.bind<SomeService>(SomeService::class).toFactory2<A, B>{(a, b) -> SomeService(a, b)}

// Код выше означает: Контейнер, регистрирует создание A и B только в первый раз, когда оно будет запрошен,
// и регистрирует создание SomeService каждый раз, когда оно будет запрошен.

val a = container.resolve<A>(A::class)
val b = container.resolve<B>(B::class)
val anotherA = container.resolve<A>(A::class)
val anotherB = container.resolve<B>(B::class)

assert(a == anotherA && b == anotherB);

val someService = container.resolve<SomeService>(SomeService::class)
val anotherSomeService = container.resolve<SomeService>(SomeService::class)

assert(someService != anotherSomeService);
```

Если вы хотите сразу создать свой зарегистрированный экземпляр, вы можете вызвать `resolve()`. Например:


```kotlin
val container = DiContainer();
// Это заставит создать зависимость после регистрации
container.bind <SomeService>(SomeService::class)
  .toFactory{ SomeService() }
  .asSingleton()
  .resolve()
```

Когда вы работаете со сложным приложением, в большинстве случаев вы можете работать со многими модулями с собственными зависимостями.
Эти модули могут быть настроены различными `DiContainer`-ми. И вы можете прикрепить контейнер к другому, как родительский.
В этом случае родительские зависимости будут видны для дочернего контейнера,
и через него вы можете формировать различные области видимости зависимостей. Например:

```kotlin
val parentContainer = DiContainer()
parentContainer.bind<A>(A::class).toFactory{ A() }

val childContainer =  DiContainer(parentContainer)
// Обратите внимание, что родительская зависимость A видна для дочернего контейнера
val a = childContainer.resolve<A>(A::class)

/*
// Но следующий код потерпит неудачу с ошибкой, потому что родитель не знает о своем потомке.
val parentContainer = DiContainer()
val childContainer = DiContainer()
childContainer.bind<A>(A::class).toFactory{ A() }

// Выдает ошибку
val a = parentContainer.resolve<A>(A::class)
 */
```

### Структура библиотеки

Библиотека состоит из DiContainer и Resolver. 
DiContainer - это контейнер со всеми Resolver для разных типов. А `Resolver` - это просто объект, который знает, как разрешить данный тип.
Многие из resolver-ов обернуты другими, поэтому они могут быть составлены для разных вариантов использования.
Resolver - интерфейс, поэтому он имеет много реализаций. Основным является ResolvingContext. 
Вы можете думать об этом как об объекте контекста, который имеет вспомогательные методы для создания различных вариантов  resolver-ов (`toFactory`,` toValue`, `asSingleton`).
Но все они просто используют метод `toResolver` для определения некоторого корневого resolver в контексте.
Когда вы запрашиваете тип из контейнера с помощью метода `resolve<T>()`, он просто находит контекст для типа и вызывает корневой resolver, который может вызывать другие resolver-ы.


Пример (из ```example```): 

```kotlin
package org.kotlin.di.example

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.kotlin.di.DiContainer


fun main(args: Array<String>)= runBlocking {
    val container = DiContainer()
    container.bind<String>(String::class).toValue("279f7923kf23fs")
    container.bind<ApiClient>(ApiClient::class).toValue(ApiClientMock())
    container.bind<DataRepository>(DataRepository::class).toFactory2<String, ApiClient> { it1, it2 ->
        NetworkDataRepository(
                token = it1,
                apiClient = it2
        )
    }

    val dataRepository = container.resolve<DataRepository>(DataRepository::class)

    dataRepository.getData().collect {
        println(it)
    }
}

interface DataRepository {
    fun getData(): Flow<String>
}

class NetworkDataRepository(
        private val token: String,
        private val apiClient: ApiClient
): DataRepository {

    override fun getData(): Flow<String> {
        return apiClient.sendRequest(
                url = "www.google.com",
                token = token,
                requestBody = mapOf("type" to "string")
        )
    }
}



interface ApiClient {
    fun sendRequest(url: String, token: String, requestBody: Map<String, Any>): Flow<String>
}

class ApiClientMock: ApiClient {
    override fun sendRequest(url: String, token: String, requestBody: Map<String, Any>): Flow<String> {
        return flowOf("Answer: $url $token $requestBody")
    }
}
```