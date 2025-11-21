# ⭐ MASTER BLUEPRINT
# Open Camera Pipeline (OCP)

A cross-device, community-driven, plugin-powered camera pipeline for Android that overcomes OEM restrictions and produces near-camera-app quality for social media, apps, and creators.

This blueprint covers the app, the SDK, the plugin system, the pipeline editor, the marketplace, the PC companion, cloud sync, developer documentation, security, and long-term extensibility.

## 🚀 1. Core Vision

Android camera quality is fragmented because:
- OEM pipelines are private
- Social media apps use fallback APIs
- Device vendors expose limited controls
- Optimizations differ per device

Your platform solves this by providing:

✔ **A unified video pipeline engine**
Built on zero-copy GPU → MediaCodec, extensible via plugins.

✔ **A community-powered optimization ecosystem**
Anyone can build and share device-specific pipelines, stabilization packs, color profiles, noise reducers, HDR maps, tone curves, and more.

✔ **A drop-in replacement SDK for social apps**
Apps can adopt your SDK instead of their legacy Camera API → delivering near-camera-app quality automatically.

✔ **A no-code / low-code pipeline editor**
Let users drag-drop plugins to build their own optimized pipelines.

✔ **A marketplace for pipelines & plugins**
Monetizable for creators and developers.

## 🧠 2. Architecture Overview
### High-Level Blocks
```text
+----------------------+
|     Social Apps      | <-- SDK (drop-in)
+----------+-----------+
           |
           v
+----------------------+        +--------------------+
|   Open Camera SDK    | -----> | Plugin Marketplace |
+----------+-----------+        +--------------------+
           |               
           v
+----------------------+
|  Pipeline Engine     |
| - Zero-copy GL       |
| - MediaCodec         |
| - Plugin graph       |
+----------+-----------+
           |
           v
+----------------------+
|   Hardware Camera    |
+----------------------+
```

## 🛠 3. Core Engine
Same as your spec:
- Stage negotiation: PREVIEW, RECORD, POST
- Pixel formats: OES → RGBA → (optional) YUV
- Preallocated GPU frame pool
- Latency watchdog
- Engine adapters: format converters
- Plugin execution: no allocs, GPU-only for RT
- Encoder: HEVC/AVC, HDR10+ (future)
- Audio path: AudioRecord → Muxer

### Extra features the engine MUST support:
**A) Multiple camera lenses:**
- ultrawide
- wide/main
- tele
- front cam
(lens switching inside pipeline)

**B) Multi-frame intelligence:**
- GyroFusion stabilization
- Temporal denoise
- Multi-frame HDR fusion
(via stateful plugin history)

**C) ML acceleration:**
- NNAPI
- GPU compute shaders
- Vulkan compute backends

**D) Dynamic fallback:**
- If GPU load is high → reduce plugin resolution or bypass.

## 🎨 4. The SDK for Apps (Drop-In Replacement)
**Goals:**
- Works like CameraX or Camera2 → easy replace
- Supports all pipeline features
- Automatically detects the best profile for the device
- Allows app devs to use high-quality output with one line

### SDK Components:
**A) CaptureController**
```kotlin
controller = OCP.create(context)
controller.start(cameraSelector, pipelineId)
controller.setPreviewSurface(surfaceView)
controller.startRecording(fileOrBuffer)
```

**B) Background encoder**
Buffer → MP4 output for social sharing.

**C) Auto device profile loading**
From marketplace cloud → best pipeline for your phone.

**D) Compatibility fallback**
If phone missing:
- HDR → fallback to SDR
- high fps → fallback to 30
- GPU shader feature → skip plugin

**E) Minimal friction**
Developers should be able to replace:
`CameraX.startPreview()`
With:
`OCP.startPreview()`

**F) Extensions**
- Face beautification plugin
- Color LUT plugin
- HDR tone mapping plugin
- Stabilization plugin

## 🎚 5. Plugin System (The Heart of the Platform)
### Types of plugins:
- **GPU Plugins**: Shaders, tone mapping, LUTs, color grading, blur, etc.
- **ML Plugins**: Face enhancement, object detection, super-resolution, etc.
- **Stateful Plugins**: Temporal filters, gyro stabilization, motion estimation.
- **Post-only Plugins**: Sharpening, denoise, color science, gamma correction.
- **Device-specific optimization plugins**:
    - Samsung S24 Ultra “Night Mode Fix”
    - Pixel 8 “Motion Denoise Enhancer”
    - Xiaomi “Oversharpen Removal”

### Plugin Structure:
Exactly what you have:
- `PluginCaps`
- `Params` (with UI metadata)
- `Stable schema version`
- `init()`, `process()`, `destroy()`

### Plugin Safety:
- **Sandbox**:
    - Execution time monitoring
    - Memory caps
    - No network access allowed
    - GPU watchdog kill-switch

## 🧩 6. Pipelines
Pipelines are JSON graphs of plugins:
```json
{
  "pipelineId": "s24ultra_default",
  "stages": {
    "PREVIEW": ["SamsungColorFix", "LUT_Default"],
    "RECORD": ["TemporalDenoise", "Stabilizer", "ColorScienceV3"],
    "POST": ["SharpenV1"]
  },
  "meta": { ... device caps ... }
}
```

### Pipeline rules:
- MUST validate before running
- Plugins must agree on input/output format
- Stages must obey latency budget

### Pipeline ranking:
System auto-selects pipeline:
- best rating
- most downloads
- matching device model
- matching Android version
- benchmark score from device tests

## 🧑‍💻 7. Pipeline Editor (On-Device)
**Features:**
✔ Drag-and-drop nodes
✔ Live preview
✔ GPU usage meter
✔ Per-plugin parameter UI
✔ Color picker for LUTs
✔ One-click test on footage
✔ Auto benchmark
✔ Export/import pipeline
✔ Switch camera lens
✔ Add metadata tags
✔ Revisions/history

## 🖥 8. PC Companion App
**Why?**
Professional creators want:
- bigger screen
- faster iteration
- precise color work
- shader editing with preview

**Communication:**
- USB ADB tunnel
- WebRTC over USB
- Pipeline sync (JSON)
- Live preview feed
- Real-time stats (FPS, latency, GPU usage)

**PC Companion features:**
- Full pipeline graph editor
- Shader editor with live updating
- Instant re-deploy to phone
- Simultaneous preview: raw vs processed
- AI assistant for pipeline optimization
- Pipeline testing: worst-case stress tests

## 🛒 9. Marketplace
### Marketplace content:
- **Plugins**
    - free
    - paid
    - premium verified developers
- **Pipelines (profiles)**
    - per phone model
    - per creator
    - per lighting scenario
    - per aesthetic (cinematic, vlog, clean, natural)
- **LUT packs**
- **ML models**
- **Shader packs**

### Monetization:
- App takes 15%
- Developer gets 85%
- Subscriptions for plugin suites
- Device-specific “Premium Pack”

### Rating system:
- Quality rating
- Device compatibility rating
- Stability rating
- GPU impact rating

### Trust:
- Verified developers
- Versioning
- Automatic sandbox testing

### Security:
- Plugins must pass automated review:
    - no native code outside allowed API
    - no network calls
    - no dangerous permissions

## ☁ 10. Cloud Infrastructure
### Components:
- Pipeline repository
- Plugin repository
- User accounts + sync
- Device profiles
- Analytics (opt-in)

### Sync data:
- Installed pipelines
- Purchased plugins
- Settings
- Device benchmark data
- Crash logs
- GPU metrics

## 🔐 11. Security Model
**Strong sandbox:**
- Plugins run only via provided API
- GPU access only through engine
- ML models cannot read device storage
- No internet access except via marketplace API
- Version signature verification

## 🧱 12. Long-Term Upgradability
**Future engine versions:**
- Vulkan backend
- ML-only pipelines
- HDR10+ encode
- RAW → computational pipeline
- Sensor fusion with gyroscope
- Hardware-accelerated denoise

**Future plugin types:**
- ML super-resolution
- AI background replacement
- Portrait segmentation
- Bokeh simulation
- Frame interpolation

## 🧪 13. Testing Framework
### Automated testing:
- **Frame validation**: no missing frames, no out-of-order timestamps
- **Performance validation**: plugin latency test, pipeline profiling
- **Visual quality tests**: SSIM comparison, color deviation test, dynamic range test

### Device matrix
Test across: Samsung, Pixel, Xiaomi, Oppo, Vivo, Nothing

### Stability stress tests
- 30 min continuous recording
- thermal throttling test

## 📱 14. App UI Overview
### Main Tabs:
- **Capture**
    - Manual pipeline selector
    - Profiles: vlog, cinematic, nature, low-light
    - Manual controls (ISO, shutter, WB)
    - Quick LUT switch
    - Plugin toggle panel
- **Pipeline Editor**
    - Node graph
    - Parameter editor
    - Live preview
    - Benchmarks
- **Marketplace**
    - Plugin store
    - Pipeline store
    - Downloaded items
- **Profile Manager**
    - Device-specific profiles
    - Auto-detection
    - Backup/sync
- **Creator Mode**
    - Dual preview (before/after)
    - Color scopes (vectorscope, waveform)

## 🧵 15. Developer Tools
### SDK provides:
- Pipeline validation tool
- Plugin test harness
- Shader builder
- GPU memory visualizer
- NNAPI graph tester
- Latency simulation

## 👑 16. Final Product Summary
**What you are building:**
A unified camera platform that can:
✔ outperform OEM camera pipelines (long-term)
✔ unify video quality across Android
✔ give social apps near-native image quality
✔ empower creators with custom pipelines
✔ enable a marketplace of visual innovation

This is basically:
The “Unreal Engine of Mobile Video Pipelines” for Android.
