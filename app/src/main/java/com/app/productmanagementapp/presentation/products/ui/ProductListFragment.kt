package com.app.productmanagementapp.presentation.products.ui


import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.productmanagementapp.R
import com.app.productmanagementapp.core.common.ProductSortType
import com.app.productmanagementapp.data.entity.ProductEntity
import com.app.productmanagementapp.databinding.FragmentProductListBinding
import com.app.productmanagementapp.presentation.products.viewmodel.ProductVM
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.productmanagementapp.core.dialog.DialogManager
import com.app.productmanagementapp.core.dialog.DialogState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import javax.inject.Inject

/******************************************
 * Product List Fragment
 *
 * Responsibilities:
 * 1. Display products using RecyclerView.
 * 2. Observe product LiveData.
 * 3. Display product summary.
 * 4. Handle sorting selection.
 * 5. Forward user actions to ViewModel.
 ******************************************/

@AndroidEntryPoint
class ProductListFragment :
    Fragment(R.layout.fragment_product_list) {

    private var _binding: FragmentProductListBinding? = null

    private val binding get() = _binding!!

    private val productVM: ProductVM by viewModels()

    private lateinit var productAdapter: ProductAdapter

    @Inject
    lateinit var dialogManager: DialogManager

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProductListBinding.bind(view)

        setupRecyclerView()

        setupSortingDropdown()

        setupClickListeners()

        observeProductData()
        observeErrorMessage()
        observeSuccessMessage()
    }


    private fun setupRecyclerView() {

        productAdapter = ProductAdapter(

            onEditClick = { product ->
                onEditProductRequested(product)
            },

            onDeleteClick = { product ->
                onDeleteProductRequested(product)
            }
        )

        binding.rvProducts.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = productAdapter

            setHasFixedSize(true)
        }
    }

    private fun setupSortingDropdown() {

        val sortingOptions = resources.getStringArray(
            R.array.product_sort_options
        )

        val dropdownAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            sortingOptions
        )

        binding.actvSort.setAdapter(dropdownAdapter)

        binding.actvSort.setText(
            sortingOptions[0],
            false
        )

        binding.actvSort.setOnItemClickListener { _, _, position, _ ->

            val selectedSort = when (position) {

                0 -> ProductSortType.NAME_ASCENDING

                1 -> ProductSortType.NAME_DESCENDING

                2 -> ProductSortType.PRICE_ASCENDING

                3 -> ProductSortType.PRICE_DESCENDING

                else -> ProductSortType.NAME_ASCENDING
            }

            productVM.updateSortType(selectedSort)
        }
    }


    private fun observeSuccessMessage() {

        productVM.successMessage.observe(viewLifecycleOwner) { messageRes ->

            messageRes?.let { successRes ->

                Toast.makeText(
                    requireContext(),
                    getString(successRes),
                    Toast.LENGTH_SHORT
                ).show()

                productVM.clearSuccessMessage()
            }
        }
    }

    private fun observeErrorMessage() {

        productVM.errorMessage.observe(viewLifecycleOwner) { messageRes ->

            messageRes?.let { errorRes ->

                dialogManager.showErrorDialog(
                    context = requireContext(),
                    messageRes = errorRes
                )

                // Clear the error after displaying it
                productVM.clearError()
            }
        }
    }


    private fun observeProductData() {

        productVM.products.observe(viewLifecycleOwner) { products ->

            productAdapter.submitList(products)

            updateProductListVisibility(products)
        }

        productVM.productSummary.observe(viewLifecycleOwner) { summary ->

            binding.tvTotalProducts.text =
                summary.totalProducts.toString()

            binding.tvTotalPrice.text =
                formatCurrency(summary.totalPricePaise)

            binding.tvProductCount.text =
                getString(
                    R.string.product_list_subtitle_count,
                    summary.totalProducts
                )
        }

        productVM.isLoading.observe(viewLifecycleOwner) { isLoading ->

            binding.btnAddProduct.isEnabled = !isLoading

            binding.btnAddProduct.alpha =
                if (isLoading) 0.5f else 1f
        }
    }


    private fun updateProductListVisibility(
        products: List<ProductEntity>
    ) {

        val isEmpty = products.isEmpty()

        binding.layoutEmptyState.visibility =
            if (isEmpty) View.VISIBLE else View.GONE

        binding.rvProducts.visibility =
            if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun setupClickListeners() {

        binding.btnAddProduct.setOnClickListener {

            onAddProductRequested()
        }
    }


    private fun onAddProductRequested() {

        AddEditProductDialog.newInstance()
            .show(
                childFragmentManager,
                AddEditProductDialog.TAG
            )
    }


    private fun onEditProductRequested(
        product: ProductEntity
    ) {

        AddEditProductDialog.newInstance(product)
            .show(
                childFragmentManager,
                AddEditProductDialog.TAG
            )
    }



    /**
     * Requests confirmation before deleting a product.
     *
     * The actual database operation is performed
     * by ProductVM only after user confirmation.
     */
    private fun onDeleteProductRequested(product: ProductEntity) {

        dialogManager.showConfirmationDialog(
            context = requireContext(),
            titleRes = R.string.confirm_delete_title,
            messageRes = R.string.confirm_delete_message,
            positiveButtonRes = R.string.action_confirm_delete,
            negativeButtonRes = R.string.action_cancel,

            onPositiveClick = {
                productVM.deleteProduct(product.productId)
            }
        )
    }

    private fun formatCurrency(amountPaise: Long): String {

        return NumberFormat.getCurrencyInstance(
            Locale.Builder()
                .setLanguage("en")
                .setRegion("IN")
                .build()
        ).format(amountPaise / 100.0)
    }


    override fun onDestroyView() {

        binding.rvProducts.adapter = null

        _binding = null

        super.onDestroyView()
    }
}
