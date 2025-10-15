package com.tab.utrabajo

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class FirebaseRepository private constructor() {

    companion object {
        @Volatile
        private var INSTANCE: FirebaseRepository? = null

        fun getInstance(): FirebaseRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseRepository().also { INSTANCE = it }
            }
        }
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // 🔹 Obtener usuario actual
    fun getCurrentUser() = auth.currentUser

    // 🔹 Registrar usuario (estudiante)
    fun registerStudent(
        email: String,
        password: String,
        fullName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val uid = user?.uid ?: run {
                        onError("Error al obtener UID del usuario")
                        return@addOnCompleteListener
                    }

                    val data = hashMapOf(
                        "uid" to uid,
                        "nombre" to fullName,
                        "email" to email,
                        "rol" to "estudiante",
                        "fechaRegistro" to Timestamp.now(),
                        "completado" to false
                    )

                    db.collection("usuarios").document(uid)
                        .set(data)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onError(e.message ?: "Error al guardar usuario")
                        }
                } else {
                    onError(task.exception?.message ?: "Error al registrar usuario")
                }
            }
    }

    // 🔹 Registrar empresa
    fun registerCompany(
        nit: String,
        phone: String,
        email: String,
        workers: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, "tempPassword123")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    val uid = user?.uid ?: run {
                        onError("Error al obtener UID de la empresa")
                        return@addOnCompleteListener
                    }

                    val data = hashMapOf(
                        "uid" to uid,
                        "nit" to nit,
                        "telefono" to phone,
                        "email" to email,
                        "numeroTrabajadores" to workers,
                        "rol" to "empresa",
                        "fechaRegistro" to Timestamp.now(),
                        "completado" to false
                    )

                    db.collection("empresas").document(uid)
                        .set(data)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onError(e.message ?: "Error al guardar empresa")
                        }
                } else {
                    onError(task.exception?.message ?: "Error al registrar empresa")
                }
            }
    }

    // 🔹 Guardar información laboral del estudiante
    fun saveStudentWorkInfo(
        userId: String,
        worksNow: Boolean,
        companyName: String = "",
        role: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val data = hashMapOf<String, Any>(
            "trabajaActual" to worksNow,
            "empresaActual" to companyName,
            "rolActual" to role,
            "ultimaActualizacion" to Timestamp.now()
        )

        db.collection("usuarios").document(userId)
            .update(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al guardar información laboral")
            }
    }

    // 🔹 Guardar habilidades del estudiante
    fun saveStudentSkills(
        userId: String,
        skills: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val data = hashMapOf<String, Any>(
            "habilidades" to skills,
            "ultimaActualizacion" to Timestamp.now()
        )

        db.collection("usuarios").document(userId)
            .update(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al guardar habilidades")
            }
    }

    // 🔹 Subir CV del estudiante
    fun uploadCV(
        fileUri: Uri,
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = storage.reference.child("cvs/$userId/${UUID.randomUUID()}.pdf")
        ref.putFile(fileUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val updateData = hashMapOf<String, Any>(
                        "cvUrl" to uri.toString(),
                        "cvSubido" to true,
                        "completado" to true
                    )

                    db.collection("usuarios").document(userId)
                        .update(updateData)
                        .addOnSuccessListener { onSuccess(uri.toString()) }
                        .addOnFailureListener { e ->
                            onError("Error guardando URL del CV: ${e.message}")
                        }
                }.addOnFailureListener { e ->
                    onError("Error obteniendo URL del archivo: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                onError("Error subiendo archivo: ${e.message}")
            }
    }

    // 🔹 Guardar información del representante legal
    fun saveCompanyRepresentative(
        userId: String,
        repName: String,
        docType: String,
        docNumber: String,
        docUri: Uri,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val docRef = storage.reference.child("empresas/$userId/representante/${UUID.randomUUID()}.pdf")
        docRef.putFile(docUri)
            .addOnSuccessListener {
                docRef.downloadUrl.addOnSuccessListener { docUrl ->
                    val data = hashMapOf<String, Any>(
                        "representanteLegal" to repName,
                        "tipoDocumento" to docType,
                        "numeroDocumento" to docNumber,
                        "documentoRepresentanteUrl" to docUrl.toString(),
                        "ultimaActualizacion" to Timestamp.now()
                    )

                    db.collection("empresas").document(userId)
                        .update(data)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onError("Error guardando información del representante: ${e.message}")
                        }
                }.addOnFailureListener { e ->
                    onError("Error obteniendo URL del documento: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                onError("Error subiendo documento: ${e.message}")
            }
    }

    // 🔹 Subir documentos de la empresa (RUT y Cámara de Comercio)
    fun uploadCompanyDocuments(
        userId: String,
        rutUri: Uri,
        camaraComercioUri: Uri,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val rutRef = storage.reference.child("empresas/$userId/documentos/rut_${UUID.randomUUID()}.pdf")
        val camaraRef = storage.reference.child("empresas/$userId/documentos/camara_${UUID.randomUUID()}.pdf")

        rutRef.putFile(rutUri)
            .addOnSuccessListener {
                rutRef.downloadUrl.addOnSuccessListener { rutUrl ->
                    camaraRef.putFile(camaraComercioUri)
                        .addOnSuccessListener {
                            camaraRef.downloadUrl.addOnSuccessListener { camaraUrl ->
                                val data = hashMapOf<String, Any>(
                                    "rutUrl" to rutUrl.toString(),
                                    "camaraComercioUrl" to camaraUrl.toString(),
                                    "completado" to true,
                                    "ultimaActualizacion" to Timestamp.now()
                                )

                                db.collection("empresas").document(userId)
                                    .update(data)
                                    .addOnSuccessListener { onSuccess() }
                                    .addOnFailureListener { e ->
                                        onError("Error guardando URLs de documentos: ${e.message}")
                                    }
                            }.addOnFailureListener { e ->
                                onError("Error obteniendo URL de cámara de comercio: ${e.message}")
                            }
                        }
                        .addOnFailureListener { e ->
                            onError("Error subiendo cámara de comercio: ${e.message}")
                        }
                }.addOnFailureListener { e ->
                    onError("Error obteniendo URL del RUT: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                onError("Error subiendo RUT: ${e.message}")
            }
    }

    // 🔹 Iniciar sesión
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Verificar en estudiantes
                        db.collection("usuarios").document(user.uid).get()
                            .addOnSuccessListener { studentDoc ->
                                if (studentDoc.exists()) {
                                    onSuccess()
                                } else {
                                    // Verificar en empresas
                                    db.collection("empresas").document(user.uid).get()
                                        .addOnSuccessListener { companyDoc ->
                                            if (companyDoc.exists()) {
                                                onSuccess()
                                            } else {
                                                onError("Usuario no encontrado en el sistema")
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            onError("Error verificando empresa: ${e.message}")
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                onError("Error verificando estudiante: ${e.message}")
                            }
                    } else {
                        onError("Error al obtener usuario después del login")
                    }
                } else {
                    onError(task.exception?.message ?: "Error al iniciar sesión")
                }
            }
    }

    // 🔹 Recuperar contraseña
    fun recoverPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error enviando email de recuperación")
            }
    }

    // 🔹 Verificar si el usuario completó el registro
    fun isUserComplete(
        userId: String,
        onComplete: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { studentDoc ->
                if (studentDoc.exists()) {
                    val completado = studentDoc.getBoolean("completado") ?: false
                    onComplete(completado)
                } else {
                    db.collection("empresas").document(userId).get()
                        .addOnSuccessListener { companyDoc ->
                            if (companyDoc.exists()) {
                                val completado = companyDoc.getBoolean("completado") ?: false
                                onComplete(completado)
                            } else {
                                onComplete(false)
                            }
                        }
                        .addOnFailureListener { e ->
                            onError("Error verificando empresa: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                onError("Error verificando estudiante: ${e.message}")
            }
    }

    // 🔹 Cerrar sesión
    fun logout() {
        auth.signOut()
    }
}