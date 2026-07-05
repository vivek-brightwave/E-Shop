package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.CartItemModel

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int
)

fun CartItem.toModel() = CartItemModel(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    quantity = quantity
)

fun CartItemModel.toEntity() = CartItem(
    id = id,
    name = name,
    price = price,
    imageUrl = imageUrl,
    quantity = quantity
)
