<h1 align="center">Leofin</h1>
<h3 align="center">Stable Android TV & Google TV Client for <a href="https://jellyfin.org">Jellyfin</a></h3>

<p align="center">
  <a href="https://github.com/leonida92/leofin">
    <img alt="GPL 2.0 License" src="https://img.shields.io/badge/license-GPL%202.0-blue.svg"/>
  </a>
  <a href="https://github.com/leonida92/leofin">
    <img alt="Platform" src="https://img.shields.io/badge/platform-Android%20TV%20%7C%20Google%20TV-green.svg"/>
  </a>
  <a href="https://github.com/leonida92/leofin">
    <img alt="Engine" src="https://img.shields.io/badge/engine-Media3%20ExoPlayer%20%2B%20libass-orange.svg"/>
  </a>
</p>

---

**Leofin** is an enhanced, highly stable fork of the official [Jellyfin Android TV](https://github.com/jellyfin/jellyfin-androidtv) client.

Unlike experimental development branches, Leofin is built directly on the battle-tested official release base. It retains full architectural parity with the official app — including native Leanback preferences, rock-solid remote D-pad navigation, and single-window screensaver integration — while adding focused enhancements and running under an independent package ID (`org.leofin.androidtv`) for complete side-by-side coexistence with the official app.

---

## Architectural Parity with Official Jellyfin

Leofin stays as close as possible to the official Android TV client:
* **Native Leanback Settings**: Uses the official, fast Leanback preference hierarchy rather than unstable rewrites, ensuring zero remote lag, zero focus drops, and instant response.
* **Single-Window Overlay Screensaver**: Uses the official in-app screensaver overlay architecture, preventing black screens, window-focus traps, or remote capture bugs.
* **Independent Package ID (`org.leofin.androidtv`)**: Allows Leofin and the official Play Store Jellyfin app to be installed together on the same TV without signature conflicts, update collisions, or Play Store interference.

---

## Key Enhancements

* **Native ASS/SSA Subtitle Direct Play (`libass`)**:
  Integrated with native C++ `libass` rendering via `io.github.peerless2012:ass-media:0.2.2`. Direct-plays complex anime and styled SubStation Alpha subtitles locally on the device without server-side video transcoding, preserving original video quality and avoiding server CPU load. Can be toggled under **Preferences -> Developer options**.

* **Intro Skipper with Configurable Duration**:
  Fully integrated with Jellyfin Media Segments to detect and skip show intros. Includes a dedicated duration slider under **Preferences -> Playback -> Skip button duration** to configure how long the on-screen skip prompt remains visible (1 to 30 seconds).

* **Max Days in Next Up (Watch Cutoff)**:
  Brings the long-requested date cutoff filter to the TV interface. Configurable under **Preferences -> Home -> Max days in Next Up** (7, 14, 30, 60, 90, 180, 365 days, or disabled). Shows watched long ago automatically disappear from the Next Up row.

* **In-App GitHub Release Updater**:
  Integrated update engine under **Preferences -> About -> Check for updates**. Queries GitHub releases, downloads the latest signed APK directly onto the device, and triggers the system package installer with progress reporting.

---

## Building

Leofin requires the Android SDK and Java 17.

### Build Release APK
```bash
./gradlew assembleRelease
```

The signed release APK will be generated at:
```text
app/build/outputs/apk/release/leofin-androidtv-*-release.apk
```

### Build Debug APK
```bash
./gradlew assembleDebug
```

---

## Installation via ADB

To install Leofin directly to your Android TV or Google TV device over local Wi-Fi:

1. Enable **Developer Options** and **Network Debugging** on your TV.
2. Connect via ADB:
   ```bash
   adb connect <TV_IP_ADDRESS>:5555
   ```
3. Install the APK:
   ```bash
   adb -s <TV_IP_ADDRESS>:5555 install -r app/build/outputs/apk/release/leofin-androidtv-*-release.apk
   ```

---

## Upstream & License

Leofin is based on the open-source [Jellyfin Android TV](https://github.com/jellyfin/jellyfin-androidtv) client and is licensed under the **GNU General Public License v2.0 (GPLv2)**.
