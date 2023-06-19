package com.example.snapshots

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.snapshots.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private lateinit var mBinding: FragmentAddBinding
    private lateinit var galleryLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAddBinding.inflate(inflater)
        return mBinding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGallery()
        mBinding.btnPost.setOnClickListener { postSnapshot() }

        mBinding.btnSelect.setOnClickListener { openGallery() }
    }

    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setupGallery(){
        galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.i("PhotoPicker", "Selected URI: $uri")
                mBinding.imgPhoto.setImageURI(uri)
                mBinding.tilTitle.visibility = View.VISIBLE
                mBinding.tvMessage.text = getString(R.string.post_message_valid_title)

            } else {
                Log.i("PhotoPicker", "No media selected")
            }
        }
    }

    private fun postSnapshot() {

    }

}