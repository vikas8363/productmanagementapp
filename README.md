# Product Management App

A native Android application designed to simplify product inventory management through a clean user interface and structured MVVM architecture.

## Features

- **Product Management:** Add, view, update, and delete products.
- **Dynamic Price Calculation:** Automatically calculates total price based on product cost and quantity.
- **Product Sorting:** Sort products by name and price in ascending or descending order.
- **Inventory Summary:** Displays total product count and overall inventory value.
- **Local Data Persistence:** Stores product information using Room Database.
- **Input Validation:** Validates product details and handles invalid inputs.
- **Confirmation Dialogs:** Provides confirmation before deleting products.
- **User Feedback:** Displays success messages and error dialogs.
- **Persistent Inventory:** Retains product data across application restarts.

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM
- **Database:** Room Database
- **Asynchronous Programming:** Kotlin Coroutines
- **UI:** XML, Material Components
- **Dependency Injection:** Dagger Hilt
- **Navigation:** Android Navigation Component
- **Data Binding:** ViewBinding and DataBinding
- **Observable Data:** LiveData

## Architecture

The application follows the Model-View-ViewModel (MVVM) architectural pattern to maintain separation of concerns, improve code organization, and simplify maintenance.

## Key Highlights

- Reactive UI updates using LiveData and Room.
- Centralized dialog management.
- Efficient local database operations.
- Modular and maintainable project structure.
- Clean and user-friendly inventory management experience.
