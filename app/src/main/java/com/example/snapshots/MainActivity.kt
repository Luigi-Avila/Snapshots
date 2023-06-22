package com.example.snapshots

import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.snapshots.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mActiveFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager

    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private var mFirebaseAuth: FirebaseAuth? = null



    private val sigInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupAuth()
        setupBottomNavigation()
    }

    private fun setupAuth() {
        Log.i("USER", "Valor de user inicio setup ")
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            Toast.makeText(this, "Inicia sesion", Toast.LENGTH_SHORT).show()
            val user = it.currentUser
            Log.i("USER", "Valor de user inicio setup $user")
            if ( user == null){
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                val sigInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
                sigInLauncher.launch(sigInIntent)
            }
        }

        /*
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            Log.i("USER", "Valor de user $user")
            if (user == null) {
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                val sigInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()
                sigInLauncher.launch(sigInIntent)
            } else {
                Toast.makeText(this, "Usuario loggeado", Toast.LENGTH_LONG).show()
            }
        }
        */


    }

    override fun onStart() {
        super.onStart()
        mFirebaseAuth?.addAuthStateListener(mAuthListener)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "Bienvenido...", Toast.LENGTH_LONG).show()
            // ...
        } else {
            if (response == null) {
                Toast.makeText(
                    this,
                    "No puedes continuar sin logearte",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }

        }
    }

    private fun setupBottomNavigation() {
        mFragmentManager = supportFragmentManager

        val homeFragment = HomeFragment()
        val addFragment = AddFragment()
        val profileFragment = ProfileFragment()

        mActiveFragment = homeFragment

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment)
            .commit()

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, addFragment, AddFragment::class.java.name)
            .hide(addFragment)
            .commit()

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, homeFragment, HomeFragment::class.java.name)
            .commit()

        mBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    mFragmentManager.beginTransaction()
                        .hide(mActiveFragment)
                        .show(homeFragment)
                        .commit()
                    mActiveFragment = homeFragment
                    true
                }

                R.id.action_add -> {
                    mFragmentManager.beginTransaction()
                        .hide(mActiveFragment)
                        .show(addFragment)
                        .commit()
                    mActiveFragment = addFragment
                    true
                }

                R.id.action_profile -> {
                    mFragmentManager.beginTransaction()
                        .hide(mActiveFragment)
                        .show(profileFragment)
                        .commit()
                    mActiveFragment = profileFragment
                    true
                }

                else -> false
            }
        }

        mBinding.bottomNav.setOnItemReselectedListener {
            when(it.itemId){
                R.id.action_home -> (homeFragment as HomeAux).goToTop()
            }
        }
    }
    override fun onStop() {
        super.onStop()
        mFirebaseAuth?.removeAuthStateListener(mAuthListener)
    }

}