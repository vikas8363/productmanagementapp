package com.app.productmanagementapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.productmanagementapp.data.entity.ProductEntity

/***
 * Product DAO
 *
 * Responsible for all database operations
 * related to the products table.
 *
 * Room automatically generates the required
 * implementation at compile time.
 ****/

@Dao
interface ProductDao {

    /**Insert a new product.**/
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProduct(product: ProductEntity): Long


    /***
     Update an existing product.
     Returns the number of affected rows.
     **/
    @Update
    suspend fun updateProduct(product: ProductEntity): Int

    /**
     Deletes a product using its primary key.
     Returns the number of deleted rows.
     **/

    @Query(
        """
    DELETE FROM products
    WHERE product_id = :productId
    """
    )
    suspend fun deleteProductById(productId: Int): Int


    /**Retrieve a product using its primary key.**/

    @Query(
        """
        SELECT * FROM products
        WHERE product_id = :productId
        LIMIT 1
        """
    )
    suspend fun getProductById(productId: Int): ProductEntity?


    /**
      Retrieve products sorted by name.
      A -> Z
     ***/

    @Query(
        """
        SELECT * FROM products
        ORDER BY product_name COLLATE NOCASE ASC,
                 product_id ASC
        """
    )
    fun observeProductsByNameAscending(): LiveData<List<ProductEntity>>

    /**
      Retrieve products sorted by name.
      Z -> A
     **/

    @Query(
        """
        SELECT * FROM products
        ORDER BY product_name COLLATE NOCASE DESC,
                 product_id ASC
        """
    )
    fun observeProductsByNameDescending(): LiveData<List<ProductEntity>>


    /**
      Retrieve products sorted by total price.
      Low -> High
    **/

    @Query(
        """
        SELECT * FROM products
        ORDER BY (product_cost_paise * quantity) ASC,
                 product_id ASC
        """
    )
    fun observeProductsByPriceAscending(): LiveData<List<ProductEntity>>


    /**
      Retrieve products sorted by total price.
      High -> Low
     **/

    @Query(
        """
        SELECT * FROM products
        ORDER BY (product_cost_paise * quantity) DESC,
                 product_id ASC
        """
    )
    fun observeProductsByPriceDescending(): LiveData<List<ProductEntity>>


}

