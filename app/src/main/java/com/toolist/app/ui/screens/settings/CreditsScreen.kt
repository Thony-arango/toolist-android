package com.toolist.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toolist.app.R
import com.toolist.app.ui.components.ToolistBottomNavBar
import com.toolist.app.ui.components.ToolistTab
import com.toolist.app.ui.theme.AvatarMd
import com.toolist.app.ui.theme.RadiusMd
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXl
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.ToolistTheme

// Datos del equipo (constantes — no cambian)

private data class TeamMember(val name: String, val initials: String)

// Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val members = listOf(
        TeamMember(stringResource(R.string.credits_member_1), "AA"),
        TeamMember(stringResource(R.string.credits_member_2), "CM"),
        TeamMember(stringResource(R.string.credits_member_3), "MA"),
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.credits_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            ToolistBottomNavBar(
                activeTab = ToolistTab.CREDITS,
                onNavigateToHome = onNavigateToHome,
                onNavigateToSearch = onNavigateToSearch,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToCredits = { /* ya estamos aquí */ },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(SpacingXl))

            Image(
                painter = painterResource(R.drawable.ic_toolist_logo),
                contentDescription = null,
                modifier = Modifier.size(72.dp),
            )

            Spacer(modifier = Modifier.height(SpacingMd))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = stringResource(R.string.credits_app_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.app_version),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(SpacingXl))
            HorizontalDivider(modifier = Modifier.padding(horizontal = SpacingMd))
            Spacer(modifier = Modifier.height(SpacingMd))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingMd),
                verticalArrangement = Arrangement.spacedBy(SpacingXs),
            ) {
                SectionHeaderCredits(
                    text = "INFORMACIÓN DEL PROYECTO",
                )

                Spacer(modifier = Modifier.height(SpacingXs))

                InfoRow(
                    label = stringResource(R.string.credits_label_subject),
                    value = stringResource(R.string.credits_subject_name),
                )
                InfoRow(
                    label = stringResource(R.string.credits_label_institution),
                    value = stringResource(R.string.credits_institution_name),
                )
                InfoRow(
                    label = stringResource(R.string.credits_label_year),
                    value = stringResource(R.string.credits_year),
                )
            }

            Spacer(modifier = Modifier.height(SpacingLg))
            HorizontalDivider(modifier = Modifier.padding(horizontal = SpacingMd))
            Spacer(modifier = Modifier.height(SpacingMd))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingMd),
                verticalArrangement = Arrangement.spacedBy(SpacingXs),
            ) {
                SectionHeaderCredits(text = stringResource(R.string.credits_section_team))

                Spacer(modifier = Modifier.height(SpacingXs))

                members.forEach { member ->
                    TeamMemberRow(member = member)
                }
            }

            Spacer(modifier = Modifier.height(SpacingXl))
        }
    }
}

// Encabezado de sección

@Composable
private fun SectionHeaderCredits(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

// Fila de información

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

// Fila de integrante del equipo

@Composable
private fun TeamMemberRow(member: TeamMember, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = SpacingXs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar con iniciales
        Surface(
            modifier = Modifier.size(AvatarMd),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = member.initials,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Spacer(modifier = Modifier.width(SpacingMd))

        Text(
            text = member.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

// Preview

@Preview(showSystemUi = true)
@Composable
private fun CreditsScreenPreview() {
    ToolistTheme {
        CreditsScreen(
            onNavigateToHome = {},
            onNavigateToSearch = {},
            onNavigateToSettings = {},
        )
    }
}
