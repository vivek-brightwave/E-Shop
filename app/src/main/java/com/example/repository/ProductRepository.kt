package com.example.repository

import com.example.model.Product

class ProductRepository {
    // Hardcoded sample products for Grocery
    private val sampleProducts = listOf(
        Product("1", "Fresh Apple (1kg)", 180.0, "Fresh and crispy apples.", "https://images.unsplash.com/photo-1560806887-1e4cd0b6fd6c?auto=format&fit=crop&w=500", "Fruits"),
        Product("2", "Banana (12 pcs)", 70.0, "Fresh sweet bananas.", "https://images.unsplash.com/photo-1528825871115-3581a5387919?auto=format&fit=crop&w=500", "Fruits"),
        Product("3", "Mango (1kg)", 220.0, "Juicy tropical mangoes.", "https://images.unsplash.com/photo-1553284965-83fd3e82fa5a?auto=format&fit=crop&w=500", "Fruits"),
        Product("4", "Tomato (1kg)", 45.0, "Red farm fresh tomatoes.", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=500", "Vegetables"),
        Product("5", "Potato (1kg)", 40.0, "Organic potatoes.", "https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&w=500", "Vegetables"),
        Product("6", "Onion (1kg)", 50.0, "Fresh onions.", "https://images.unsplash.com/photo-1620574387735-3624d75b2dbc?auto=format&fit=crop&w=500", "Vegetables"),
        Product("7", "Carrot (1kg)", 65.0, "Crunchy healthy carrots.", "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&w=500", "Vegetables"),
        Product("8", "Broccoli", 90.0, "Fresh green broccoli.", "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?auto=format&fit=crop&w=500", "Vegetables"),
        Product("9", "Milk (1L)", 65.0, "Fresh cow milk.", "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=500", "Dairy"),
        Product("10", "Bread", 45.0, "Soft whole wheat bread.", "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=500", "Bakery"),
        Product("11", "Eggs (12 pcs)", 95.0, "Farm fresh brown eggs.", "https://images.unsplash.com/photo-1506976785307-8732e854ad03?auto=format&fit=crop&w=500", "Dairy"),
        Product("12", "Rice (5kg)", 420.0, "Premium quality basmati rice.", "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=500", "Grains")
    )

    fun getProducts(): List<Product> {
        return sampleProducts
    }

    fun getProductById(id: String): Product? {
        return sampleProducts.find { it.id == id }
    }
}
