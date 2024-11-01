package com.example.inventory.ui.settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.navigation.NavigationDestination
import com.example.inventory.ui.theme.InventoryTheme
object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_title
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            InventoryTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) { innerPadding ->
        SettingsBody(
            settingsUiState = viewModel.settingsUiState,
            onSettingsChanged = viewModel::update,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
        )
    }
}
@Composable
private fun SettingsBody(
    settingsUiState: SettingsUiState,
    onSettingsChanged: (SettingsUiState) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        SettingCheckbox(
            label = "Hide important data",
            checked = settingsUiState.hideImportantData,
            onValueChanged = {
                onSettingsChanged(settingsUiState.copy(hideImportantData = it))
            }
        )
        SettingCheckbox(
            label = "Prohibit sending data",
            checked = settingsUiState.prohibitSendingData,
            onValueChanged = {
                onSettingsChanged(settingsUiState.copy(prohibitSendingData = it))
            }
        )
        SettingCheckbox(
            label = "Use default items quantity",
            checked = settingsUiState.useDefaultItemsQuantity,
            onValueChanged = {
                onSettingsChanged(settingsUiState.copy(useDefaultItemsQuantity = it))
            }
        )
        if (settingsUiState.useDefaultItemsQuantity) {
            OutlinedTextField(
                value = settingsUiState.defaultItemsQuantity.toString(),
                onValueChange = {
                    onSettingsChanged(settingsUiState.copy(defaultItemsQuantity = it.toInt()))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text("Default items quantity") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
@Composable
private fun SettingCheckbox(
    label: String,
    checked: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Checkbox(
            checked = checked,
            onCheckedChange = onValueChanged
        )
    }
}
@Preview(showBackground = true)
@Composable
fun SettingsBodyPreview() {
    InventoryTheme {
        SettingsBody(
            settingsUiState = SettingsUiState(),
            onSettingsChanged = {}
        )
    }
}