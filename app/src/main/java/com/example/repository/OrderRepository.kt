package com.example.repository

import android.util.Log
import com.example.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveOrder(order: Order): Result<Unit> {
        return try {
            firestore.collection("orders").document(order.orderId).set(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("OrderRepository", "Failed to save order", e)
            Result.failure(e)
        }
    }

    suspend fun getUserOrders(userId: String): Result<List<Order>> {
        return try {
            val snapshot = firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val orders = snapshot.toObjects(Order::class.java)
            Result.success(orders)
        } catch (e: Exception) {
            Log.e("OrderRepository", "Failed to get user orders", e)
            Result.failure(e)
        }
    }
}
