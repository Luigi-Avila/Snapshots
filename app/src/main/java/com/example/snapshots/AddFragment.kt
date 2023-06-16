package com.example.snapshots

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snapshots.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private val RC_GALLERY = 18

    private lateinit var mBinding: FragmentAddBinding

    private var mPhotoSelected: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAddBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnPost.setOnClickListener { postSnapshot() }

        mBinding.btnSelect.setOnClickListener { openGallery() }
    }

    private fun openGallery() {

    }

    private fun postSnapshot() {

    }

}