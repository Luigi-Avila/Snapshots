package com.example.snapshots

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import com.example.snapshots.databinding.FragmentAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
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
    ): View {
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
                    mBinding.etTitle.addTextChangedListener {
                        Log.i("Change", "Holaaaaa ${it?.length}")
                        if (it?.length != null){
                            if (it.isNotEmpty()){
                                mBinding.btnPost.visibility = View.VISIBLE
                            } else {
                                mBinding.btnPost.visibility = View.INVISIBLE
                            }
                        }
                    }
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
        val key = mDatabaseReference.push().key!!
        val storageReference = mStorageReference.child(PATH_SNAPSHOT)
            .child(FirebaseAuth.getInstance().uid!!)
            .child(key)
        storageReference.putFile(mPhotoSelectedUri)
            .addOnProgressListener {
                val progress = (100 * it.bytesTransferred / it.totalByteCount).toDouble()
                mBinding.progressBar.progress = progress.toInt()
                mBinding.tvMessage.text = "$progress%"
            }
            .addOnCompleteListener {
                mBinding.progressBar.visibility = View.INVISIBLE
            }
            .addOnSuccessListener {
                Snackbar
                    .make(mBinding.root, "Foto publicada", Snackbar.LENGTH_SHORT)
                    .show()
                it.storage.downloadUrl.addOnSuccessListener { url ->
                    FirebaseAuth.getInstance().currentUser?.let { user ->
                        user.displayName
                        savePost(
                            key,
                            url.toString(),
                            mBinding.etTitle.text.toString().trim(),
                            user.displayName.toString(),
                            user.uid
                        )
                        mBinding.tilTitle.visibility = View.GONE
                        mBinding.tvMessage.text = getString(R.string.post_message_title)
                        mBinding.imgPhoto.setImageURI(null)
                        mBinding.etTitle.text = null
                        hideKeyboard()
                    }

                }

            }
            .addOnFailureListener {
                Snackbar.make(
                    mBinding.root,
                    "No se pudo subir, intente de nuevo",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }

    private fun savePost(key: String, url: String, title: String, author: String, ownerUid: String) {
        val snapshot = Snapshot(title = title, photoUrl = url, author = author, ownerUid = ownerUid)
        mDatabaseReference.child(key).setValue(snapshot)
    }

    private fun hideKeyboard() {

        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)

        /*val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = requireActivity().currentFocus
        Toast.makeText(context, "hide keyboard", Toast.LENGTH_LONG).show()
        Log.i("HIDEBOARD", "Valor del focused view $currentFocusedView")
        if (currentFocusedView != null){
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        } else {
            view?.let { view ->
                inputMethodManager.hideSoftInputFromWindow(
                    view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }*/
    }

}