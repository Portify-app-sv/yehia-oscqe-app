import Foundation

/// Mirrors PortfolioPresenter output (see portfolio.sample.json). JSON is snake_case and
/// decoded with `.convertFromSnakeCase`, so Swift properties stay camelCase.
struct Portfolio: Codable {
    var name: String
    var username: String?
    var headline: String?
    var bio: String?
    var location: String?
    var email: String?
    var socialLinks: [SocialLink] = []
    var skills: [Skill] = []
    var experiences: [Experience] = []
    var education: [Education] = []
    var projects: [Project] = []

    /// Loads `portfolio.json` from the app bundle; empty fallback if missing/invalid.
    static func load() -> Portfolio {
        (try? Bundle.main.decode(Portfolio.self, from: "portfolio.json"))
            ?? Portfolio(name: "Portfolio")
    }
}

struct SocialLink: Codable { var platform: String; var url: String }
struct Skill: Codable { var name: String; var category: String?; var level: String? }

struct Experience: Codable {
    var role: String
    var company: String
    var location: String?
    var startDate: String?
    var endDate: String?
    var current: Bool = false
    var description: String?
    var highlights: [String] = []
}

struct Education: Codable {
    var institution: String
    var degree: String
    var fieldOfStudy: String?
    var startDate: String?
    var endDate: String?
}

struct Project: Codable {
    var title: String
    var description: String?
    var liveUrl: String?
    var sourceUrl: String?
    var logoUrl: String?
    var images: [String] = []
    var tags: [String] = []
}

extension Bundle {
    /// Decode a bundled JSON resource using snake_case → camelCase key conversion.
    func decode<T: Decodable>(_ type: T.Type, from file: String) throws -> T {
        let name = (file as NSString).deletingPathExtension
        let ext = (file as NSString).pathExtension
        guard let url = url(forResource: name, withExtension: ext),
              let data = try? Data(contentsOf: url) else {
            throw CocoaError(.fileNoSuchFile)
        }
        let decoder = JSONDecoder()
        decoder.keyDecodingStrategy = .convertFromSnakeCase
        return try decoder.decode(T.self, from: data)
    }
}
