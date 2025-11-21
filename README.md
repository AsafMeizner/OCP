# Open Camera Pipeline (OCP)

**Open Camera Pipeline (OCP)** is a production-grade, cross-platform camera ecosystem designed to unlock the full potential of mobile photography and videography. It provides a flexible, node-based engine for creating custom image and audio processing pipelines, overcoming standard OEM restrictions.

## 🚀 Features

- **Core Engine**: Zero-copy GPU processing pipeline using OpenGL ES 2.0 and EGL.
- **Plugin System**: Modular plugin architecture supporting:
  - **Shader Plugins**: Custom GLSL fragment shaders.
  - **LUT Support**: 3D Color Grading.
  - **ML Plugins**: Face Detection (Google ML Kit).
  - **Audio Plugins**: DSP effects (Reverb, EQ).
- **Cross-Platform**:
  - **Android App**: Jetpack Compose UI, Camera2 API integration.
  - **Desktop App**: Kotlin Multiplatform (Compose for Desktop) for pipeline design.
- **Marketplace**: Backend server (Node.js + PostgreSQL) for sharing plugins and pipelines.

## 🏗 Architecture

The project is organized into a modular monorepo:

- **`sdk/`**: The core Android library containing the `PipelineEngine`, `CameraInput`, `VideoEncoder`, and Plugin interfaces.
- **`app/`**: The Android application module using Jetpack Compose for the UI (`CameraScreen`, `PipelineEditor`, `Marketplace`).
- **`shared/`**: Kotlin Multiplatform module for common logic (Data Models, JSON Serialization) shared between Android and Desktop.
- **`desktop/`**: Desktop application for designing pipelines and (future) remote control.
- **`server/`**: Node.js (Express + TypeScript) backend for the Marketplace and User Auth.

## 🛠 Setup & Build

### Prerequisites
- JDK 17+
- Android Studio Hedgehog or newer
- Node.js 18+
- Docker (optional, for PostgreSQL)

### Building the Android App
1. Open the project in Android Studio.
2. Sync Gradle.
3. Run the `app` configuration on an Android device (Android 8.0+).

### Running the Desktop App
1. Run `./gradlew :desktop:run` from the terminal.

### Running the Server
1. Navigate to `server/`.
2. Run `npm install`.
3. Run `npm run dev`.
   - Ensure `dev.db` (SQLite) is initialized via Prisma: `npx prisma migrate dev`.

## 🧩 Creating Plugins

Plugins implement the `Plugin` interface (for Graphics) or `AudioPlugin` (for Audio).

```kotlin
class MyCustomFilter : ShaderPlugin() {
    override fun getFragmentShaderCode(): String {
        return """
            precision mediump float;
            varying vec2 vTextureCoord;
            uniform sampler2D sTexture;
            void main() {
                vec4 color = texture2D(sTexture, vTextureCoord);
                gl_FragColor = vec4(1.0 - color.rgb, color.a); // Invert
            }
        """
    }
}
```

## 🗺 Roadmap

- [x] Core Engine (GL + Camera2)
- [x] Plugin System (Shaders, LUTs)
- [x] Android App UI (Compose)
- [x] Backend Server (Auth, Marketplace)
- [x] Advanced Plugins (ML Face Detect, Audio Reverb)
- [x] Desktop App (Pipeline Designer)
- [ ] Cloud Sync
- [ ] Web Editor

## 📄 License

MIT License.
