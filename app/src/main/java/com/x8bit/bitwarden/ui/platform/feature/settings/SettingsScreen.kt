package com.x8bit.bitwarden.ui.platform.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.x8bit.bitwarden.R
import com.x8bit.bitwarden.ui.platform.base.util.EventsEffect
import com.x8bit.bitwarden.ui.platform.base.util.Text
import com.x8bit.bitwarden.ui.platform.base.util.bottomDivider
import com.x8bit.bitwarden.ui.platform.base.util.mirrorIfRtl
import com.x8bit.bitwarden.ui.platform.components.appbar.BitwardenMediumTopAppBar
import com.x8bit.bitwarden.ui.platform.components.scaffold.BitwardenScaffold
import com.x8bit.bitwarden.ui.platform.components.util.rememberVectorPainter
import com.x8bit.bitwarden.ui.platform.theme.BitwardenTheme

/**
 * Displays the settings screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit,
    onNavigateToAccountSecurity: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToAutoFill: () -> Unit,
    onNavigateToOther: () -> Unit,
    onNavigateToVault: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            SettingsEvent.NavigateAbout -> onNavigateToAbout()
            SettingsEvent.NavigateAccountSecurity -> onNavigateToAccountSecurity.invoke()
            SettingsEvent.NavigateAppearance -> onNavigateToAppearance()
            SettingsEvent.NavigateAutoFill -> onNavigateToAutoFill()
            SettingsEvent.NavigateOther -> onNavigateToOther()
            SettingsEvent.NavigateVault -> onNavigateToVault()
        }
    }

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    BitwardenScaffold(
        topBar = {
            BitwardenMediumTopAppBar(
                title = stringResource(id = R.string.settings),
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
        ) {
            Settings.entries.forEach {
                SettingsRow(
                    text = it.text,
                    onClick = remember(viewModel) {
                        { viewModel.trySendAction(SettingsAction.SettingsClick(it)) }
                    },
                    modifier = Modifier
                        .testTag(it.testTag)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SettingsRow(
    text: Text,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = MaterialTheme.colorScheme.primary),
                onClick = onClick,
            )
            .bottomDivider(paddingStart = 16.dp)
            .defaultMinSize(minHeight = 56.dp)
            .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
            .then(modifier),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f),
            text = text(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Icon(
            painter = rememberVectorPainter(id = R.drawable.ic_navigate_next),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .mirrorIfRtl()
                .size(24.dp),
        )
    }
}

@Preview
@Composable
private fun SettingsRows_preview() {
    BitwardenTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Settings.entries.forEach {
                SettingsRow(
                    text = it.text,
                    onClick = { },
                )
            }
        }
    }
}
