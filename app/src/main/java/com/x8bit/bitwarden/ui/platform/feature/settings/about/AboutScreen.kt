package com.x8bit.bitwarden.ui.platform.feature.settings.about

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.x8bit.bitwarden.R
import com.x8bit.bitwarden.data.platform.util.toAnnotatedString
import com.x8bit.bitwarden.ui.platform.base.util.EventsEffect
import com.x8bit.bitwarden.ui.platform.base.util.IntentHandler
import com.x8bit.bitwarden.ui.platform.base.util.Text
import com.x8bit.bitwarden.ui.platform.base.util.asText
import com.x8bit.bitwarden.ui.platform.components.BitwardenExternalLinkRow
import com.x8bit.bitwarden.ui.platform.components.BitwardenTopAppBar
import com.x8bit.bitwarden.ui.platform.components.BitwardenWideSwitch
import com.x8bit.bitwarden.ui.platform.theme.BitwardenTheme

/**
 * Displays the about screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit,
    viewModel: AboutViewModel = hiltViewModel(),
    clipboardManager: ClipboardManager = LocalClipboardManager.current,
    intentHandler: IntentHandler = IntentHandler(context = LocalContext.current),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resources = context.resources
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is AboutEvent.CopyToClipboard -> {
                clipboardManager.setText(event.text.toString(resources).toAnnotatedString())
            }

            AboutEvent.NavigateBack -> onNavigateBack.invoke()

            AboutEvent.NavigateToHelpCenter -> {
                intentHandler.launchUri("https://bitwarden.com/help".toUri())
            }

            AboutEvent.NavigateToLearnAboutOrganizations -> {
                intentHandler.launchUri("https://bitwarden.com/help/about-organizations".toUri())
            }

            AboutEvent.NavigateToWebVault -> {
                intentHandler.launchUri("https://vault.bitwarden.com".toUri())
            }

            is AboutEvent.ShowToast -> {
                Toast.makeText(context, event.text(resources), Toast.LENGTH_SHORT).show()
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = stringResource(id = R.string.about),
                scrollBehavior = scrollBehavior,
                navigationIcon = painterResource(id = R.drawable.ic_back),
                navigationIconContentDescription = stringResource(id = R.string.back),
                onNavigationIconClick = remember(viewModel) {
                    { viewModel.trySendAction(AboutAction.BackClick) }
                },
            )
        },
    ) { innerPadding ->
        ContentColum(
            state = state,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            onHelpCenterClick = remember(viewModel) {
                { viewModel.trySendAction(AboutAction.HelpCenterClick) }
            },
            onLearnAboutOrgsClick = remember(viewModel) {
                { viewModel.trySendAction(AboutAction.LearnAboutOrganizationsClick) }
            },
            onRateTheAppClick = remember(viewModel) {
                { viewModel.trySendAction(AboutAction.RateAppClick) }
            },
            onSubmitCrashLogsCheckedChange = remember(viewModel) {
                { viewModel.trySendAction(AboutAction.SubmitCrashLogsClick(it)) }
            },
            onVersionClick = remember(viewModel) {
                { viewModel.trySendAction(AboutAction.VersionClick) }
            },
            onWebVaultClick = remember(viewModel) {
                { viewModel.trySendAction(AboutAction.WebVaultClick) }
            },
        )
    }
}

@Composable
private fun ContentColum(
    state: AboutState,
    onHelpCenterClick: () -> Unit,
    onLearnAboutOrgsClick: () -> Unit,
    onRateTheAppClick: () -> Unit,
    onSubmitCrashLogsCheckedChange: (Boolean) -> Unit,
    onVersionClick: () -> Unit,
    onWebVaultClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        BitwardenWideSwitch(
            label = stringResource(id = R.string.submit_crash_logs),
            isChecked = state.isSubmitCrashLogsEnabled,
            onCheckedChange = onSubmitCrashLogsCheckedChange,
            modifier = Modifier
                .defaultMinSize(56.dp)
                .padding(horizontal = 16.dp),
            contentDescription = stringResource(id = R.string.submit_crash_logs),
        )
        Spacer(modifier = Modifier.height(16.dp))
        BitwardenExternalLinkRow(
            text = R.string.bitwarden_help_center.asText(),
            onClick = onHelpCenterClick,
        )
        BitwardenExternalLinkRow(
            text = R.string.web_vault.asText(),
            onClick = onWebVaultClick,
        )
        BitwardenExternalLinkRow(
            text = R.string.learn_org.asText(),
            onClick = onLearnAboutOrgsClick,
        )
        BitwardenExternalLinkRow(
            text = R.string.rate_the_app.asText(),
            onClick = onRateTheAppClick,
        )
        CopyRow(
            text = state.version,
            onClick = onVersionClick,
        )
        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = 56.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "© Bitwarden Inc. 2015-2023",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun CopyRow(
    text: Text,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val resources = LocalContext.current.resources
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                onClick = onClick,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = text.toString(resources)
            },
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 56.dp)
                .padding(start = 16.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .weight(1f),
                text = text(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_copy),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Preview
@Composable
private fun CopyRow_preview() {
    BitwardenTheme {
        CopyRow(
            text = "Copyable Text".asText(),
            onClick = { },
        )
    }
}