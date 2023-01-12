package com.application.phonerecord

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


object Repository {

    var storage = FirebaseStorage.getInstance()



    fun uploadFile(byteArray: ByteArray, fileName : String) {
        val filesRef: StorageReference = storage.reference
        filesRef.child("records/${fileName}").putBytes(byteArray).addOnSuccessListener {
            println("")
        }.addOnFailureListener {
            println("")
        }
    }

}