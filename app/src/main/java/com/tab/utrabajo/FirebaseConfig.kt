package com.tab.utrabajo

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseConfig {

    // 🔹 Instancias de Firebase
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    /**
     * 🔹 Inicializa Firebase en toda la app
     * Llama a esta función una sola vez, normalmente en onCreate() de MainActivity
     */
    fun init(context: Context) {
        FirebaseApp.initializeApp(context)
    }
}
