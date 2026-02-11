# 📱 Maya AI - How to Build APK

## 🚀 Quick Start (EASIEST METHOD)

### Method 1: Using Android Studio (Recommended)

**Step-by-Step Guide:**

#### 1️⃣ **Download & Install Android Studio**
- Download from: https://developer.android.com/studio
- Install with default settings
- Wait for SDK installation

#### 2️⃣ **Clone the Project**
```bash
git clone https://github.com/piashmsubd/Maya-ai-automation-android.git
```

#### 3️⃣ **Open in Android Studio**
- Launch Android Studio
- Click "Open"
- Select `Maya-ai-automation-android` folder
- Click "OK"

#### 4️⃣ **Wait for Gradle Sync**
- First time will take 5-10 minutes
- Downloads all dependencies automatically
- Wait until "Gradle sync finished" appears

#### 5️⃣ **Build APK**
- Click: **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
- Wait 5-10 minutes (first build is slow)
- Success message will appear

#### 6️⃣ **Get Your APK**
```bash
Location: app/build/outputs/apk/debug/app-debug.apk
```

---

## 🖥️ Alternative: Command Line Build

### Requirements:
- JDK 17 installed
- Android SDK installed
- Git installed

### Installation Steps:

#### 1️⃣ **Install JDK 17**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Check installation
java -version
```

#### 2️⃣ **Install Android SDK**
```bash
# Download Android Command Line Tools
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

# Extract
unzip commandlinetools-linux-9477386_latest.zip -d ~/android-sdk
mkdir -p ~/android-sdk/cmdline-tools/latest
mv ~/android-sdk/cmdline-tools/* ~/android-sdk/cmdline-tools/latest/

# Set environment variables
export ANDROID_HOME=~/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accept licenses and install SDK
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

#### 3️⃣ **Clone & Build**
```bash
# Clone repository
git clone https://github.com/piashmsubd/Maya-ai-automation-android.git
cd Maya-ai-automation-android

# Make gradlew executable
chmod +x gradlew

# Build APK
./gradlew assembleDebug

# APK location
ls -la app/build/outputs/apk/debug/app-debug.apk
```

---

## 📦 Pre-Built APK Download

**Coming Soon!** 

Since GitHub Actions is disabled, pre-built APKs will be uploaded manually to:
- **Releases**: https://github.com/piashmsubd/Maya-ai-automation-android/releases

Check the Releases page for downloadable APK files.

---

## 🔧 Troubleshooting

### Issue 1: "SDK location not found"

**Error:**
```
SDK location not found. Define location with sdk.dir
```

**Fix:**
```bash
# Create local.properties file
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

---

### Issue 2: "Permission denied" on gradlew

**Fix:**
```bash
chmod +x gradlew
```

---

### Issue 3: Gradle sync failed

**Fix:**
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

---

### Issue 4: "Failed to install" on Android Studio

**Fix:**
1. File → Invalidate Caches → Invalidate and Restart
2. Build → Clean Project
3. Build → Rebuild Project

---

## 📱 Install APK on Phone

### Method 1: Direct Install via USB
```bash
# Enable USB debugging on phone
# Connect phone to computer

adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Method 2: Transfer & Install
1. Copy `app-debug.apk` to phone
2. Open File Manager on phone
3. Tap on APK file
4. Enable "Install from Unknown Sources" if prompted
5. Tap "Install"

---

## ⚙️ After Installation

### 1️⃣ **Configure AI Provider**
- Open Maya AI app
- Go to Settings
- Select AI Provider (OpenAI, Letta, etc.)
- Enter API Key:
  - **OpenAI**: https://platform.openai.com/api-keys
  - **Letta**: https://letta.com
  - **Cartesia**: https://cartesia.ai

### 2️⃣ **Grant Permissions**
The app will request:
- ✅ Microphone (for voice commands)
- ✅ SMS (read/send messages)
- ✅ Contacts (call by name)
- ✅ Phone (make calls)
- ✅ Camera (take photos)
- ✅ Storage (save files)

### 3️⃣ **Enable Services**

**Accessibility Service:**
1. Android Settings → Accessibility
2. Find "Maya AI Accessibility Service"
3. Turn ON

**Notification Listener:**
1. Android Settings → Apps → Special Access
2. Notification Access
3. Enable Maya AI

**Display Over Other Apps:**
1. Android Settings → Apps → Special Access
2. Display over other apps
3. Enable Maya AI

### 4️⃣ **Activate Maya**
- Open Maya AI app
- Toggle "Maya Active" to ON
- Say "Hey Maya" to test!

---

## 🎤 Test Voice Commands

Try these after setup:

**English:**
- "Hey Maya, what's the weather?"
- "Hey Maya, open Spotify"
- "Hey Maya, call Mom"

**Bengali:**
- "Hey Maya, WiFi on koro"
- "Hey Maya, SMS read koro"
- "Hey Maya, music play koro"

---

## 📊 Build Times

**First Build:**
- Android Studio: 10-15 minutes
- Command Line: 10-15 minutes

**Incremental Builds:**
- Android Studio: 1-3 minutes
- Command Line: 2-5 minutes

**APK Size:**
- Debug: ~50-80 MB
- Release: ~30-50 MB

---

## 💡 Tips for Faster Builds

1. **Use Android Studio** - Fastest for development
2. **Enable Gradle Daemon** - Speeds up builds
3. **Increase RAM** - Edit `gradle.properties`:
   ```
   org.gradle.jvmargs=-Xmx4096m
   ```
4. **Use Gradle Build Cache** - Already enabled
5. **Close Other Apps** - More RAM for build

---

## 🆘 Need Help?

**Build Issues:**
1. Check error message carefully
2. Google the error
3. Check this guide
4. Open GitHub issue

**GitHub Issues:**
https://github.com/piashmsubd/Maya-ai-automation-android/issues

**Documentation:**
- README.md - Full project overview
- FIXES.md - Known issues and fixes
- BUILD_NOTES.md - Detailed build info

---

## 📝 Important Notes

- **No GitHub Actions**: Manual build required
- **First build is slow**: Downloads all dependencies
- **Internet required**: For downloading libraries
- **Android 8.0+**: Minimum required
- **Root optional**: Works without root

---

## ✅ Success Checklist

After building, verify:
- [x] APK file exists at `app/build/outputs/apk/debug/app-debug.apk`
- [x] APK size is 50-80 MB
- [x] APK installs on phone without errors
- [x] App launches and shows main screen
- [x] Can navigate to Settings
- [x] Can request permissions

---

**Happy Building! 🚀**

*If you build successfully, consider sharing the APK on the Releases page to help others!*
