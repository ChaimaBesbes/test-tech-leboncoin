# Android Recruitment Test App - Architecture & Implementation Documentation

This document outlines the architectural decisions, core logic, and libraries used to refactor and enhance this project.

## Architecture: Clean Architecture

I reorganized the project into a **Clean Architecture** structure to ensure separation of concerns, high testability, and scalability. The project is split into three main modules:

### 1. `:domain` (Domain Layer)
- **Role:** The core of the application. It contains the business rules and is completely independent of any Android framework or third-party libraries (Pure Kotlin).
- **Contents:**
  - **Models:** `Album`, `Result` (Sealed class for UI state: Loading, Success, Error).
  - **Repository Interfaces:** `AlbumRepository` defines the contract for data operations without knowing *how* they are implemented.
  - **Use Cases:** `GetAllAlbumsUseCase`, `GetAlbumUseCase`, `ToggleFavoriteUseCase`. These encapsulate single, specific business actions.

### 2. `:data` (Data Layer)
- **Role:** Responsible for fetching, caching, and mapping data. It implements the interfaces defined in the `:domain` layer.
- **Contents:**
  - **Network:** Retrofit API service (`AlbumApiService`) and DTOs (`AlbumDto`).
  - **Local Database:** Room database setup (`AppDatabase`, `AlbumDao`, `AlbumEntity`).
  - **Repositories:** `AlbumRepositoryImpl` which acts as the Single Source of Truth (SSOT), deciding whether to fetch data from the network or the local database.
  - **Utilities:** `NetworkMonitor` to check network availability before making HTTP requests.

### 3. `:app` (Presentation / UI Layer)
- **Role:** Handles the UI rendering and user interactions using Jetpack Compose and ViewModels.
- **Contents:**
  - **ViewModels:** `AlbumsViewModel`, `AlbumDetailViewModel`. They consume Use Cases and expose reactive `StateFlow`s to the UI.
  - **UI (Compose):** `AlbumsScreen`, `AlbumDetailScreen`, `AlbumItem`. 
  - **Navigation:** Type-safe Jetpack Navigation Compose defining routes like `AlbumsListRoute` and `AlbumDetailRoute`.
  - **Dependency Injection:** Manual DI container (`AppDependenciesProvider` and `DataDependencies`) to wire up the application globally.

---

## Core Logic Implemented

### 1. Offline-First Approach & Caching
Instead of waiting for a network request to fail, I implemented a proactive `NetworkMonitor` using Android's `ConnectivityManager`.
- **Online:** The repository fetches data from the API, preserves any existing favorite statuses, caches the new data into the Room Database, and returns it.
- **Offline:** The API call is skipped entirely, and the application instantly loads the cached `Album` list from the Room Database.

### 2. UI State Management
I created a `Result<T>` sealed class (`Loading`, `Success`, `Error`). The ViewModel initializes with `Result.Loading` and updates to `Success` once the UseCase returns data. The UI listens to this `StateFlow` and reactively switches between a loading spinner, an error text, or the main list.

### 3. Pull-to-Refresh & Network Events
- The list screen supports **Pull-to-Refresh** using Material 3's `PullToRefreshBox`.
- The ViewModel tracks the network state history. If the user refreshes while offline, an `offlineEvent` triggers a Toast (*"You are offline, fetching local data..."*). If the connection is restored during a refresh, an `onlineEvent` triggers a Toast (*"Connection is back!"*).

### 4. Favorites System
Users can favorite albums in the detail screen.
- **State Preservation:** When toggling a favorite, the `ToggleFavoriteUseCase` updates the Room database directly. When the app fetches fresh data from the API, the repository queries the database for existing favorites and merges that state into the incoming API data before saving it, ensuring user favorites are never overwritten by a network refresh.

---

## Libraries & Tools

- **Jetpack Compose:** The modern, declarative UI toolkit used for all UI components.
- **Compose Material 3:** Used for structural elements like `Scaffold`, `PullToRefreshBox`, and basic typography.
- **Spark Design System (`com.adevinta.spark`):** A custom design system used for styled components like `IconButtonGhost`, `IconToggleButtonGhost`, and `ChipTinted`.
- **Jetpack Navigation Compose:** Configured with the latest **Type-Safe Routing** (using `kotlinx-serialization` to pass data objects like `AlbumDetailRoute` instead of raw strings).
- **Room:** SQLite object mapping library used for the local offline cache and storing the "favorite" state of albums.
- **Retrofit & Kotlinx Serialization:** Used for type-safe REST API communication and JSON parsing.
- **Kotlin Coroutines & Flow:** Used heavily across all layers for asynchronous programming. `StateFlow` for state management, `SharedFlow` for one-time UI events (like Toasts).
- **Coil:** Used (`AsyncImage`) for asynchronous, cached image loading on both the list and detail screens.
- **Manual Dependency Injection:** I utilized a structured Manual DI pattern (via Application class and Factory providers) for maximum build speed and complete control over the graph.