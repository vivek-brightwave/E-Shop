package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.CartScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProductDetailsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.ViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel

import android.util.Log
import android.widget.Toast
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.example.viewmodel.ShopViewModel

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {
    var shopViewModel: ShopViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(this)
                }
            }
        }
    }

    override fun onPaymentSuccess(paymentId: String?, paymentData: PaymentData?) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        val amountStr = paymentData?.data?.optString("amount") ?: "0"
        val amount = try { amountStr.toDouble() / 100 } catch (e: Exception) { 0.0 }
        shopViewModel?.handlePaymentSuccess(paymentId ?: "", amount)
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        Log.e("MainActivity", "Payment failed $code $response")
        Toast.makeText(this, "Payment failed: $response", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun AppNavigation(mainActivity: MainActivity) {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val appContainer = (context.applicationContext as EShopApplication).container
    val viewModelFactory = ViewModelFactory(appContainer)
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val user by authViewModel.currentUser.collectAsState()
    val shopViewModel: ShopViewModel = viewModel(factory = viewModelFactory)
    mainActivity.shopViewModel = shopViewModel

    val startDestination = "splash"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                val nextDestination = if (user != null) "home" else "login"
                navController.navigate(nextDestination) {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                viewModel = shopViewModel,
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToProduct = { productId ->
                    navController.navigate("product/$productId")
                }
            )
        }
        composable("product/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailsScreen(
                productId = productId,
                viewModel = shopViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCart = { navController.navigate("cart") }
            )
        }
        composable("cart") {
            CartScreen(
                viewModel = shopViewModel,
                onNavigateBack = { navController.popBackStack() },
                onCheckout = { navController.navigate("checkout") }
            )
        }
        composable("checkout") {
            CheckoutScreen(
                viewModel = shopViewModel,
                activity = mainActivity,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                viewModel = shopViewModel,
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
