# Open Camera Pipeline (OCP)

**Open Camera Pipeline (OCP)** is a comprehensive, open-source ecosystem for building advanced mobile photography and videography applications. It provides a high-performance OpenGL ES engine, a modular plugin system (Shaders, ML, Audio), a backend for cloud synchronization, and cross-platform tools.

## Features

### 📱 Android SDK & App
*   **Custom Rendering Engine**: OpenGL ES 2.0 based pipeline with `PipelineEngine`.
*   **Plugin System**:
    *   **Shader Plugins**: Write custom GLSL effects (e.g., LUTs, Brightness/Contrast).
    *   **ML Plugins**: Integrate Google ML Kit (e.g., Face Detection).
    *   **Audio Plugins**: Process audio streams (e.g., Reverb).
*   **Video Recording**: Hardware-accelerated H.264/AAC recording (`VideoEncoder`).
*   **Advanced UI**:
    *   **Pipeline Editor**: Node-based graph and list views with parameter sliders.
    *   **Marketplace**: Browse and install community plugins.
    *   **Cloud Sync**: Save and load pipelines from the cloud.

### ☁️ Backend Server
*   **Node.js & Express**: RESTful API for user authentication and data management.
*   **Pipeline Repository**: Store and retrieve pipeline definitions.
*   **Plugin Registry**: Centralized marketplace for plugins.

### 🖥️ Desktop Tools
*   **Pipeline Designer**: Kotlin Multiplatform (Compose for Desktop) application to design pipelines on your PC.
*   **JSON Export**: Export pipeline definitions for use in the Android app.

## Architecture

The project is modularized into:
*   `:sdk`: Core logic, GL engine, Plugin interfaces.
*   `:app`: Android application UI and integration.
*   `:shared`: Kotlin Multiplatform data models and serialization.
*   `:desktop`: Desktop application.
*   `:server`: Node.js backend.

## Getting Started

### Prerequisites
*   Android Studio Koala or newer.
*   JDK 17.
*   Node.js 18+.

### Build & Run
1.  **Android App**: Open in Android Studio and run the `app` configuration.
2.  **Desktop App**: Run `./gradlew :desktop:run`.
3.  **Server**:
    ```bash
    cd server
    npm install
    npm start
    ```

## Roadmap Status
This project has reached **Production Release Candidate** status. All planned features from Roadmap V1-V4 have been implemented.

## License
MIT
