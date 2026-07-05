package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.ShopViewModel
import com.example.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel,
    viewModel: ShopViewModel
) {
    val user by authViewModel.currentUser.collectAsState()
    val orders by viewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            user?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (it.photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = it.photoUrl,
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text(it.name, style = MaterialTheme.typography.headlineSmall)
                    Text(it.email, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        authViewModel.signOut()
                        onLogout()
                    }) {
                        Text("Sign Out")
                    }
                }
            }

            HorizontalDivider()
            
            Text(
                "Order History",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(orders) { order ->
                    val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(order.orderDate))
                    ListItem(
                        headlineContent = { Text("Order: ${order.orderId.take(8)}") },
                        supportingContent = { Text("Date: $date\nStatus: ${order.status}") },
                        trailingContent = { Text("₹${order.totalPrice.toInt()}") }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
