package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItem)

    @Query("UPDATE cart_items SET quantity = quantity + 1 WHERE id = :id")
    suspend fun increaseQuantity(id: String)

    @Query("UPDATE cart_items SET quantity = quantity - 1 WHERE id = :id AND quantity > 1")
    suspend fun decreaseQuantity(id: String)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
