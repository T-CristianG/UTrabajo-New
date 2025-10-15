package com.tab.utrabajo

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseRepository {

    private val auth: FirebaseAuth = FirebaseConfig.auth
    private val db: FirebaseFirestore = FirebaseConfig.firestore
    private val storage: FirebaseStorage = FirebaseConfig.storage

    // 🔹 Registrar usuario (estudiante)
    fun registerStudent(
        email: String,
        password: String,
        fullName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val data = hashMapOf(
                    "uid" to uid,
                    "nombre" to fullName,
                    "email" to email,
                    "rol" to "estudiante"
                )
                db.collection("usuarios").document(uid).set(data)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Error al guardar usuario") }
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error al registrar usuario") }
    }

    // 🔹 Subir archivo (por ejemplo, hoja de vida en PDF)
    fun uploadCV(
        fileUri: Uri,
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = storage.reference.child("cvs/$userId.pdf")
        ref.putFile(fileUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString())
                }.addOnFailureListener { e ->
                    onError(e.message ?: "Error obteniendo URL del archivo")
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error subiendo archivo")
            }
    }
}
