package com.digitalbank.ui.accounts

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.digitalbank.model.Customer
import com.digitalbank.model.KycStatus
import com.digitalbank.ui.UiState
import com.digitalbank.ui.theme.*
import com.digitalbank.util.maskIban
import com.digitalbank.util.toIndianRupee
import com.digitalbank.util.toInitials
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun AccountsScreen(
    viewModel: AccountsViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCamera: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedChip by viewModel.selectedChip.collectAsStateWithLifecycle()
    val customers by viewModel.filteredCustomers.collectAsStateWithLifecycle()

    // Focus manager for search
    val focusManager = LocalFocusManager.current

    // Debounced search logic
    var searchInput by remember { mutableStateOf(searchQuery) }
    LaunchedEffect(searchInput) {
        snapshotFlow { searchInput }
            .debounce(300)
            .collect { viewModel.searchQuery.value = it }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy900)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DigitalBank",
                            color = White90,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Relationship Manager",
                            color = White50,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                        )
                    }
                    IconButton(
                        onClick = { /* Toggle Theme - always dark here */ },
                        modifier = Modifier
                            .background(Navy800, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "Theme",
                            tint = Electric
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                TextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Navy700),
                    placeholder = {
                        Text("Search customer or account...", color = White50)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = White50
                        )
                    },
                    trailingIcon = {
                        if (searchInput.isNotEmpty()) {
                            IconButton(onClick = { searchInput = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = White50
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Navy700,
                        unfocusedContainerColor = Navy700,
                        focusedTextColor = White90,
                        unfocusedTextColor = White90,
                        cursorColor = Electric,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                )
            }
        },
        containerColor = Navy900
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chips row
            AccountTypeChipsRow(
                selectedChip = selectedChip,
                onChipSelected = { viewModel.selectedChip.value = it }
            )

            // Tabs verified/pending row
            TabRowSection(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.selectedTab.value = it }
            )

            // Content Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        ShimmerGrid()
                    }
                    is UiState.Error -> {
                        ErrorStateView(message = state.message, onRetry = { viewModel.retry() })
                    }
                    is UiState.Empty -> {
                        EmptyStateView()
                    }
                    is UiState.Success -> {
                        if (customers.isEmpty()) {
                            EmptyStateView(message = "No customers found")
                        } else {
                            CustomerGrid(
                                customers = customers,
                                onNavigateToDetail = onNavigateToDetail,
                                onNavigateToCamera = onNavigateToCamera,
                                onLoadMore = { viewModel.loadMore() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountTypeChipsRow(
    selectedChip: String,
    onChipSelected: (String) -> Unit
) {
    val chips = listOf("All", "Savings", "Current", "NRI")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            val isSelected = chip == selectedChip
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onChipSelected(chip) },
                color = if (isSelected) Electric else Navy700
            ) {
                Text(
                    text = chip,
                    color = if (isSelected) White90 else White50,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TabRowSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Navy900,
        contentColor = White90,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .width(24.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50)),
                color = Electric
            )
        },
        divider = {
            HorizontalDivider(color = CardStroke)
        }
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = {
                Text(
                    "VERIFIED",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = if (selectedTab == 0) White90 else White50
                )
            }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = {
                Text(
                    "PENDING",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = if (selectedTab == 1) White90 else White50
                )
            }
        )
    }
}

@Composable
fun CustomerGrid(
    customers: List<Customer>,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCamera: (Int) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyGridState()

    // Trigger pagination when reaching end
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 4
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(customers, key = { it.id }) { customer ->
            CustomerCard(
                customer = customer,
                onCardClick = { onNavigateToDetail(customer.id) },
                onKycClick = { onNavigateToCamera(customer.id) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    onCardClick: () -> Unit,
    onKycClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, CardStroke),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(Navy800, Navy900)
                    )
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Top row with status badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val badgeBg = if (customer.kycStatus == KycStatus.VERIFIED) Emerald else Amber
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = badgeBg,
                        modifier = Modifier.height(20.dp)
                    ) {
                        Text(
                            text = customer.kycStatus.name,
                            color = Navy900,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Navy700),
                    contentAlignment = Alignment.Center
                ) {
                    val imageSource = customer.selfiePath ?: customer.imageUrl
                    if (imageSource != null) {
                        AsyncImage(
                            model = imageSource,
                            contentDescription = customer.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = customer.name.toInitials(),
                            color = White90,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Name
                Text(
                    text = customer.name,
                    color = White90,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )

                // IBAN Masked
                Text(
                    text = "A/C ${customer.maskedIban}",
                    color = White50,
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Balance
                Text(
                    text = customer.balance.toIndianRupee(),
                    color = White90,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )

                // Type Chip
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Navy700,
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .height(22.dp)
                ) {
                    Text(
                        text = customer.accountType,
                        color = White50,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                // Do KYC button if PENDING
                if (customer.kycStatus == KycStatus.PENDING) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onKycClick() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Electric,
                            contentColor = White90
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Text(
                            "Do KYC",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerGrid() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(6) {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CardStroke),
                colors = CardDefaults.cardColors(containerColor = Navy800),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .alpha(alphaAnim)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .size(40.dp, 16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Navy700)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Navy700)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(100.dp, 16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Navy700)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(60.dp, 12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Navy700)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .size(80.dp, 20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Navy700)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            tint = Rose,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = White90,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRetry,
            border = BorderStroke(1.dp, Electric),
            shape = RoundedCornerShape(50)
        ) {
            Text("Retry", color = Electric)
        }
    }
}

@Composable
fun EmptyStateView(
    message: String = "No customers found"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Empty",
            tint = White50,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = White50,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
