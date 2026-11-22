package com.app.productmanagementapp.data.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    val productId: Int = 0,

    @ColumnInfo(name = "product_name")
    val productName: String,

    @ColumnInfo(name = "product_cost_paise")
    val productCostPaise: Long,

    @ColumnInfo(name = "quantity")
    val quantity: Int

) {

    /******************************************
     * Calculates the total product price.
     *
     * Formula:
     * Product Cost × Quantity
     ******************************************/

    val totalPricePaise: Long
        get() = productCostPaise * quantity
}

