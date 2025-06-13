# Stock Screener Android App

A modern Android stock screening application built with Kotlin that allows users to browse, search, and favorite stocks with price information and market indicators.

## Features

-  **Real-time Stock Data**: Browse trending stocks with current prices and market changes
-  **Search Functionality**: Search for stocks by symbol or company name
-  **Favorites System**: Add/remove stocks to your personal watchlist
-  **Price Indicators**: Visual indicators for price changes (green for gains, red for losses)
-  **Market Status**: Live market open/close status indicator
-  **Clean UI**: Modern Material Design interface optimized for mobile

## Screenshots

<img src="https://github.com/user-attachments/assets/21800204-4233-4a26-bc7c-0de84887fdfd" width="200" alt="Main Screen">
<img src="https://github.com/user-attachments/assets/23333dd0-659c-4d0d-995b-86b400f2b5dd" width="200" alt="Search Feature">
<img src="https://github.com/user-attachments/assets/9059ac1e-7dc9-4db1-813f-fa8f2de1ec67" width="200" alt="Favorites Screen">

## Setup Instructions

### Prerequisites

- Android Studio 
- Android SDK API level 24 or higher
- Kotlin 1.8.0 or later
- Gradle 7.0 or later

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/kamaliabadrina/StockScreener.git
   cd StockScreener
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned directory and select it

3. **Sync Project**
   - Android Studio will automatically sync the project
   - Wait for Gradle sync to complete

4. **Run the Application**
   - Connect an Android device or start an emulator
   - Click the "Run" button 

## App Structure

### Architecture Pattern
The app follows **MVVM (Model-View-ViewModel)** architecture pattern with the following components:

```
app/
├── src/main/
│   ├── assets/
│   │   └── stocks.json                 # Local stock data
│   ├── java/com/example/stockscreener/
│   │   ├── model/
│   │   │   └── Stock.kt               # Data model for stock information
│   │   ├── utils/
│   │   │   └── FavoritesManager.kt    # Utility for managing favorites
│   │   ├── viewmodel/
│   │   │   └── MainViewModel.kt       # ViewModel for business logic
│   │   ├── FavoritesActivity.kt       # Favorites screen activity
│   │   ├── MainActivity.kt            # Main screen activity
│   │   └── StockAdapter.kt            # RecyclerView adapter
│   └── res/
│       ├── drawable/                  # Vector graphics and backgrounds
│       └── layout/                    # XML layout files
```

### Key Components

#### 1. **Data Layer**
- `Stock.kt`: Data model representing stock information including price, changes, and metadata
- `stocks.json`: Local JSON file containing stock data

#### 2. **Business Logic Layer**
- `MainViewModel.kt`: Handles business logic, data processing, and state management
- `FavoritesManager.kt`: Manages favorite stocks using SharedPreferences for persistence

#### 3. **Presentation Layer**
- `MainActivity.kt`: Main screen displaying stock list and search functionality
- `FavoritesActivity.kt`: Dedicated screen for user's favorite stocks
- `StockAdapter.kt`: RecyclerView adapter for displaying stock items

#### 4. **UI Components**
- `header_layouts.xml`: Custom header with market status indicator
- `item_stock.xml`: Individual stock item layout with price and change indicators
- Material Design drawables for consistent visual styling


## Trade-offs and Design Decisions

### 1. **Local Data vs API Integration**
- **Current**: Uses local JSON file for stock data
- **Trade-off**: Faster loading and offline capability vs real-time data
- **Future**: Integrate with financial APIs (Alpha Vantage, Yahoo Finance, etc.)

### 2. **Logo URL Updates**
- **Decision**: Replaced deprecated logo URLs with working alternatives
- **Reasoning**: Ensures proper image loading and better user experience
- **Impact**: Maintained API structure while improving functionality


### 3. **Architecture Choice**
- **Selected**: MVVM with Repository pattern principles
- **Benefits**: Separation of concerns, testability, maintainability
- **Trade-off**: Slightly more complex for simple app vs better scalability

## Future Improvements

- [ ] **Real-time Data Integration**: Connect to live stock APIs
- [ ] **Pull-to-refresh**: Add refresh functionality for updated prices
- [ ] **Dark Mode**: Implement theme switching
- [ ] **Portfolio Tracking**: Allow users to track owned stocks and performance
- [ ] **Cloud Sync**: Sync favorites across devices
- [ ] **AI-powered Insights**: Smart recommendations based on user behavior


## Contact

**Developer**: Kamalia Badrina
**Email**: badrinakamalia@gmail.com 
**GitHub**: [@kamaliabadrina](https://github.com/kamaliabadrina)
