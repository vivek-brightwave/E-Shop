package com.example.viewmodel

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.CartItemModel
import com.example.model.Order
import com.example.model.Product
import com.example.repository.AuthRepository
import com.example.repository.CartRepository
import com.example.repository.OrderRepository
import com.example.repository.ProductRepository
import com.razorpay.Checkout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

class ShopViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts = _filteredProducts.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories = _categories.asStateFlow()

    val cartItems: StateFlow<List<CartItemModel>> = cartRepository.allCartItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        val allProds = productRepository.getProducts()
        _products.value = allProds
        _filteredProducts.value = allProds
        _categories.value = allProds.map { it.category }.distinct()
    }

    fun searchProducts(query: String) {
        val allProds = _products.value
        if (query.isBlank()) {
            _filteredProducts.value = allProds
        } else {
            _filteredProducts.value = allProds.filter {
                it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
            }
        }
    }

    fun filterByCategory(category: String) {
        val allProds = _products.value
        _filteredProducts.value = allProds.filter { it.category == category }
    }
    
    fun getProductById(id: String): Product? {
        return productRepository.getProductById(id)
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val item = CartItemModel(
                id = product.id,
                name = product.name,
                price = product.price,
                imageUrl = product.imageUrl,
                quantity = 1
            )
            cartRepository.insertItem(item)
        }
    }

    fun increaseQuantity(id: String) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(id)
        }
    }

    fun decreaseQuantity(id: String) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(id)
        }
    }

    fun removeCartItem(id: String) {
        viewModelScope.launch {
            cartRepository.deleteItem(id)
        }
    }

    fun loadOrders() {
        val user = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val res = orderRepository.getUserOrders(user.id)
            if (res.isSuccess) {
                _orders.value = res.getOrNull() ?: emptyList()
            }
            _isLoading.value = false
        }
    }

    fun startCheckout(activity: Activity, totalAmount: Double) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_SE1IwALnkdPlvy")
        try {
            val options = JSONObject()
            options.put("name", "E-Shop")
            options.put("description", "Grocery Order")
            // options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc")
            options.put("currency", "INR")
            options.put("amount", (totalAmount * 100).toInt().toString())//pass amount in currency subunits
            
            val preFill = JSONObject()
            preFill.put("email", authRepository.currentUser.value?.email ?: "")
            options.put("prefill", preFill)

            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e("ShopViewModel", "Error in starting Razorpay Checkout", e)
            Toast.makeText(activity, "Error in payment: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun handlePaymentSuccess(paymentId: String, totalAmount: Double) {
        val user = authRepository.currentUser.value ?: return
        viewModelScope.launch {
            val items = cartItems.value
            val order = Order(
                orderId = UUID.randomUUID().toString(),
                userId = user.id,
                items = items,
                totalPrice = totalAmount,
                paymentId = paymentId,
                status = "SUCCESS"
            )
            val result = orderRepository.saveOrder(order)
            if (result.isSuccess) {
                cartRepository.clearCart()
            }
        }
    }
}
