# TOTP Authenticator

A secure, modern TOTP (Time-based One-Time Password) authenticator app for Android, built with Jetpack Compose and following MVVM architecture principles.

## Features

- **Secure TOTP Generation**: Generate time-based one-time passwords using industry-standard algorithms (SHA1, SHA256, SHA512)
- **QR Code Scanning**: Easily add accounts by scanning QR codes from authenticator apps
- **Manual Entry**: Add accounts manually with custom settings
- **Encrypted Storage**: All secrets are encrypted using Android's EncryptedSharedPreferences
- **Auto-refresh**: TOTP codes automatically refresh every 30 seconds
- **Modern UI**: Clean, Material 3 design with dark mode support
- **Settings**: Customizable preferences including dark mode, haptic feedback, and refresh intervals
- **Clipboard Integration**: Copy codes to clipboard with haptic feedback

## Architecture

The app follows clean architecture principles with:

- **MVVM Pattern**: Separation of concerns with ViewModels handling business logic
- **Repository Pattern**: Centralized data access layer
- **Dependency Injection**: Using Hilt for clean dependency management
- **Room Database**: Local data persistence with encrypted storage
- **Jetpack Compose**: Modern declarative UI framework

## Tech Stack

- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Room**: Local database
- **Hilt**: Dependency injection
- **Navigation Compose**: Navigation between screens
- **CameraX**: QR code scanning
- **ZXing**: QR code processing
- **Coroutines**: Asynchronous programming
- **Material 3**: Design system

## Project Structure

```
app/src/main/java/com/test/totp/
├── data/
│   ├── dao/                 # Data Access Objects
│   ├── database/           # Room database
│   ├── model/              # Data models
│   ├── repository/         # Repository implementations
│   └── security/           # Encryption services
├── domain/
│   └── service/            # Business logic services
├── presentation/
│   ├── components/         # Reusable UI components
│   ├── navigation/        # Navigation setup
│   ├── screen/           # Screen composables
│   └── viewmodel/        # ViewModels
├── di/                    # Dependency injection modules
└── TotpApplication.kt     # Application class
```

## Key Components

### Data Layer
- **TotpAccount**: Entity representing a TOTP account
- **UserPreferences**: User settings and preferences
- **EncryptionService**: Handles secure storage of secrets
- **TotpRepository**: Centralized data access

### Domain Layer
- **TotpService**: TOTP code generation using RFC 6238
- **QrCodeService**: QR code parsing and generation
- **ClipboardService**: Clipboard operations with haptic feedback

### Presentation Layer
- **MainScreen**: Displays all TOTP accounts
- **AddAccountScreen**: Add new accounts via QR or manual entry
- **SettingsScreen**: App preferences and configuration
- **TotpAccountCard**: Individual account display component

## Security Features

- **Encrypted Storage**: All secrets encrypted using Android's EncryptedSharedPreferences
- **Secure Key Generation**: Cryptographically secure random secret generation
- **No Network Access**: All operations performed locally
- **Memory Safety**: Secrets cleared from memory after use

## Usage

### Adding Accounts

1. **QR Code Scanning**: 
   - Tap the + button on the main screen
   - Select "Scan QR Code"
   - Point camera at the QR code from your authenticator app

2. **Manual Entry**:
   - Tap the + button on the main screen
   - Select "Enter Manually"
   - Fill in account details and secret key

### Using TOTP Codes

- Codes automatically refresh every 30 seconds
- Tap "Copy Code" to copy to clipboard
- Progress bar shows remaining time
- Haptic feedback on copy (if enabled in settings)

### Settings

- **Dark Mode**: Toggle between light and dark themes
- **Show Seconds**: Display countdown timer
- **Haptic Feedback**: Vibration when copying codes
- **Refresh Interval**: Customize auto-refresh timing

## Development

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 30+
- Kotlin 1.8+

### Building

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Build and run on device/emulator

### Testing

The project includes unit tests for:
- TOTP service functionality
- QR code parsing
- Repository operations
- Data model validation

Run tests with:
```bash
./gradlew test
```

## Dependencies

- **Jetpack Compose BOM**: UI framework
- **Room**: Database
- **Hilt**: Dependency injection
- **Navigation Compose**: Navigation
- **CameraX**: Camera functionality
- **ZXing**: QR code processing
- **Security Crypto**: Encrypted storage

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## Security Considerations

- Never log or expose secret keys
- Use encrypted storage for all sensitive data
- Implement proper key management
- Regular security audits recommended
- Follow OWASP mobile security guidelines
