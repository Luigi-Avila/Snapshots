package com.example.snapshots

import android.net.Uri
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddFragment : Fragment() {

    private val PATH_SNAPSHOT = "snapshots"

    private lateinit var mBinding: FragmentAddBinding
    private lateinit var galleryLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mStorageReference: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mPhotoSelectedUri: Uri

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

        mStorageReference = FirebaseStorage.getInstance().reference
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOT)

    }

    private fun setupGallery() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.i("PhotoPicker", "Selected URI: $uri")
                    mPhotoSelectedUri = uri
                    mBinding.imgPhoto.setImageURI(uri)
                    mBinding.tilTitle.visibility = View.VISIBLE
                    mBinding.tvMessage.text = getString(R.string.post_message_valid_title)

                } else {
                    Log.i("PhotoPicker", "No media selected")
                }
            }
    }

    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun postSnapshot() {
        mBinding.progressBar.visibility = View.VISIBLE
        val storageReference = mStorageReference.child(PATH_SNAPSHOT).child("my_photo")
        storageReference.putFile(mPhotoSelectedUri)
            .addOnProgressListener {
                val progress = (100 * it.bytesTransferred / it.totalByteCount).toDouble()
                mBinding.progressBar.progress = progress.toInt()
                mBinding.tvMessage.text = "$progress %"
            }
            .addOnCompleteListener {
                mBinding.progressBar.visibility = View.INVISIBLE
            }
            .addOnSuccessListener {
                Snackbar.make(mBinding.root, "Foto publicada", Snackbar.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Snackbar.make(
                    mBinding.root,
                    "No se pudo subir, intente de nuevo",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }

}