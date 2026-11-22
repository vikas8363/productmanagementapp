package com.app.productmanagementapp.presentation.splash.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.productmanagementapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment(R.layout.fragment_splash) {

    companion object {
        private const val SPLASH_DELAY_MILLIS = 1200L
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        navigateToProductList()
    }

    private fun navigateToProductList() {

        viewLifecycleOwner.lifecycleScope.launch {

            delay(SPLASH_DELAY_MILLIS)

            if (findNavController()
                    .currentDestination?.id == R.id.splashFragment
            ) {
                findNavController().navigate(
                    R.id.action_splashFragment_to_productListFragment
                )
            }
        }
    }
}
