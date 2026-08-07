package com.shamiacademy.maktabalibrary.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.ui.fragments.AuthorsFragment
import com.shamiacademy.maktabalibrary.ui.fragments.BooksFragment
import com.shamiacademy.maktabalibrary.ui.fragments.DownloadsFragment
import com.shamiacademy.maktabalibrary.ui.fragments.MoreFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            showFragment(BooksFragment())
        }

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_books -> BooksFragment()
                R.id.nav_authors -> AuthorsFragment()
                R.id.nav_downloads -> DownloadsFragment()
                R.id.nav_more -> MoreFragment()
                else -> BooksFragment()
            }
            showFragment(fragment)
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
