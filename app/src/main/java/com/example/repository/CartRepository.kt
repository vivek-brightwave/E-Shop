package com.example.repository

import com.example.data.local.CartDao
import com.example.data.local.CartItem
import com.example.data.local.toEntity
import com.example.data.local.toModel
import com.example.model.CartItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepository(private val cartDao: CartDao) {
    val allCartItems: Flow<List<CartItemModel>> = cartDao.getAllCartItems().map { items ->
        items.map { it.toModel() }
    }

    suspend fun insertItem(item: CartItemModel) {
        cartDao.insertCartItem(item.toEntity())
    }

    suspend fun increaseQuantity(id: String) {
        cartDao.increaseQuantity(id)
    }

    suspend fun decreaseQuantity(id: String) {
        cartDao.decreaseQuantity(id)
    }

    suspend fun deleteItem(id: String) {
        cartDao.deleteCartItem(id)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
