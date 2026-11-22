package com.app.productmanagementapp.presentation.products.ui
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.app.productmanagementapp.R
import com.app.productmanagementapp.data.entity.ProductEntity
import com.app.productmanagementapp.databinding.DialogAddEditProductBinding
import com.app.productmanagementapp.presentation.products.viewmodel.ProductVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

/**
 * Dialog for adding a new product or updating an existing product.
 *
 * Responsibilities:
 * 1. Collect product name, cost and quantity.
 * 2. Calculate total price automatically.
 * 3. Perform basic UI input checks.
 * 4. Send valid input to ProductVM.
 *
 * Business validation and database operations remain in ProductVM.
 */
@AndroidEntryPoint
class AddEditProductDialog : DialogFragment() {

    private var _binding: DialogAddEditProductBinding? = null
    private val binding get() = _binding!!

    /**
     * Shares the ProductVM owned by ProductListFragment.
     */
    private val productVM: ProductVM by viewModels(
        { requireParentFragment() }
    )

    private var productId: Int = 0

    private var initialProductName: String = ""
    private var initialProductCostPaise: Long = 0L
    private var initialQuantity: Int = 1

    private val isEditMode: Boolean
        get() = productId > 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { args ->
            productId = args.getInt(ARG_PRODUCT_ID)
            initialProductName = args.getString(ARG_PRODUCT_NAME).orEmpty()
            initialProductCostPaise = args.getLong(ARG_PRODUCT_COST)
            initialQuantity = args.getInt(ARG_PRODUCT_QUANTITY, 1)
        }

        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_ProductManagement_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEditProductBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupQuantityDropdown()
        setupInitialValues()
        setupTotalPriceCalculation()
        setupClickListeners()
    }

    private fun setupQuantityDropdown() {

        val quantityOptions = resources.getStringArray(
            R.array.product_quantity_options
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            quantityOptions
        )

        binding.actvQuantity.setAdapter(adapter)
    }

    private fun setupInitialValues() {

        binding.etProductName.setText(initialProductName)

        if (initialProductCostPaise > 0L) {
            binding.etProductCost.setText(
                BigDecimal.valueOf(
                    initialProductCostPaise,
                    2
                ).toPlainString()
            )
        }

        binding.actvQuantity.setText(
            initialQuantity.toString(),
            false
        )

        binding.btnSaveProduct.setText(
            if (isEditMode) {
                R.string.action_update_product
            } else {
                R.string.action_add_product
            }
        )

        updateCalculatedTotal()
    }

    private fun setupTotalPriceCalculation() {

        binding.etProductCost.doAfterTextChanged {
            updateCalculatedTotal()
        }

        binding.actvQuantity.setOnItemClickListener { _, _, _, _ ->
            updateCalculatedTotal()
        }
    }

    /**
     * Calculates cost multiplied by quantity.
     *
     * Cost is converted to paise to avoid floating-point
     * arithmetic errors in the calculation.
     */
    private fun updateCalculatedTotal() {

        val costPaise = parseCostToPaise(
            binding.etProductCost.text?.toString().orEmpty()
        )

        val quantity = binding.actvQuantity.text
            ?.toString()
            ?.toIntOrNull()
            ?: 1

        val totalPaise = if (
            costPaise != null &&
            costPaise > 0L &&
            quantity in 1..10
        ) {
            runCatching {
                Math.multiplyExact(costPaise, quantity.toLong())
            }.getOrNull()
        } else {
            0L
        }

        binding.tvCalculatedTotal.text = formatCurrency(
            totalPaise ?: 0L
        )
    }

    private fun setupClickListeners() {

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSaveProduct.setOnClickListener {
            submitProduct()
        }
    }

    private fun submitProduct() {

        clearInputErrors()

        val productName = binding.etProductName.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val costText = binding.etProductCost.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val quantity = binding.actvQuantity.text
            ?.toString()
            ?.toIntOrNull()

        var isValid = true

        if (productName.isBlank()) {
            binding.tilProductName.error =
                getString(R.string.error_product_name_required)
            isValid = false
        }

        if (costText.isBlank()) {
            binding.tilProductCost.error =
                getString(R.string.error_product_cost_required)
            isValid = false
        }

        val costPaise = parseCostToPaise(costText)

        if (costText.isNotBlank() && (costPaise == null || costPaise <= 0L)) {
            binding.tilProductCost.error =
                getString(R.string.error_product_cost_invalid)
            isValid = false
        }

        if (quantity == null || quantity !in 1..10) {
            binding.tilQuantity.error =
                getString(R.string.error_quantity_required)
            isValid = false
        }

        if (!isValid || costPaise == null || quantity == null) {
            return
        }

        val totalIsSafe = runCatching {
            Math.multiplyExact(costPaise, quantity.toLong())
        }.isSuccess

        if (!totalIsSafe) {
            binding.tilProductCost.error =
                getString(R.string.error_product_cost_too_large)
            return
        }

        if (isEditMode) {
            productVM.updateProduct(
                productId = productId,
                productName = productName,
                productCostPaise = costPaise,
                quantity = quantity
            )
        } else {
            productVM.addProduct(
                productName = productName,
                productCostPaise = costPaise,
                quantity = quantity
            )
        }

        dismiss()
    }

    private fun clearInputErrors() {
        binding.tilProductName.error = null
        binding.tilProductCost.error = null
        binding.tilQuantity.error = null
    }

    /**
     * Converts a decimal rupee amount to paise exactly.
     * Rejects more than two decimal places and overflow.
     */
    private fun parseCostToPaise(value: String): Long? {

        if (value.isBlank()) return null

        return runCatching {
            val amount = BigDecimal(value)

            if (amount.scale() > 2) {
                return null
            }

            amount
                .setScale(2, RoundingMode.UNNECESSARY)
                .movePointRight(2)
                .longValueExact()
        }.getOrNull()
    }

    private fun formatCurrency(amountPaise: Long): String {

        return NumberFormat.getCurrencyInstance(
            Locale("en", "IN")
        ).format(
            BigDecimal.valueOf(amountPaise, 2)
        )
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        const val TAG = "AddEditProductDialog"

        private const val ARG_PRODUCT_ID = "product_id"
        private const val ARG_PRODUCT_NAME = "product_name"
        private const val ARG_PRODUCT_COST = "product_cost"
        private const val ARG_PRODUCT_QUANTITY = "product_quantity"

        /**
         * Creates an empty dialog for adding a product,
         * or pre-populates it for editing.
         */
        fun newInstance(
            product: ProductEntity? = null
        ): AddEditProductDialog {

            return AddEditProductDialog().apply {

                arguments = Bundle().apply {

                    putInt(
                        ARG_PRODUCT_ID,
                        product?.productId ?: 0
                    )

                    putString(
                        ARG_PRODUCT_NAME,
                        product?.productName.orEmpty()
                    )

                    putLong(
                        ARG_PRODUCT_COST,
                        product?.productCostPaise ?: 0L
                    )

                    putInt(
                        ARG_PRODUCT_QUANTITY,
                        product?.quantity ?: 1
                    )
                }
            }
        }
    }
}
