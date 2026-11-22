package com.app.productmanagementapp.data.repository


import android.database.sqlite.SQLiteException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import androidx.lifecycle.LiveData
import com.app.productmanagementapp.core.common.AppError
import com.app.productmanagementapp.core.common.ProductSortType
import com.app.productmanagementapp.core.common.Resource
import com.app.productmanagementapp.data.dao.ProductDao
import com.app.productmanagementapp.data.entity.ProductEntity

/**
 * Product Repository
 *
 * Responsibilities:
 *
 * 1. Handle all product database operations.
 * 2. Communicate with ProductDao.
 * 3. Handle database-related exceptions.
 * 4. Convert exceptions into AppError.
 * 5. Provide sorted product observations.
 *
 * ViewModel communicates with this class
 * instead of directly accessing ProductDao.
 ***/

class ProductRepo @Inject constructor(

    private val productDao: ProductDao

) {

    /**
     * Inserts a new product into Room.
     * @return Generated product ID on success.
     **/

    suspend fun insertProduct(
        product: ProductEntity
    ): Resource<Long> {

        return executeDatabaseOperation {

            productDao.insertProduct(product)

        }
    }


    /**
     * Updates an existing product.
     * @return Unit when the update succeeds.
     **/

    suspend fun updateProduct(
        product: ProductEntity
    ): Resource<Unit> {

        return executeDatabaseOperation {

            val updatedRows =
                productDao.updateProduct(product)

            if (updatedRows == 0) {
                throw ProductOperationException.ProductNotFound
            }

            Unit
        }
    }


    /**
     * Deletes a product using its ID.
     * @return Unit when deletion succeeds.
     ***/

    suspend fun deleteProduct(
        productId: Int
    ): Resource<Unit> {

        return executeDatabaseOperation {

            val deletedRows =
                productDao.deleteProductById(productId)

            if (deletedRows == 0) {
                throw ProductOperationException.ProductNotFound
            }

            Unit
        }
    }


    /**
     * Retrieves an individual product.
     **/

    suspend fun getProductById(
        productId: Int
    ): Resource<ProductEntity> {

        return executeDatabaseOperation {

            productDao.getProductById(productId)
                ?: throw ProductOperationException.ProductNotFound
        }
    }


    /**
     * Observes products using the selected
     * sorting configuration.
     * Room automatically updates LiveData
     * whenever the products table changes.
     ***/

    fun observeProducts(
        sortType: ProductSortType
    ): LiveData<List<ProductEntity>> {

        return when (sortType) {

            ProductSortType.NAME_ASCENDING -> {

                productDao.observeProductsByNameAscending()
            }

            ProductSortType.NAME_DESCENDING -> {

                productDao.observeProductsByNameDescending()
            }

            ProductSortType.PRICE_ASCENDING -> {

                productDao.observeProductsByPriceAscending()
            }

            ProductSortType.PRICE_DESCENDING -> {

                productDao.observeProductsByPriceDescending()
            }
        }
    }


    /**
     * Centralized database operation handler.
     *
     * All suspend database operations pass
     * through this function.
     *
     * CancellationException is rethrown
     * because coroutine cancellation must
     * never be treated as a database error.
     ***/

    private suspend fun <T> executeDatabaseOperation(
        operation: suspend () -> T
    ): Resource<T> {

        return try {

            Resource.Success(
                data = operation()
            )

        } catch (exception: CancellationException) {

            throw exception

        } catch (exception: ProductOperationException.ProductNotFound) {

            Resource.Error(
                error = AppError.ProductNotFound
            )

        } catch (exception: SQLiteException) {

            Resource.Error(
                error = AppError.DatabaseError(exception)
            )

        } catch (exception: Exception) {

            Resource.Error(
                error = AppError.UnexpectedError(exception)
            )
        }
    }


    /**
     * Internal exception used to represent
     * repository-level product failures.
     **/

    private sealed class ProductOperationException : Exception() {

        data object ProductNotFound :
            ProductOperationException()
    }
}
