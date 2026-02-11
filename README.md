# Maya AI - Android AI Assistant

Maya AI is a powerful Android AI assistant app inspired by Jarvis. It combines voice control, multiple AI providers, accessibility services, and system automation to provide a comprehensive hands-free Android experience.

## Features

### Voice System
- **Wake Word Detection**: Activate Maya with "Hey Maya"
- **Speech-to-Text**: Convert voice commands to text
- **Text-to-Speech**: Natural voice responses (Android TTS + Cartesia AI)
- **Continuous Listening**: Keep Maya active for ongoing conversation
- **Multi-language Support**: English and Bengali

### AI Providers
Choose from multiple AI backends:
- **OpenAI**: GPT-4, GPT-3.5, Claude (via OpenAI-compatible API)
- **Groq**: Fast inference with open models
- **Letta.ai**: Stateful AI with long-term memory
- **LLaMA Local**: On-device inference (offline mode)
- **OpenCode Zen**: Code assistance
- **Cartesia AI**: Realistic voice synthesis

### Automation Features
- **SMS Management**: Read incoming SMS, auto-reply with AI
- **Messenger Integration**: Read/reply on WhatsApp, Facebook Messenger, Telegram
- **Call Management**: Make calls by name, announce callers
- **App Control**: Open, close, switch apps by voice
- **System Control**: WiFi, Bluetooth, brightness, volume, airplane mode
- **UI Automation**: Full accessibility service for screen interaction

### Advanced Capabilities
- **Root Access**: Terminal access with LibSU (optional)
- **Floating Bubble**: Always-accessible assistant overlay
- **Memory System**: Persistent conversation history with Room database
- **Custom Commands**: Create your own voice-activated shortcuts
- **Auto-start**: Launch on device boot

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room
- **Preferences**: DataStore
- **AI Integration**: OpenAI API, Letta API, Cartesia AI
- **Root**: LibSU
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Screenshots

_Coming soon_

## Installation

### Prerequisites
- Android device running Android 8.0+ (API 26+)
- Microphone permission
- Internet connection (for cloud AI providers)
- Optional: Rooted device for advanced features

### Build from Source

1. Clone the repository:
```bash
git clone https://github.com/yourusername/Maya-ai-automation-android.git
cd Maya-ai-automation-android
```

2. Open in Android Studio (Hedgehog or newer)

3. Sync Gradle dependencies

4. Configure your AI API keys in the app settings after installation

5. Build and run:
```bash
./gradlew assembleDebug
```

### Permissions Required

The app requires the following permissions:
- Microphone (voice input)
- SMS (read/send messages)
- Contacts (name-based calling)
- Phone (make/receive calls)
- Camera (photo capture)
- Storage (file access)
- Accessibility Service (UI automation)
- Notification Listener (messenger integration)
- System Alert Window (floating bubble)
- Optional: Root access (advanced features)

## Setup

### 1. Configure AI Provider

Open Settings → AI Provider and choose your preferred provider:

**OpenAI**:
- Get API key from https://platform.openai.com
- Enter API key and optionally customize base URL
- Select model (gpt-4, gpt-3.5-turbo, etc.)

**Letta.ai**:
- Get API key from https://letta.com
- Create an agent and copy the agent ID
- Enter both in settings

**Cartesia AI**:
- Get API key from https://cartesia.ai
- Enable for realistic voice synthesis

**Local LLaMA**:
- Download a compatible LLaMA model
- Convert to TensorFlow Lite format
- Place in app directory

### 2. Enable Services

**Accessibility Service**:
1. Go to Android Settings → Accessibility
2. Find "Maya AI Accessibility Service"
3. Enable it

**Notification Listener**:
1. Go to Android Settings → Apps → Special Access → Notification Access
2. Enable Maya AI

**Overlay Permission**:
1. Go to Android Settings → Apps → Special Access → Display over other apps
2. Enable Maya AI

### 3. Configure Voice

- Set wake word (default: "Hey Maya")
- Choose TTS language (English/Bengali)
- Adjust speech rate
- Enable continuous listening if desired

## Usage

### Voice Commands

Activate Maya with "Hey Maya" and try:

**General**:
- "What's the weather?"
- "Tell me a joke"
- "What can you do?"

**Communication**:
- "Send SMS to John saying I'll be late"
- "Call Mom"
- "Read my messages"

**Apps**:
- "Open Spotify"
- "Close Chrome"
- "Switch to WhatsApp"

**System**:
- "Turn on WiFi"
- "Increase brightness"
- "Set volume to 50%"
- "Enable airplane mode"

**Advanced** (requires root):
- "Reboot device"
- "Clear cache for Instagram"
- "Uninstall Facebook"

### Chat Interface

- Type messages directly in the chat screen
- View conversation history
- Switch between AI providers
- Voice input button for hands-free chat

### Floating Bubble

Enable the floating bubble for quick access from anywhere:
- Tap to open Maya
- Drag to reposition
- Always accessible overlay

## Development

### Project Structure

```
app/
├── src/main/
│   ├── java/com/maya/ai/
│   │   ├── ai/                    # AI providers and voice system
│   │   │   ├── providers/         # OpenAI, Letta, LLaMA clients
│   │   │   └── voice/             # Speech recognition & synthesis
│   │   ├── data/                  # Data layer
│   │   │   ├── database/          # Room database
│   │   │   ├── datastore/         # Preferences
│   │   │   ├── models/            # Data models
│   │   │   └── repository/        # Repositories
│   │   ├── presentation/          # UI layer
│   │   │   ├── chat/              # Chat screen
│   │   │   ├── settings/          # Settings screen
│   │   │   ├── main/              # Main activity & navigation
│   │   │   └── theme/             # Material 3 theme
│   │   ├── services/              # Android services
│   │   │   ├── MayaAccessibilityService.kt
│   │   │   ├── VoiceAssistantService.kt
│   │   │   ├── FloatingBubbleService.kt
│   │   │   └── ...
│   │   └── utils/                 # Utility classes
│   │       ├── RootShell.kt       # Root access
│   │       ├── SystemController.kt # System control
│   │       ├── AppController.kt   # App management
│   │       └── PhoneController.kt # Phone functions
│   └── res/                       # Resources
```

### Adding Custom Commands

1. Create command in database
2. Define trigger phrase
3. Specify action and parameters
4. Maya will execute when triggered

### Extending AI Providers

Implement the `AIClient` interface:

```kotlin
class MyAIClient : AIClient {
    override suspend fun sendMessage(message: String, history: List<Message>): Result<String>
    override suspend fun streamMessage(message: String, history: List<Message>, onChunk: (String) -> Unit): Result<Unit>
    override fun isConfigured(): Boolean
}
```

## Roadmap

- [ ] Camera-based features (visual AI)
- [ ] Calendar integration
- [ ] Email management
- [ ] Smart home control
- [ ] Routine automation
- [ ] Multi-user support
- [ ] Cloud sync for conversations
- [ ] Plugin system for extensions

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Disclaimer

This app requires extensive permissions for full functionality. Use responsibly and only grant permissions you're comfortable with. Root access is entirely optional and not required for basic features.

The app's automation capabilities should be used ethically and in compliance with local laws and terms of service of third-party apps.

## Support

For issues, questions, or feature requests:
- Open an issue on GitHub
- Email: support@example.com

## Acknowledgments

- OpenAI for GPT models
- Letta.ai for stateful AI capabilities
- Cartesia AI for voice synthesis
- LibSU for root access library
- Android Jetpack libraries

---

Made with ❤️ for the Android community

**Status**: Active Development | **Version**: 1.0.0 | **Last Updated**: February 2026
