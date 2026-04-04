# Video Player

A clean, fast Android video player that organises your device videos by folder and plays them with full controls.

---

## Features

- **Folder-based browsing** — videos grouped by directory, just like your file system
- **Smooth playback** — powered by ExoPlayer with next/previous navigation
- **Video actions** — rename, share, delete, and view file properties from a bottom sheet
- **Thumbnail previews** — generated on-the-fly using Glide
- **Pull to refresh** — swipe down on the home screen to rescan storage
- **Permission handling** — guides users through storage access with a rationale flow

---

## Screens

| Screen | Description |
|---|---|
| Splash | 2-second branded launch screen |
| Allow Access | Requests storage permission before entering the app |
| Home | Grid of video folders found on device |
| Folder View | All videos inside a selected folder |
| Player | Full-screen playback with controls |

---

## Tech Stack

| | |
|---|---|
| Language | Kotlin |
| Video Playback | ExoPlayer 2.17.1 |
| Image Loading | Glide 4.13.0 |
| UI | Material Design, ConstraintLayout, RecyclerView |
| Min SDK | 23 (Android 6.0) |
| Target SDK | 28 (Android 9.0) |

---

## Getting Started

1. Clone the repo
2. Open in Android Studio
3. Run on a device or emulator with API 23+
4. Grant storage permission when prompted

---

## Permissions

| Permission | Reason |
|---|---|
| `READ_EXTERNAL_STORAGE` | Read videos from device storage |
| `WRITE_EXTERNAL_STORAGE` | Rename and delete video files |
