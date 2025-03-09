# MediCare - Android App

## Overview
MediCare is an Android application designed to enhance user interaction with medical devices using Augmented Reality (AR). The app allows users to capture or select images for analysis and view the results in an AR interface. It is built with Jetpack Compose for UI design and integrates camera and gallery functionalities.

## Features
- Capture photos using the device's camera.
- Select images from the gallery.
- Display the selected/captured image in the UI.
- Perform mock analysis on the image.
- View analysis results in an AR interface (Placeholder for now).

## Tech Stack
- **Kotlin**
- **Jetpack Compose**
- **Material 3D Design**
- **Coil for image loading**
- **AndroidX Activity Result API**
- **FileProvider for secure file sharing**
- **TensorFlow Lite (Planned for Image Detection and Analysis)**
- **ARCore (Planned for AR Integration)**

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/Utkarzxhh/Bot-Coders-
   ```
2. Open the project in **Android Studio**.
3. Build and run the app on an emulator or a physical device.

## Permissions Required
The app requires the following permissions:
- **Camera Permission** (`Manifest.permission.CAMERA`) for taking photos.
- **Storage Access** for selecting images from the gallery.

## How to Use
1. Click on **"Select or Take Photo"** button.
2. Choose between:
   - **Take Photo** (Requires Camera permission)
   - **Pick from Gallery**
3. Once an image is selected/captured, it is displayed in the UI.
4. The app runs a mock analysis and shows the result.
5. Click **"View in AR"** to navigate to the AR interface (Feature Placeholder).

## Project Structure
```
MediCare/
│-- MainActivity.kt
│-- PhotoScreen.kt
│-- ARActivity.kt
│-- res/
│   ├── drawable/
│   ├── font/
│   ├── values/
│-- AndroidManifest.xml
```

## Future Improvements
- Implement real image analysis using **TensorFlow Lite**.
- Integrate **ARCore** for AR-based visualization.
- Improve UI with additional animations and effects.
- Add support for more medical device recognition.

## License
This project is licensed under the **MIT License**.

## Author
Developed by **Bot Coders**.
