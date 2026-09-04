import SwiftUI

/// App entry. Loads the bundled portfolio + theme (no network) and shows the themed UI.
@main
struct PortifyApp: App {
    private let portfolio = Portfolio.load()
    private let palette = Palette(AppTheme.load())

    var body: some Scene {
        WindowGroup {
            ContentView(p: portfolio, c: palette)
                .preferredColorScheme(palette.dark ? .dark : .light)
        }
    }
}
