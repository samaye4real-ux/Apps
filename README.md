# Chess Coach

Offline beginner chess training game for Android.

## Current playable build

- White player versus a lightweight offline coach
- Legal movement for all standard pieces
- Move destination highlights
- Hint toggle, undo, and new game
- Responsive portrait board
- No network or third-party runtime dependencies

## Build

Use Android Studio with Android SDK 35, or run:

```bash
gradle --no-daemon assembleDebug
```

APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Roadmap

Check/checkmate validation, castling, promotion, en passant, lessons, puzzles, and persistent progress are planned for subsequent iterations.
