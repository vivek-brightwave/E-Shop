package com.example.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = ""
)

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val category: String = ""
)

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartItemModel> = emptyList(),
    val totalPrice: Double = 0.0,
    val paymentId: String = "",
    val orderDate: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS"
)

data class CartItemModel(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val quantity: Int = 1
)
