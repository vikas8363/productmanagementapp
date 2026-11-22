package com.app.productmanagementapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge support
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        // Configure system bar icon colors
        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).apply {

            // White status bar icons on blue background
            isAppearanceLightStatusBars = false

            // Dark navigation bar icons
            isAppearanceLightNavigationBars = true
        }

        handleWindowInsets()
    }

    private fun handleWindowInsets() {

        val navHost = findViewById<View>(
            R.id.nav_host_fragment
        )

        // Preserve existing padding
        val initialLeft = navHost.paddingLeft
        val initialTop = navHost.paddingTop
        val initialRight = navHost.paddingRight
        val initialBottom = navHost.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(
            navHost
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            // Apply actual system bar dimensions
            view.setPadding(
                initialLeft + systemBars.left,
                initialTop + systemBars.top,
                initialRight + systemBars.right,
                initialBottom + systemBars.bottom
            )

            insets
        }

        // Request insets after installing the listener
        ViewCompat.requestApplyInsets(navHost)
    }
}
