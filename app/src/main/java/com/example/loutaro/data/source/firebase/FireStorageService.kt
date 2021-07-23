package com.example.loutaro.data.source.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

object FireStorageService {
    private val storage = FirebaseStorage.getInstance()
    private val storageReference = storage.reference

    fun putFile(filePath: Uri): UploadTask {
        val ref: StorageReference = storageReference.child(
                "images2/"
                        + UUID.randomUUID().toString()
        )

        return ref.putFile(filePath)
    }
}