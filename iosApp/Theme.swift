import SwiftUI

/// Colour palette injected by the generation pipeline (theme.json) from the chosen
/// template's config/app_themes.php entry — so the built app matches the preview.
struct AppTheme: Codable {
    var accent = "#2563eb"
    var page = "#e8edf6"
    var screen = "#ffffff"
    var ink = "#0f172a"
    var muted = "#64748b"
    var surface = "#f1f5f9"
    var onAccent = "#ffffff"
    var dark = false

    static func load() -> AppTheme {
        (try? Bundle.main.decode(AppTheme.self, from: "theme.json")) ?? AppTheme()
    }
}

/// Resolved SwiftUI colours for exact palette control.
struct Palette {
    let accent, page, screen, ink, muted, surface, onAccent: Color
    let dark: Bool

    init(_ t: AppTheme) {
        accent = Color(hex: t.accent); page = Color(hex: t.page); screen = Color(hex: t.screen)
        ink = Color(hex: t.ink); muted = Color(hex: t.muted); surface = Color(hex: t.surface)
        onAccent = Color(hex: t.onAccent); dark = t.dark
    }
}

extension Color {
    /// Parse "#RRGGBB" (or "RRGGBB") into a Color.
    init(hex: String) {
        let clean = hex.hasPrefix("#") ? String(hex.dropFirst()) : hex
        var value: UInt64 = 0
        Scanner(string: clean).scanHexInt64(&value)
        let r = Double((value & 0xFF0000) >> 16) / 255
        let g = Double((value & 0x00FF00) >> 8) / 255
        let b = Double(value & 0x0000FF) / 255
        self.init(red: r, green: g, blue: b)
    }
}
