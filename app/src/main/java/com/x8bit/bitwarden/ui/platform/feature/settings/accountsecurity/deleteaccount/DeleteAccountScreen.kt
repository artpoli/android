package com.x8bit.bitwarden.ui.platform.feature.settings.accountsecurity.deleteaccount

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.x8bit.bitwarden.R
import com.x8bit.bitwarden.ui.platform.base.util.EventsEffect
import com.x8bit.bitwarden.ui.platform.base.util.asText
import com.x8bit.bitwarden.ui.platform.components.BasicDialogState
import com.x8bit.bitwarden.ui.platform.components.BitwardenBasicDialog
import com.x8bit.bitwarden.ui.platform.components.BitwardenErrorButton
import com.x8bit.bitwarden.ui.platform.components.BitwardenLoadingDialog
import com.x8bit.bitwarden.ui.platform.components.BitwardenMasterPasswordDialog
import com.x8bit.bitwarden.ui.platform.components.BitwardenOutlinedButton
import com.x8bit.bitwarden.ui.platform.components.BitwardenScaffold
import com.x8bit.bitwarden.ui.platform.components.BitwardenTopAppBar
import com.x8bit.bitwarden.ui.platform.components.LoadingDialogState

/**
 * Displays the delete account screen.
 */
@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(
    viewModel: DeleteAccountViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    val context = LocalContext.current
    val resources = context.resources
    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            DeleteAccountEvent.NavigateBack -> onNavigateBack()

            is DeleteAccountEvent.ShowToast -> {
                Toast.makeText(context, event.message(resources), Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (val dialog = state.dialog) {
        is DeleteAccountState.DeleteAccountDialog.Error -> BitwardenBasicDialog(
            visibilityState = BasicDialogState.Shown(
                title = R.string.an_error_has_occurred.asText(),
                message = dialog.message,
            ),
            onDismissRequest = remember(viewModel) {
                { viewModel.trySendAction(DeleteAccountAction.DismissDialog) }
            },
        )

        DeleteAccountState.DeleteAccountDialog.Loading,

        -> BitwardenLoadingDialog(
            visibilityState = LoadingDialogState.Shown(R.string.loading.asText()),
        )

        null -> Unit
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    BitwardenScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BitwardenTopAppBar(
                title = stringResource(id = R.string.delete_account),
                scrollBehavior = scrollBehavior,
                navigationIcon = painterResource(id = R.drawable.ic_close),
                navigationIconContentDescription = stringResource(id = R.string.close),
                onNavigationIconClick = remember(viewModel) {
                    { viewModel.trySendAction(DeleteAccountAction.CloseClick) }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .imePadding()
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_warning),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.deleting_your_account_is_permanent),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.delete_account_explanation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(24.dp))
            DeleteAccountButton(
                onConfirmationClick = remember(viewModel) {
                    { viewModel.trySendAction(DeleteAccountAction.DeleteAccountClick(it)) }
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            BitwardenOutlinedButton(
                label = stringResource(id = R.string.cancel),
                onClick = remember(viewModel) {
                    { viewModel.trySendAction(DeleteAccountAction.CancelClick) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun DeleteAccountButton(
    onConfirmationClick: (masterPassword: String) -> Unit,
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    if (showPasswordDialog) {
        BitwardenMasterPasswordDialog(
            onConfirmClick = {
                showPasswordDialog = false
                onConfirmationClick(it)
            },
            onDismissRequest = { showPasswordDialog = false },
        )
    }

    BitwardenErrorButton(
        label = stringResource(id = R.string.delete_account),
        onClick = { showPasswordDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}