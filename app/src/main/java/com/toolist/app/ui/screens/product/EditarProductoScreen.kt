package com.toolist.app.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.toolist.app.R
import com.toolist.app.domain.model.Product
import com.toolist.app.domain.model.ProductStatus
import com.toolist.app.domain.model.ShoppingList
import com.toolist.app.ui.theme.ButtonHeight
import com.toolist.app.ui.theme.ButtonRadius
import com.toolist.app.ui.theme.IconMd
import com.toolist.app.ui.theme.SpacingLg
import com.toolist.app.ui.theme.SpacingMd
import com.toolist.app.ui.theme.SpacingSm
import com.toolist.app.ui.theme.SpacingXs
import com.toolist.app.ui.theme.TextFieldRadius
import com.toolist.app.ui.theme.ToolistTheme

// Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    uiState: AgregarProductoUiState,
    onSaveClick: (
        name: String,
        targetListId: String,
        quantity: String,
        unit: String,
        categoryName: String?,
        estimatedPrice: String,
        status: ProductStatus,
        notes: String,
    ) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("1") }
    var estimatedPrice by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var selectedStatus by rememberSaveable { mutableStateOf(ProductStatus.PENDING) }
    var prefilled by rememberSaveable { mutableStateOf(false) }

    var listDropdownExpanded by remember { mutableStateOf(false) }
    var selectedListId by rememberSaveable { mutableStateOf<String?>(null) }

    var unitDropdownExpanded by remember { mutableStateOf(false) }
    val units = listOf(
        stringResource(R.string.unit_unit),
        stringResource(R.string.unit_kg),
        stringResource(R.string.unit_g),
        stringResource(R.string.unit_l),
        stringResource(R.string.unit_ml),
        stringResource(R.string.unit_pack),
        stringResource(R.string.unit_dozen),
        stringResource(R.string.unit_box),
    )
    var selectedUnit by rememberSaveable { mutableStateOf(units.first()) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    val categories = listOf(
        null to stringResource(R.string.add_product_label_category),
        "dairy" to stringResource(R.string.category_dairy),
        "meat" to stringResource(R.string.category_meat),
        "vegetables" to stringResource(R.string.category_vegetables),
        "fruits" to stringResource(R.string.category_fruits),
        "bakery" to stringResource(R.string.category_bakery),
        "cleaning" to stringResource(R.string.category_cleaning),
        "personal_care" to stringResource(R.string.category_personal_care),
        "beverages" to stringResource(R.string.category_beverages),
        "snacks" to stringResource(R.string.category_snacks),
        "frozen" to stringResource(R.string.category_frozen),
        "other" to stringResource(R.string.category_other),
    )
    var selectedCategoryName by rememberSaveable { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.existingProduct) {
        val product = uiState.existingProduct
        if (product != null && !prefilled) {
            name = product.name
            quantity = if (product.quantity == product.quantity.toLong().toDouble())
                product.quantity.toLong().toString()
            else product.quantity.toString()
            selectedUnit = if (units.contains(product.unit)) product.unit else units.first()
            selectedCategoryName = product.categoryName
            estimatedPrice = if (product.estimatedPrice > 0)
                product.estimatedPrice.toLong().toString()
            else ""
            selectedStatus = product.status
            notes = product.notes ?: ""
            selectedListId = product.listId
            prefilled = true
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) snackbarHostState.showSnackbar(uiState.error)
    }

    val selectedListName = uiState.lists.firstOrNull { it.id == selectedListId }?.name ?: ""

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_product_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = SpacingMd)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SpacingMd),
        ) {
            Spacer(modifier = Modifier.height(SpacingXs))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.add_product_label_name)) },
                placeholder = { Text(stringResource(R.string.add_product_hint_name)) },
                isError = uiState.nameError != null,
                supportingText = uiState.nameError?.let {
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(TextFieldRadius),
            )

            ExposedDropdownMenuBox(
                expanded = listDropdownExpanded,
                onExpandedChange = { listDropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = selectedListName,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    label = { Text(stringResource(R.string.add_product_label_target_list)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = listDropdownExpanded) },
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(TextFieldRadius),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = listDropdownExpanded,
                    onDismissRequest = { listDropdownExpanded = false },
                ) {
                    uiState.lists.forEach { list ->
                        DropdownMenuItem(
                            text = { Text(list.name) },
                            onClick = {
                                selectedListId = list.id
                                listDropdownExpanded = false
                            },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpacingSm),
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.add_product_label_quantity)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(TextFieldRadius),
                )

                ExposedDropdownMenuBox(
                    expanded = unitDropdownExpanded,
                    onExpandedChange = { unitDropdownExpanded = it },
                    modifier = Modifier.weight(1f),
                ) {
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        label = { Text(stringResource(R.string.add_product_label_unit)) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = null,
                            )
                        },
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(TextFieldRadius),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = unitDropdownExpanded,
                        onDismissRequest = { unitDropdownExpanded = false },
                    ) {
                        units.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    selectedUnit = unit
                                    unitDropdownExpanded = false
                                },
                            )
                        }
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = selectedCategoryName ?: stringResource(R.string.add_product_label_category),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    label = { Text(stringResource(R.string.add_product_label_category)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(TextFieldRadius),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false },
                ) {
                    categories.forEach { (key, displayName) ->
                        DropdownMenuItem(
                            text = { Text(displayName) },
                            onClick = {
                                selectedCategoryName = if (key == null) null else displayName
                                categoryDropdownExpanded = false
                            },
                        )
                    }
                }
            }

            OutlinedTextField(
                value = estimatedPrice,
                onValueChange = { estimatedPrice = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.add_product_label_price)) },
                placeholder = { Text(stringResource(R.string.add_product_hint_price)) },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(TextFieldRadius),
            )

            Column {
                Text(
                    text = stringResource(R.string.add_product_label_initial_state),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(SpacingXs))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedStatus == ProductStatus.PENDING,
                        onClick = { selectedStatus = ProductStatus.PENDING },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    ) {
                        Text(stringResource(R.string.add_product_state_pending))
                    }
                    SegmentedButton(
                        selected = selectedStatus == ProductStatus.PURCHASED,
                        onClick = { selectedStatus = ProductStatus.PURCHASED },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    ) {
                        Text(stringResource(R.string.add_product_state_purchased))
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text(stringResource(R.string.add_product_label_notes)) },
                placeholder = { Text(stringResource(R.string.add_product_hint_notes)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                maxLines = 4,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(TextFieldRadius),
            )

            Spacer(modifier = Modifier.height(SpacingLg))

            Button(
                onClick = {
                    onSaveClick(
                        name,
                        selectedListId ?: uiState.existingProduct?.listId ?: "",
                        quantity,
                        selectedUnit,
                        selectedCategoryName,
                        estimatedPrice,
                        selectedStatus,
                        notes,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ButtonHeight),
                shape = RoundedCornerShape(ButtonRadius),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(IconMd),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.add_product_btn_save),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingLg))
        }
    }
}

// Preview

@Preview(showSystemUi = true)
@Composable
private fun EditarProductoScreenPreview() {
    ToolistTheme {
        EditarProductoScreen(
            uiState = AgregarProductoUiState(
                lists = listOf(
                    ShoppingList(
                        id = "l1",
                        name = "Mercado Semanal",
                        description = "",
                        colorHex = "#16A34A",
                        totalEstimated = 0.0,
                        purchasedCount = 0,
                        totalCount = 0,
                        createdAt = 0L,
                    ),
                ),
                existingProduct = Product(
                    id = "p1",
                    listId = "l1",
                    name = "Leche entera",
                    quantity = 2.0,
                    unit = "L",
                    categoryId = null,
                    categoryName = "Lácteos",
                    estimatedPrice = 5200.0,
                    status = ProductStatus.PENDING,
                    notes = "Comprar marca Colanta",
                    imageUrl = null,
                ),
            ),
            onSaveClick = { _, _, _, _, _, _, _, _ -> },
            onNavigateBack = {},
        )
    }
}
