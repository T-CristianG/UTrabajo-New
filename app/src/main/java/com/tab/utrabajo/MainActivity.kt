package com.tab.utrabajo

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.ExperimentalComposeUiApi
import com.tab.utrabajo.ui.theme.UTrabajoTheme
import com.google.firebase.FirebaseApp

// --- Pantallas disponibles ---
sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object RoleSelection : Screen()
    object RegisterStudent : Screen()
    object StudentWorkInfo : Screen()
    object StudentSkills : Screen()
    object StudentUploadCV : Screen()
    object RegisterCompany : Screen()
    object CompanyRepInfo : Screen()
    object CompanyDocsUpload : Screen()
    object CompleteCompany : Screen()
    object RegistrationComplete : Screen()
    object RecoverStart : Screen()
    object VerifyCode : Screen()
    object ResetPassword : Screen()
    object RecoverSuccess : Screen()
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        FirebaseConfig.init(this)


        setContent {
            UTrabajoTheme {
                App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            when (currentScreen) {
                is Screen.Splash -> { /* no topbar */ }
                else -> {
                    TopAppBar(
                        title = { Text(text = "") },
                        navigationIcon = {
                            IconButton(onClick = {
                                currentScreen = when (currentScreen) {
                                    is Screen.Login -> Screen.Splash
                                    is Screen.RoleSelection -> Screen.Splash
                                    is Screen.RegisterStudent -> Screen.Splash
                                    is Screen.StudentWorkInfo -> Screen.RegisterStudent
                                    is Screen.StudentSkills -> Screen.StudentWorkInfo
                                    is Screen.StudentUploadCV -> Screen.StudentSkills
                                    is Screen.RegisterCompany -> Screen.RoleSelection
                                    is Screen.CompanyRepInfo -> Screen.RegisterCompany
                                    is Screen.CompanyDocsUpload -> Screen.CompanyRepInfo
                                    is Screen.CompleteCompany -> Screen.CompanyDocsUpload
                                    is Screen.RegistrationComplete -> Screen.Splash
                                    is Screen.RecoverStart -> Screen.Login
                                    is Screen.VerifyCode -> Screen.RecoverStart
                                    is Screen.ResetPassword -> Screen.VerifyCode
                                    is Screen.RecoverSuccess -> Screen.Login
                                    else -> Screen.Splash
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_media_previous),
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        })
                    }
            ) {
                when (currentScreen) {
                    is Screen.Splash -> SplashScreen(
                        onLogin = { currentScreen = Screen.Login },
                        onRegister = { currentScreen = Screen.RoleSelection }
                    )
                    is Screen.Login -> LoginScreen(
                        onBack = { currentScreen = Screen.Splash },
                        onStudentLogin = { currentScreen = Screen.Splash },
                        onCompanyLogin = { currentScreen = Screen.Splash },
                        onRecoverPassword = { currentScreen = Screen.RecoverStart }
                    )
                    is Screen.RoleSelection -> RoleSelectionScreen(
                        onStudent = { currentScreen = Screen.RegisterStudent },
                        onCompany = { currentScreen = Screen.RegisterCompany }
                    )
                    is Screen.RegisterStudent -> RegisterStudentScreen(
                        onNext = { currentScreen = Screen.StudentWorkInfo },
                        onBack = { currentScreen = Screen.RoleSelection }
                    )
                    is Screen.StudentWorkInfo -> StudentWorkInfoScreen(
                        onNext = { currentScreen = Screen.StudentSkills },
                        onBack = { currentScreen = Screen.RegisterStudent }
                    )
                    is Screen.StudentSkills -> StudentSkillsScreen(
                        onNext = { currentScreen = Screen.StudentUploadCV },
                        onBack = { currentScreen = Screen.StudentWorkInfo }
                    )
                    is Screen.StudentUploadCV -> StudentUploadCVScreen(
                        onNext = { currentScreen = Screen.RegistrationComplete },
                        onBack = { currentScreen = Screen.StudentSkills }
                    )
                    is Screen.RegisterCompany -> RegisterCompanyScreen(
                        onNext = { currentScreen = Screen.CompanyRepInfo },
                        onBack = { currentScreen = Screen.RoleSelection }
                    )
                    is Screen.CompanyRepInfo -> CompanyRepresentativeScreen(
                        onNext = { currentScreen = Screen.CompanyDocsUpload },
                        onBack = { currentScreen = Screen.RegisterCompany }
                    )
                    is Screen.CompanyDocsUpload -> CompanyDocumentsUploadScreen(
                        onNext = { currentScreen = Screen.CompleteCompany },
                        onBack = { currentScreen = Screen.CompanyRepInfo }
                    )
                    is Screen.CompleteCompany -> CompleteCompanyScreen(
                        onSubmit = { currentScreen = Screen.RegistrationComplete },
                        onBack = { currentScreen = Screen.CompanyDocsUpload }
                    )
                    is Screen.RegistrationComplete -> RegistrationCompleteScreen(
                        onFinish = { currentScreen = Screen.Splash }
                    )
                    is Screen.RecoverStart -> RecoverStartScreen(
                        onNext = { currentScreen = Screen.VerifyCode },
                        onBack = { currentScreen = Screen.Login }
                    )
                    is Screen.VerifyCode -> VerifyCodeScreen(
                        onNext = { currentScreen = Screen.ResetPassword },
                        onResend = { /* lógica de reenvío */ },
                        onBack = { currentScreen = Screen.RecoverStart }
                    )
                    is Screen.ResetPassword -> ResetPasswordScreen(
                        onSubmit = { currentScreen = Screen.RecoverSuccess },
                        onBack = { currentScreen = Screen.VerifyCode }
                    )
                    is Screen.RecoverSuccess -> RecoverSuccessScreen(
                        onNext = { currentScreen = Screen.Login }
                    )
                }
            }
        }
    )
}

/* -------------------------
   Vistas (pantallas)
   ------------------------- */

@Composable
fun SplashScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF2F90D9)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("UT", fontSize = 72.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Text("Tu futuro profesional comienza aquí.", color = Color.White)
            Spacer(Modifier.height(80.dp))
            Button(
                onClick = onLogin,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Iniciar Sesión", color = Color(0xFF2F90D9))
            }
            Spacer(Modifier.height(12.dp))
            // <-- CORRECCIÓN: usar Button blanco para que "Registro" sea visible sobre fondo azul
            Button(
                onClick = onRegister,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(0.6f).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("Registro", color = Color(0xFF2F90D9))
            }
        }
    }
}

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onStudentLogin: () -> Unit,
    onCompanyLogin: () -> Unit,
    onRecoverPassword: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Text("UT", fontSize = 48.sp, color = Color(0xFF2F90D9))
        Spacer(Modifier.height(16.dp))
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(value = username, onValueChange = { username = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre de usuario o correo") })
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(20.dp))
        Button(onClick = onStudentLogin, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Iniciar Sesión") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onCompanyLogin, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Iniciar sesión - Empresa") }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onRecoverPassword) { Text("Recuperar contraseña", color = Color(0xFF2F90D9)) }
    }
}

@Composable
fun RoleSelectionScreen(onStudent: () -> Unit, onCompany: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF2F90D9)) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("UT", fontSize = 64.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(40.dp))
            Button(onClick = onStudent, modifier = Modifier.fillMaxWidth(0.7f).height(48.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                Text("Estudiante", color = Color(0xFF2F90D9))
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onCompany, modifier = Modifier.fillMaxWidth(0.7f).height(48.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                Text("Empresa", color = Color(0xFF2F90D9))
            }
        }
    }
}

@Composable
fun RegisterStudentScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        var fullName by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirm by remember { mutableStateOf("") }

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre Completo *") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Email *") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Contraseña *") }, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = confirm, onValueChange = { confirm = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Confirmar Contraseña *") }, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(12.dp))
        Text("Requisitos de contraseña", fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        Text("• Mínimo 8 caracteres")
        Text("• Al menos una letra mayúscula")
        Text("• Al menos un número")
        Text("• Al menos un símbolo (ej: !, @, #, $)")
        Spacer(Modifier.height(18.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Continuar") }
    }
}


@Composable
fun StudentWorkInfoScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        Text("¿Trabajas actualmente?", color = Color(0xFF2F90D9), fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))

        var worksNowState by remember { mutableStateOf(true) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = worksNowState, onClick = { worksNowState = true }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2F90D9)))
            Spacer(Modifier.size(8.dp))
            Text("Si", color = Color(0xFF2F90D9))
            Spacer(Modifier.size(16.dp))
            RadioButton(selected = !worksNowState, onClick = { worksNowState = false }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2F90D9)))
            Spacer(Modifier.size(8.dp))
            Text("No", color = Color(0xFF2F90D9))
        }

        Spacer(Modifier.height(18.dp))

        var companyName by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("") }

        if (worksNowState) {
            Text("Nombre de la empresa", color = Color(0xFF2F90D9))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(value = companyName, onValueChange = { companyName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre de la empresa") })
            Spacer(Modifier.height(12.dp))
            Text("Rol que desempeñas", color = Color(0xFF2F90D9))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(value = role, onValueChange = { role = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Rol que desempeñas") })
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Continuar") }
    }
}


@Composable
fun StudentSkillsScreen(onNext: () -> Unit, onBack: () -> Unit) {
    val skills = remember { mutableStateListOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        Text("Habilidades", color = Color(0xFF2F90D9), fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        Text("Mencione o describa habilidades adicionales que tenga.", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(12.dp))

        LazyColumn {
            itemsIndexed(skills) { index, skill ->
                OutlinedTextField(
                    value = skill,
                    onValueChange = { newVal -> skills[index] = newVal },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    label = { Text("${index + 1}.") }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = { skills.add("") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F90D9))) {
            Text("+ Añadir habilidad", color = Color.White)
        }

        Spacer(Modifier.height(24.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Continuar") }
    }
}


@Composable
fun StudentUploadCVScreen(onNext: () -> Unit, onBack: () -> Unit) {
    val selectedFileUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedFileUri.value = uri
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(8.dp))
        Text("Subir HV", fontSize = 20.sp, color = Color(0xFF2F90D9), fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(18.dp))

        Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Color(0xFFEAF4FB), shape = RoundedCornerShape(8.dp)).clickable { launcher.launch("/") }, contentAlignment = Alignment.Center) {
            if (selectedFileUri.value != null) {
                Text(text = "Archivo seleccionado:\n${selectedFileUri.value}", modifier = Modifier.padding(12.dp), textAlign = TextAlign.Center)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(id = android.R.drawable.arrow_up_float), contentDescription = null)
                    Spacer(Modifier.height(8.dp))
                    Text("Por favor, adjunte su hoja de vida", color = Color(0xFF2F90D9))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Button(onClick = { onNext() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F90D9))) {
            Text("Finalizar", color = Color.White)
        }
    }
}



@Composable
fun RegisterCompanyScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        CompanyAvatarField()
        Spacer(Modifier.height(12.dp))
        var nit by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var workers by remember { mutableStateOf("") }

        OutlinedTextField(value = nit, onValueChange = { nit = it }, modifier = Modifier.fillMaxWidth(), label = { Text("NIT y Nombre de la empresa") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Número Telefónico de la empresa") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Correo de la empresa") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = workers, onValueChange = { workers = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Número de trabajadores") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Siguiente") }
    }
}

@Composable
fun CompanyRepresentativeScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        Text("Nombre del representante legal.", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(8.dp))

        var repName by remember { mutableStateOf("") }
        var docType by remember { mutableStateOf("") }
        var docNumber by remember { mutableStateOf("") }

        OutlinedTextField(value = repName, onValueChange = { repName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nombre del representante legal") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = docType, onValueChange = { docType = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Tipo de documento") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = docNumber, onValueChange = { docNumber = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Número de documento") })
        Spacer(Modifier.height(12.dp))
        Text("Suba copia del documento del representante legal.", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(12.dp))
        SingleDocumentUploadField(label = "Por favor, adjunte copia del documento")
        Spacer(Modifier.height(18.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Siguiente") }
    }
}

@Composable
fun CompanyDocumentsUploadScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        Text("En este módulo, por favor, adjunte los documentos solicitados.", color = Color(0xFF2F90D9), fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(16.dp))
        Text("Suba copia del RUT.", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(8.dp))
        SingleDocumentUploadField(label = "Por favor, adjunte copia del RUT.")
        Spacer(Modifier.height(16.dp))
        Text("Suba copia de la Cámara de Comercio.", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(8.dp))
        SingleDocumentUploadField(label = "Por favor, adjunte copia de la Cámara de Comercio.")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Finalizar") }
    }
}

/* -------------------------
   CompleteCompanyScreen
   ------------------------- */
@Composable
fun CompleteCompanyScreen(onSubmit: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        Text("Completa tu Usuario", modifier = Modifier.align(Alignment.CenterHorizontally), fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(12.dp))
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Box(modifier = Modifier.size(96.dp).background(Color(0xFFDCEAF6), shape = CircleShape), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(id = android.R.drawable.ic_menu_camera), contentDescription = "avatar")
            }
        }
        Spacer(Modifier.height(12.dp))
        var nit by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var workers by remember { mutableStateOf("") }

        OutlinedTextField(value = nit, onValueChange = { nit = it }, modifier = Modifier.fillMaxWidth(), label = { Text("NIT y Nombre de la empresa") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = phone, onValueChange = { phone = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Número Telefónico de la empresa") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Correo de la empresa") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = workers, onValueChange = { workers = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Número de trabajadores") })
        Spacer(Modifier.height(20.dp))
        Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Siguiente") }
    }
}

/* -------------------------
   Componentes reutilizables
   ------------------------- */

@Composable
fun CompanyAvatarField() {
    var selectedResId by remember { mutableStateOf<Int?>(null) }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.size(120.dp).background(Color(0xFFDCEAF6), shape = CircleShape).clickable {
            selectedResId = if (selectedResId == android.R.drawable.ic_menu_camera) android.R.drawable.ic_menu_gallery else android.R.drawable.ic_menu_camera
        }, contentAlignment = Alignment.Center) {
            if (selectedResId != null) {
                Image(painter = painterResource(id = selectedResId!!), contentDescription = "avatar seleccionado", modifier = Modifier.size(72.dp), contentScale = ContentScale.Crop)
            } else {
                Icon(painter = painterResource(id = android.R.drawable.ic_menu_camera), contentDescription = "avatar", modifier = Modifier.size(56.dp))
            }
        }
    }
}

@Composable
fun SingleDocumentUploadField(label: String) {
    var docResId by remember { mutableStateOf<Int?>(null) }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color(0xFFEAF4FB), shape = RoundedCornerShape(8.dp)).clickable {
            docResId = if (docResId == android.R.drawable.ic_menu_upload) null else android.R.drawable.ic_menu_upload
        }, contentAlignment = Alignment.Center) {
            if (docResId != null) {
                Icon(painter = painterResource(id = docResId!!), contentDescription = "documento cargado")
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_menu_upload), contentDescription = "subir")
                    Spacer(Modifier.height(6.dp))
                    Text(label)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

/* -------------------------
   Registro completado y recuperar contraseña
   ------------------------- */

@Composable
fun RegistrationCompleteScreen(onFinish: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tu registro fue completado con éxito.", color = Color(0xFF2F90D9))
            Spacer(Modifier.height(24.dp))
            Button(onClick = onFinish, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F90D9))) {
                Text("Finalizar", color = Color.White)
            }
            Spacer(Modifier.height(24.dp))
            Text("Para verificar tu perfil, por favor, ingresa al link enviado al correo que registraste.", color = Color(0xFF2F90D9), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun RecoverStartScreen(onNext: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        Text("Ingrese el usuario o correo de la cuenta que desea recuperar.", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(12.dp))
        var identifier by remember { mutableStateOf("") }
        OutlinedTextField(value = identifier, onValueChange = { identifier = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Usuario o correo") })
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Siguiente") }
        Spacer(Modifier.height(24.dp))
        Text("Tenga en cuenta que, una vez restablecida la contraseña, deberá esperar un plazo de 1 mes para volver a restablecerla", color = Color(0xFF2F90D9))
    }
}

@Composable
fun VerifyCodeScreen(onNext: () -> Unit, onResend: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        Text("Por favor, ingrese el código de 5 dígitos enviado al correo o teléfono de la cuenta asociada", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(18.dp))
        var code by remember { mutableStateOf("") }
        OutlinedTextField(value = code, onValueChange = { if (it.length <= 5) code = it.filter { c -> c.isDigit() } }, modifier = Modifier.fillMaxWidth(), label = { Text("Código (5 dígitos)") })
        Spacer(Modifier.height(18.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Siguiente") }
        Spacer(Modifier.height(24.dp))
        Text("Tenga en cuenta que el código tiene una duración de 2 minutos. En caso de necesitar otro código, haga clic en Reenviar código.", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(12.dp))
        Button(onClick = onResend, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F90D9))) { Text("Reenviar código", color = Color.White) }
    }
}

@Composable
fun ResetPasswordScreen(onSubmit: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Top) {
        Spacer(Modifier.height(8.dp))
        Text("Ingrese la nueva contraseña:", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(8.dp))
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Nueva contraseña") }, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(12.dp))
        Text("Confirme la nueva contraseña", color = Color(0xFF2F90D9))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(12.dp))
        Text("Requisitos de contraseña", fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        Text("• Mínimo 8 caracteres")
        Text("• Al menos una letra mayúscula")
        Text("• Al menos un número")
        Text("• Al menos un símbolo (ej: !, @, #, $)")
        Spacer(Modifier.height(18.dp))
        Button(onClick = { onSubmit() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("Siguiente") }
        Spacer(Modifier.height(24.dp))
        Text("Tenga en cuenta que, una vez restablecida la contraseña, deberá esperar un plazo de 1 mes para volver a restablecerla", color = Color(0xFF2F90D9))
    }
}

@Composable
fun RecoverSuccessScreen(onNext: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Contraseña restablecida con éxito.", color = Color(0xFF2F90D9))
            Spacer(Modifier.height(40.dp))
            Button(onClick = onNext, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F90D9))) { Text("Siguiente", color = Color.White) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    UTrabajoTheme {
        App()
    }
}