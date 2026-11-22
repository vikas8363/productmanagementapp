package com.app.productmanagementapp.presentation.products.ui

import com.app.productmanagementapp.databinding.ItemProductBinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.productmanagementapp.R
import com.app.productmanagementapp.data.entity.ProductEntity
import java.text.NumberFormat
import java.util.Locale


class ProductAdapter(
    private val onEditClick: (ProductEntity) -> Unit,
    private val onDeleteClick: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductAdapter.ProductViewHolder>(
    ProductDiffCallback()
) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProductViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        holder.bind(getItem(position))
    }


    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) {

            binding.apply {

                tvProductName.text = product.productName

                tvProductCost.text = root.context.getString(
                    R.string.product_cost_value,
                    formatCurrency(product.productCostPaise)
                )

                tvProductQuantity.text = root.context.getString(
                    R.string.product_quantity_value,
                    product.quantity
                )

                tvProductTotal.text = root.context.getString(
                    R.string.product_total_value,
                    formatCurrency(product.totalPricePaise)
                )

                btnEditProduct.setOnClickListener {
                    onEditClick(product)
                }

                btnDeleteProduct.setOnClickListener {
                    onDeleteClick(product)
                }
            }
        }
    }


    private fun formatCurrency(amountPaise: Long): String {

        return NumberFormat.getCurrencyInstance(
            Locale.Builder()
                .setLanguage("en")
                .setRegion("IN")
                .build()
        ).format(amountPaise / 100.0)
    }


    class ProductDiffCallback : DiffUtil.ItemCallback<ProductEntity>() {

        override fun areItemsTheSame(
            oldItem: ProductEntity,
            newItem: ProductEntity
        ): Boolean {

            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: ProductEntity,
            newItem: ProductEntity
        ): Boolean {

            return oldItem == newItem
        }
    }
}
