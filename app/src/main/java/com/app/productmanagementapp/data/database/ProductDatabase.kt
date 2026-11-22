package com.app.productmanagementapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.productmanagementapp.data.dao.ProductDao
import com.app.productmanagementapp.data.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
}
