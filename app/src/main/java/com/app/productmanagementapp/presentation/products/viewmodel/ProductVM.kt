package com.app.productmanagementapp.presentation.products.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.productmanagementapp.R
import com.app.productmanagementapp.core.common.ProductSortType
import com.app.productmanagementapp.core.common.Resource
import com.app.productmanagementapp.core.dialog.DialogManager
import com.app.productmanagementapp.data.entity.ProductEntity
import com.app.productmanagementapp.data.repository.ProductRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import com.app.productmanagementapp.presentation.products.model.ProductSummary

import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.app.productmanagementapp.core.common.AppError

/**
 * Product ViewModel
 *
 * Responsibilities:
 *
 * 1. Observe products from Room.
 * 2. Add new products.
 * 3. Update existing products.
 * 4. Delete products.
 * 5. Manage product sorting.
 * 6. Calculate product summary.
 * 7. Manage loading state.
 * 8. Handle operation results.
 *
 * No Activity, Fragment or View references
 * are maintained inside this ViewModel.
 **/

@HiltViewModel
class ProductVM @Inject constructor(

    private val productRepo: ProductRepo,


) : ViewModel() {


    /*** Product Sorting State**/

    private val _sortType = MutableLiveData(
        ProductSortType.NAME_ASCENDING
    )

    val sortType: LiveData<ProductSortType> = _sortType


    /**
     * Product Listing
     *
     * switchMap automatically changes the
     * Room observation when sorting changes.
     **/

    val products: LiveData<List<ProductEntity>> =
        sortType.switchMap { selectedSort ->
            productRepo.observeProducts(selectedSort)
        }


    private val _isLoading = MutableLiveData(false)

    val isLoading: LiveData<Boolean> = _isLoading


    private val _productSummary =
        MutableLiveData(ProductSummary())

    val productSummary: LiveData<ProductSummary> =
        products.map { productList ->

            ProductSummary(
                totalProducts = productList.size,
                totalPricePaise = productList.sumOf {
                    it.totalPricePaise
                }
            )
        }


    private val _errorMessage = MutableLiveData<Int?>()
    val errorMessage: LiveData<Int?> = _errorMessage


    private val _successMessage = MutableLiveData<Int?>()
    val successMessage: LiveData<Int?> = _successMessage


    init {

        observeProductSummary()
    }


    private fun observeProductSummary() {

        products.observeForever { productList ->

            val summary = ProductSummary(

                totalProducts = productList.size,

                totalPricePaise = productList.sumOf {
                    it.totalPricePaise
                }
            )

            _productSummary.value = summary
        }
    }


    fun updateSortType(sortType: ProductSortType) {

        _sortType.value = sortType
    }


    fun addProduct(

        productName: String,

        productCostPaise: Long,

        quantity: Int

    ) {

        if (!validateProduct(
                productName,
                productCostPaise,
                quantity
            )
        ) return


        viewModelScope.launch {

            _isLoading.value = true

            try {

                val product = ProductEntity(

                    productName = productName.trim(),

                    productCostPaise = productCostPaise,

                    quantity = quantity
                )

                when (
                    val result = productRepo.insertProduct(product)
                ) {

                    is Resource.Success -> {
                        showSuccess(R.string.message_product_added)

                    }

                    is Resource.Error -> {

                        showError(result.error.messageRes)
                    }

                    is Resource.Loading -> Unit
                    else -> {}
                }

            } finally {

                _isLoading.value = false
            }
        }
    }



    fun updateProduct(

        productId: Int,

        productName: String,

        productCostPaise: Long,

        quantity: Int

    ) {

        if (!validateProduct(
                productName,
                productCostPaise,
                quantity
            )
        ) return


        viewModelScope.launch {

            _isLoading.value = true

            try {

                val product = ProductEntity(

                    productId = productId,

                    productName = productName.trim(),

                    productCostPaise = productCostPaise,

                    quantity = quantity
                )

                when (
                    val result = productRepo.updateProduct(product)
                ) {

                    is Resource.Success -> {
                        showSuccess(R.string.message_product_updated)
                    }

                    is Resource.Error -> {

                        showError(result.error.messageRes)
                    }

                    is Resource.Loading -> Unit
                }

            } finally {

                _isLoading.value = false
            }
        }
    }


    fun deleteProduct(productId: Int) {

        viewModelScope.launch {

            _isLoading.value = true

            try {

                when (
                    val result = productRepo.deleteProduct(productId)
                ) {

                    is Resource.Success -> {
                        showSuccess(R.string.message_product_deleted)
                    }

                    is Resource.Error -> {

                        showError(result.error.messageRes)
                    }

                    is Resource.Loading -> Unit
                }

            } finally {

                _isLoading.value = false
            }
        }
    }


    private fun validateProduct(

        productName: String,

        productCostPaise: Long,

        quantity: Int

    ): Boolean {

        if (productName.isBlank()) {

            showError(R.string.error_invalid_product_name)

            return false
        }

        if (productCostPaise <= 0L) {
            showError(R.string.error_invalid_product_cost)
            return false
        }

        if (quantity !in 1..10) {

            showError(R.string.error_invalid_quantity)

            return false
        }

        return true
    }


    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }


    private fun showSuccess(messageRes: Int) {
        _successMessage.value = messageRes
    }

    private fun showError(messageRes: Int) {
        _errorMessage.value = messageRes
    }

}
