package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.repository.AuthRepository
import com.example.repository.CartRepository
import com.example.repository.ProductRepository
import com.example.repository.OrderRepository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "eshop_database"
        ).build()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    }

    val cartRepository: CartRepository by lazy {
        CartRepository(database.cartDao())
    }

    val productRepository: ProductRepository by lazy {
        ProductRepository()
    }

    val orderRepository: OrderRepository by lazy {
        OrderRepository()
    }
}
