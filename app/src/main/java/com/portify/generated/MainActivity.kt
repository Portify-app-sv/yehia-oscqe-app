package com.portify.generated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

/**
 * Renders the bundled portfolio.json + theme.json — no network. The generation pipeline
 * replaces both assets with the client's data and the chosen template's palette before
 * building, so the app looks exactly like the previewed template.
 */
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSerializationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = Json {
            ignoreUnknownKeys = true
            namingStrategy = JsonNamingStrategy.SnakeCase
        }
        val portfolio = json.decodeFromString<Portfolio>(readAsset("portfolio.json"))
        val theme = runCatching { json.decodeFromString<AppTheme>(readAsset("theme.json")) }
            .getOrDefault(AppTheme())

        setContent { PortfolioApp(portfolio, Palette.from(theme)) }
    }

    private fun readAsset(name: String): String =
        assets.open(name).bufferedReader().use { it.readText() }
}

/** Parsed theme colours (hex → Compose Color), used directly for exact palette control. */
private data class Palette(
    val accent: Color,
    val page: Color,
    val screen: Color,
    val ink: Color,
    val muted: Color,
    val surface: Color,
    val onAccent: Color,
) {
    companion object {
        fun from(t: AppTheme) = Palette(
            accent = hex(t.accent), page = hex(t.page), screen = hex(t.screen),
            ink = hex(t.ink), muted = hex(t.muted), surface = hex(t.surface),
            onAccent = hex(t.onAccent),
        )

        private fun hex(value: String): Color {
            val clean = value.removePrefix("#")
            val argb = if (clean.length == 6) "ff$clean" else clean
            return Color(argb.toLong(16))
        }
    }
}

private enum class Tab(val label: String) { HOME("Home"), PROJECTS("Projects"), PROFILE("Profile") }

@Composable
private fun PortfolioApp(p: Portfolio, c: Palette) {
    var tab by remember { mutableStateOf(Tab.HOME) }

    Column(Modifier.fillMaxSize().background(c.page)) {
        Box(Modifier.weight(1f)) {
            when (tab) {
                Tab.HOME -> HomeScreen(p, c)
                Tab.PROJECTS -> ProjectsScreen(p, c)
                Tab.PROFILE -> ProfileScreen(p, c)
            }
        }
        TabBar(tab, c) { tab = it }
    }
}

@Composable
private fun HomeScreen(p: Portfolio, c: Palette) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Avatar(p.name, c)
            Spacer(Modifier.height(12.dp))
            Text(p.name, color = c.ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            p.headline?.let { Text(it, color = c.accent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Stat(p.projects.size.toString(), "PROJECTS", c, Modifier.weight(1f))
                Stat(p.experiences.size.toString(), "ROLES", c, Modifier.weight(1f))
                Stat(p.skills.size.toString(), "SKILLS", c, Modifier.weight(1f))
            }
            if (p.projects.isNotEmpty()) {
                Spacer(Modifier.height(18.dp))
                Caption("Featured", c)
            }
        }
        items(p.projects.take(3)) { pr ->
            ProjectCard(pr.title, pr.description, c)
        }
    }
}

@Composable
private fun ProjectsScreen(p: Portfolio, c: Palette) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
            Text("Projects", color = c.ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("${p.projects.size} shipped", color = c.accent, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
        }
        items(p.projects) { pr ->
            ListRow(
                initial = pr.title.take(1),
                title = pr.title,
                subtitle = pr.tags.take(2).joinToString(" · ").ifBlank { "Project" },
                c = c,
            )
        }
    }
}

@Composable
private fun ProfileScreen(p: Portfolio, c: Palette) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Avatar(p.name, c)
            Spacer(Modifier.height(10.dp))
            Text(p.name, color = c.ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            p.bio?.let { Text(it, color = c.muted, fontSize = 12.sp) }
            if (p.skills.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Caption("Skills", c)
                SkillChips(p.skills.map { it.name }, c)
            }
            if (p.experiences.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Caption("Experience", c)
            }
        }
        items(p.experiences) { e ->
            Column(Modifier.padding(vertical = 6.dp)) {
                Text(e.role, color = c.ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(e.company, color = c.muted, fontSize = 11.sp)
            }
        }
    }
}

/* ---------- Reusable pieces ---------- */

@Composable
private fun Avatar(name: String, c: Palette) {
    val initials = name.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.take(1).uppercase() }
    Box(
        Modifier.size(56.dp).clip(RoundedCornerShape(18.dp)).background(c.accent),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = c.onAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Stat(value: String, label: String, c: Palette, modifier: Modifier = Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(12.dp)).background(c.surface).padding(vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, color = c.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = c.muted, fontSize = 9.sp)
    }
}

@Composable
private fun Caption(text: String, c: Palette) {
    Text(
        text.uppercase(), color = c.muted, fontSize = 10.sp, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
}

@Composable
private fun ProjectCard(title: String, description: String?, c: Palette) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(c.surface).padding(12.dp)) {
        Text(title, color = c.ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        description?.let {
            Text(it, color = c.muted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
private fun ListRow(initial: String, title: String, subtitle: String, c: Palette) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(c.surface),
            contentAlignment = Alignment.Center,
        ) {
            Text(initial.uppercase(), color = c.accent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = c.ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = c.muted, fontSize = 10.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillChips(skills: List<String>, c: Palette) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        skills.take(12).forEach { s ->
            Box(Modifier.clip(RoundedCornerShape(7.dp)).background(c.surface).padding(horizontal = 9.dp, vertical = 4.dp)) {
                Text(s, color = c.accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TabBar(current: Tab, c: Palette, onSelect: (Tab) -> Unit) {
    Row(Modifier.fillMaxWidth().background(c.screen).height(56.dp)) {
        Tab.entries.forEach { t ->
            val selected = t == current
            Column(
                Modifier.weight(1f).fillMaxSize().clickable { onSelect(t) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(if (selected) c.accent else Color.Transparent))
                Spacer(Modifier.height(4.dp))
                Text(t.label, color = if (selected) c.accent else c.muted, fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun Text(
    text: String,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    modifier: Modifier = Modifier,
) = androidx.compose.material3.Text(
    text = text, color = color, fontSize = fontSize, fontWeight = fontWeight,
    maxLines = maxLines, overflow = overflow, modifier = modifier,
)
