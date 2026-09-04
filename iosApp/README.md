# Portify App Template — iOS (SwiftUI)

The iOS half of the KMP portfolio app. Same design + injected data as the Android app:
it renders the bundled `Resources/portfolio.json` + `Resources/theme.json` (written by the
generation pipeline) into a themed three-tab UI — no network.

## Sources
```
iosApp/
  Portify.xcodeproj       # ← open this in Xcode (generated from project.yml)
  project.yml             # XcodeGen spec — source of truth for the project
  PortifyApp.swift        # @main App entry — loads portfolio + theme
  ContentView.swift       # themed TabView: Home / Projects / Profile
  Portfolio.swift         # Codable models (.convertFromSnakeCase decoding)
  Theme.swift             # AppTheme + Palette (hex → SwiftUI Color)
  Resources/
    portfolio.json        # injected per client (snake_case, see ../portfolio.sample.json)
    theme.json            # injected palette (see ../theme.sample.json)
```

## Open & run
```
open Portify.xcodeproj          # then ⌘R (SF Symbols only — no extra dependencies)
```
No manual setup: the project is ready to open, build, and run on a simulator.

## Regenerating the project
`Portify.xcodeproj` is generated from `project.yml`. After adding/removing files, rerun:
```
brew install xcodegen   # once
xcodegen generate       # in iosApp/
```

## Per-client values
The generation pipeline substitutes the bundle id / display name in `project.yml`
(`PRODUCT_BUNDLE_IDENTIFIER`, `INFOPLIST_KEY_CFBundleDisplayName`) — the iOS analog of the
Android `APP_ID` / `APP_NAME` placeholders — then runs `xcodegen generate` before building.

## CI (paid add-on: macOS runner + fastlane → TestFlight)
`xcodebuild -project Portify.xcodeproj -scheme Portify -sdk iphonesimulator build` is the
smoke build; a signed archive + fastlane pilot uploads to TestFlight.

The palette is identical to the web preview and the Android app (single source of truth:
`PortifyWeb/config/app_themes.php`).
