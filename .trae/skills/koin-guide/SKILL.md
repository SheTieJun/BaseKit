---
name: "koin-guide"
description: "Expert guide for Koin dependency injection in Kotlin/Android. Invoke when user asks about Koin setup, modules, injection (single/factory/viewModel), or replacing Dagger."
---

# Koin Usage Guide

This skill provides a comprehensive guide to using Koin, a lightweight dependency injection framework for Kotlin developers.

## 🚀 Why Koin?
-   **Lightweight**: No proxy, no code generation, no reflection.
-   **Kotlin-First**: Written in pure Kotlin.
-   **Simple**: Easy to learn compared to Dagger/Hilt.

## 📦 Setup (Gradle)

```groovy
dependencies {
    // Core
    implementation "io.insert-koin:koin-android:$koin_version"
    // Scope features
    implementation "io.insert-koin:koin-androidx-scope:$koin_version"
    // ViewModel features
    implementation "io.insert-koin:koin-androidx-viewmodel:$koin_version"
}
```

## 🏁 Initialization

Initialize Koin in your `Application` class:

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger() // Optional: Logger
            androidContext(this@MyApp) // Inject Android Context
            modules(appModule, netModule) // Load Modules
        }
    }
}
```

## 🧩 Defining Modules

Use `module { }` to define your dependencies.

### 1. Factory (New Instance Every Time)
Equivalent to `new Class()`.

```kotlin
val appModule = module {
    factory { Person() }
}
```

### 2. Single (Singleton)
Created once and shared.

```kotlin
val appModule = module {
    single { NetworkManager() }
}
```

### 3. ViewModel
Special factory for Android ViewModels.

```kotlin
val appModule = module {
    viewModel { MyViewModel(get()) }
}
```

## 💉 Injection

### In Activity/Fragment
Use `by inject()` (lazy) or `get()` (eager).

```kotlin
class MainActivity : AppCompatActivity() {
    // Lazy injection
    val person: Person by inject()
    val viewModel: MyViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Eager injection
        val manager: NetworkManager = get()
    }
}
```

### In Non-Android Classes (KoinComponent)
Implement `KoinComponent` interface.

```kotlin
class Repository : KoinComponent {
    val api: ApiService by inject()
}
```

## 🔧 Advanced Usage

### Parameters (Injection with Args)
Pass arguments at runtime.

**Module Definition:**
```kotlin
factory { (id: String, name: String) -> User(id, name) }
```

**Injection:**
```kotlin
val user: User by inject { parametersOf("123", "Alice") }
```

### Qualifiers (Named Injection)
Distinguish between multiple instances of the same type.

**Module Definition:**
```kotlin
single(named("local")) { LocalDataSource() }
single(named("remote")) { RemoteDataSource() }
```

**Injection:**
```kotlin
val local: DataSource by inject(named("local"))
```

### Scopes
Bind dependencies to a specific lifecycle (e.g., Activity or Fragment).

```kotlin
val scopeModule = module {
    scope<DetailActivity> {
        scoped { Presenter() }
    }
}
```

Inside `DetailActivity`:
```kotlin
// Automatically bound to this activity's scope
val presenter: Presenter by inject() 
```

## 📋 Summary of DSL Keywords
-   `module { }`: Declare a Koin module.
-   `factory { }`: Create a new instance each time.
-   `single { }`: Create a singleton.
-   `viewModel { }`: Create a ViewModel.
-   `get()`: Resolve a component dependency.
-   `named("name")`: Add a qualifier.
-   `scope<T> { }`: Define a scope for a class.
