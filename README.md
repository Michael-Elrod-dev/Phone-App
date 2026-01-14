# Bill Tracker - Native Android App

A native Android application for tracking recurring bills and managing pay days, built with Kotlin and Jetpack Compose.

## Features

- **Calendar View**: Visual monthly calendar showing bills and pay days
- **Bill Management**: Add, edit, and delete recurring or one-time bills
- **Pay Day Tracking**: Configure multiple pay days with income amounts
- **Financial Summary**: Real-time calculation of income, bills, and remaining balance
- **AutoPay Support**: Mark bills as autopay for easy tracking
- **Material Design 3**: Modern Android UI with Material You theming
- **Offline-First**: All data stored locally using Room database

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **State Management**: StateFlow
- **Navigation**: Jetpack Navigation Compose
- **Minimum Android Version**: API 24 (Android 7.0)

## Project Structure

```
app/src/main/java/com/mselrod/billtracker/
├── data/
│   ├── entity/       # Database entities (Bill, PayDay)
│   ├── dao/          # Data Access Objects
│   ├── database/     # Room database configuration
│   └── repository/   # Repository layer
├── ui/
│   ├── screens/      # Main screens (MainScreen, AddEditBillScreen, PayDaysScreen)
│   ├── components/   # Reusable UI components (BillCard, CalendarView)
│   └── theme/        # Material3 theme configuration
├── viewmodel/        # BillViewModel and ViewModelFactory
└── MainActivity.kt   # App entry point
```

## Building the App

### Prerequisites
- Android Studio (latest version)
- JDK 11 or higher
- Android SDK API 24 or higher

### Steps

1. **Open in Android Studio**
   - File → Open → Select this project folder
   - Wait for Gradle sync to complete

2. **Run on Device/Emulator**
   - Connect Android device via USB with USB debugging enabled, OR
   - Start an Android emulator from AVD Manager
   - Click the green "Run" button (or Shift+F10)

3. **Build APK for Sideloading**
   - Build → Build Bundle(s)/APK(s) → Build APK(s)
   - APK will be in `app/build/outputs/apk/debug/`
   - Transfer to phone and install

4. **Build Signed Release**
   - Build → Generate Signed Bundle/APK
   - Follow prompts to create/select keystore
   - Choose "release" build variant
   - APK/AAB will be in `app/build/outputs/`

## Installing on Your Phone

### For Testing (Debug APK)
1. Build debug APK as shown above
2. Transfer APK to phone via USB, email, or cloud storage
3. On phone: Settings → Security → Enable "Install from Unknown Sources"
4. Open the APK file on phone to install

### For Play Store
1. Build signed App Bundle (.aab file)
2. Log into Google Play Console
3. Create new app or select existing
4. Upload .aab file under "Release" section
5. Complete store listing and submit for review

## Development Notes

- Data persists in local SQLite database
- Bills are stored with day-of-month or specific dates
- Pay days show as colored dots on calendar
- Undo functionality for deleted bills
- Form validation on all inputs

## License

Personal project - All rights reserved
