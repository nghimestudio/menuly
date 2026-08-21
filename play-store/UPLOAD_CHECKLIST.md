# Play Store upload pack — Menuly

Everything below is ready to copy/upload.

## Folder map

```
play-store/
  graphics/
    icon_512.png                 ← Play Console app icon (required)
    icon_1024.png                ← master
    feature_graphic_1024x500.png ← Feature graphic (required)
    logo_lockup.png              ← brand / website / press
    logo_lockup_1200.png
  screenshots/phone/
    01_screenshot_01_home.png
    02_screenshot_02_result.png
    03_screenshot_03_scan.png
  listing/
    en.txt                       ← English store text
    vi.txt                       ← Vietnamese store text
  legal/
    privacy-policy.html          ← host this, paste URL into Play Console
  CONSOLE_ANSWERS.txt            ← Data safety / ratings / checklist
  UPLOAD_CHECKLIST.md            ← this file
```

## 1) Host privacy policy (required)

Option A — GitHub Pages from this repo:

```bash
# from repo root, after commit
# Settings → Pages → Deploy from branch → /docs or gh-pages
```

Or simply open `legal/privacy-policy.html` in a static host and copy the public URL into Play Console → App content → Privacy policy.

**Edit the contact email** in `privacy-policy.html` before going live.

## 2) Upload listing

Play Console → Grow → Store presence → Main store listing:

| Field | File |
|-------|------|
| App name / short / full description | `listing/en.txt` (and `vi.txt`) |
| App icon 512 | `graphics/icon_512.png` |
| Feature graphic | `graphics/feature_graphic_1024x500.png` |
| Phone screenshots | `screenshots/phone/*.png` |

## 3) Fill App content forms

Use `CONSOLE_ANSWERS.txt` verbatim where possible.

## 4) Build signed AAB

```bash
cd android
# 1. Create keystore once (see KEYSTORE.md)
# 2. Fill keystore.properties from keystore.properties.example
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew :app:bundleRelease
```

Output:

`android/app/build/outputs/bundle/release/app-release.aab`

Upload that AAB under Testing → Internal testing (recommended first), then Production.

## 5) Android launcher

Adaptive + legacy mipmaps are already wired in `android/app/src/main/res/`.
Rebuild the app to see the new icon on device.
