package com.portify.generated

import kotlinx.serialization.Serializable

/** Matches PortfolioPresenter output (see portfolio.sample.json). */
@Serializable
data class Portfolio(
    val name: String,
    val username: String? = null,
    val headline: String? = null,
    val bio: String? = null,
    val location: String? = null,
    val email: String? = null,
    val socialLinks: List<SocialLink> = emptyList(),
    val skills: List<Skill> = emptyList(),
    val experiences: List<Experience> = emptyList(),
    val education: List<Education> = emptyList(),
    val projects: List<Project> = emptyList(),
)

@Serializable data class SocialLink(val platform: String, val url: String)
@Serializable data class Skill(val name: String, val category: String? = null, val level: String? = null)

/**
 * Colour palette injected by the generation pipeline (theme.json) from the chosen
 * template's config/app_themes.php entry — so the built app matches the preview.
 * JSON keys are snake_case (e.g. on_accent); decoded via JsonNamingStrategy.SnakeCase.
 */
@Serializable
data class AppTheme(
    val accent: String = "#2563eb",
    val page: String = "#e8edf6",
    val screen: String = "#ffffff",
    val ink: String = "#0f172a",
    val muted: String = "#64748b",
    val surface: String = "#f1f5f9",
    val onAccent: String = "#ffffff",
    val dark: Boolean = false,
)

@Serializable
data class Experience(
    val role: String,
    val company: String,
    val location: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val current: Boolean = false,
    val description: String? = null,
    val highlights: List<String> = emptyList(),
)

@Serializable
data class Education(
    val institution: String,
    val degree: String,
    val fieldOfStudy: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
)

@Serializable
data class Project(
    val title: String,
    val description: String? = null,
    val liveUrl: String? = null,
    val sourceUrl: String? = null,
    val logoUrl: String? = null,
    val images: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
)
