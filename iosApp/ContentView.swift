import SwiftUI

/// Themed three-tab portfolio UI (Home / Projects / Profile) mirroring the Android template.
struct ContentView: View {
    let p: Portfolio
    let c: Palette

    var body: some View {
        TabView {
            HomeScreen(p: p, c: c)
                .tabItem { Label("Home", systemImage: "house") }
            ProjectsScreen(p: p, c: c)
                .tabItem { Label("Projects", systemImage: "square.grid.2x2") }
            ProfileScreen(p: p, c: c)
                .tabItem { Label("Profile", systemImage: "person") }
        }
        .tint(c.accent)
    }
}

private struct HomeScreen: View {
    let p: Portfolio
    let c: Palette

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10) {
                Avatar(name: p.name, c: c)
                Text(p.name).font(.title).bold().foregroundColor(c.ink)
                if let h = p.headline { Text(h).font(.subheadline).bold().foregroundColor(c.accent) }
                HStack(spacing: 8) {
                    Stat("\(p.projects.count)", "PROJECTS", c: c)
                    Stat("\(p.experiences.count)", "ROLES", c: c)
                    Stat("\(p.skills.count)", "SKILLS", c: c)
                }
                .padding(.top, 6)
                if !p.projects.isEmpty {
                    Caption("Featured", c: c)
                    ForEach(Array(p.projects.prefix(3).enumerated()), id: \.offset) { _, pr in
                        ProjectCard(title: pr.title, description: pr.description, c: c)
                    }
                }
            }
            .padding(20)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .background(c.page.ignoresSafeArea())
    }
}

private struct ProjectsScreen: View {
    let p: Portfolio
    let c: Palette

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 6) {
                Text("Projects").font(.title2).bold().foregroundColor(c.ink)
                Text("\(p.projects.count) shipped").font(.caption).foregroundColor(c.accent)
                ForEach(Array(p.projects.enumerated()), id: \.offset) { _, pr in
                    ListRow(
                        initial: String(pr.title.prefix(1)),
                        title: pr.title,
                        subtitle: pr.tags.prefix(2).joined(separator: " · ").isEmpty
                            ? "Project" : pr.tags.prefix(2).joined(separator: " · "),
                        c: c
                    )
                }
            }
            .padding(20)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .background(c.page.ignoresSafeArea())
    }
}

private struct ProfileScreen: View {
    let p: Portfolio
    let c: Palette

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 8) {
                Avatar(name: p.name, c: c)
                Text(p.name).font(.title2).bold().foregroundColor(c.ink)
                if let bio = p.bio { Text(bio).font(.footnote).foregroundColor(c.muted) }
                if !p.skills.isEmpty {
                    Caption("Skills", c: c)
                    SkillChips(skills: p.skills.map { $0.name }, c: c)
                }
                if !p.experiences.isEmpty {
                    Caption("Experience", c: c)
                    ForEach(Array(p.experiences.enumerated()), id: \.offset) { _, e in
                        VStack(alignment: .leading, spacing: 2) {
                            Text(e.role).font(.subheadline).bold().foregroundColor(c.ink)
                            Text(e.company).font(.caption).foregroundColor(c.muted)
                        }
                        .padding(.vertical, 4)
                    }
                }
            }
            .padding(20)
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .background(c.page.ignoresSafeArea())
    }
}

/* ---------- Reusable pieces ---------- */

private struct Avatar: View {
    let name: String
    let c: Palette

    private var initials: String {
        name.split(separator: " ").prefix(2)
            .map { $0.prefix(1).uppercased() }.joined()
    }

    var body: some View {
        RoundedRectangle(cornerRadius: 18)
            .fill(c.accent)
            .frame(width: 56, height: 56)
            .overlay(Text(initials).font(.title3).bold().foregroundColor(c.onAccent))
    }
}

private struct Stat: View {
    let value: String
    let label: String
    let c: Palette

    init(_ value: String, _ label: String, c: Palette) {
        self.value = value; self.label = label; self.c = c
    }

    var body: some View {
        VStack(spacing: 2) {
            Text(value).font(.headline).bold().foregroundColor(c.ink)
            Text(label).font(.system(size: 9)).foregroundColor(c.muted)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 9)
        .background(c.surface)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

private struct Caption: View {
    let text: String
    let c: Palette
    init(_ text: String, c: Palette) { self.text = text; self.c = c }
    var body: some View {
        Text(text.uppercased())
            .font(.system(size: 10)).bold().foregroundColor(c.muted)
            .padding(.top, 8)
    }
}

private struct ProjectCard: View {
    let title: String
    let description: String?
    let c: Palette
    var body: some View {
        VStack(alignment: .leading, spacing: 3) {
            Text(title).font(.subheadline).bold().foregroundColor(c.ink)
            if let d = description { Text(d).font(.caption).foregroundColor(c.muted).lineLimit(2) }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .background(c.surface)
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}

private struct ListRow: View {
    let initial: String
    let title: String
    let subtitle: String
    let c: Palette
    var body: some View {
        HStack(spacing: 10) {
            RoundedRectangle(cornerRadius: 10)
                .fill(c.surface)
                .frame(width: 34, height: 34)
                .overlay(Text(initial.uppercased()).font(.subheadline).bold().foregroundColor(c.accent))
            VStack(alignment: .leading, spacing: 1) {
                Text(title).font(.caption).bold().foregroundColor(c.ink).lineLimit(1)
                Text(subtitle).font(.system(size: 10)).foregroundColor(c.muted)
            }
            Spacer()
        }
        .padding(.vertical, 8)
    }
}

private struct SkillChips: View {
    let skills: [String]
    let c: Palette

    private let columns = [GridItem(.adaptive(minimum: 70), spacing: 6, alignment: .leading)]

    var body: some View {
        LazyVGrid(columns: columns, alignment: .leading, spacing: 6) {
            ForEach(Array(skills.prefix(12).enumerated()), id: \.offset) { _, s in
                Text(s)
                    .font(.system(size: 10)).bold().foregroundColor(c.accent)
                    .padding(.horizontal, 9).padding(.vertical, 4)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(c.surface)
                    .clipShape(RoundedRectangle(cornerRadius: 7))
            }
        }
    }
}
