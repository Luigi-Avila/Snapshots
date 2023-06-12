package com.example.snapshots

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.snapshots.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.hostFragment, HomeFragment()).commit()
    }


}