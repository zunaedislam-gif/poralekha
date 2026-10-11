package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppViewModel

// --- Screen Navigation Enum ---
sealed class Screen {
    object Onboarding : Screen()
    object Login : Screen()
    object StudentHome : Screen()
    object StudentSearch : Screen()
    object CourseDetail : Screen()
    object Cart : Screen()
    object MyLearning : Screen()
    object VideoPlayer : Screen()
    object Certificate : Screen()
    object Profile : Screen()
    object NotificationCenter : Screen()
    object BecomeInstructor : Screen()
    
    // Instructor Dashboard & actions
    object InstructorDashboard : Screen()
    object CourseWizard : Screen()
    object PayoutRequest : Screen()
    object QAInbox : Screen()
    
    // Super Admin Panel
    object AdminDashboard : Screen()
    object AdminUsers : Screen()
    object AdminInstructorApps : Screen()
    object AdminCourseMod : Screen()
    object AdminPayouts : Screen()
}

// --- Theme Colors ---
object PoraTheme {
    val DeepPurple = Color(0xFF5E35B1)
    val LightPurple = Color(0xFFD0BCFF)
    val AccentYellow = Color(0xFFFFB300)
    val AccentOrange = Color(0xFFF57C00)
    val BackgroundLight = Color(0xFFFFFBFE)
    val SurfaceDark = Color(0xFF1E1E2C)
    val TextDark = Color(0xFF12121E)
    val TextLight = Color(0xFFECEFF1)
}

// --- Onboarding Slider ---
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val slides = listOf(
        OnboardingSlideData(
            "শিখুন আপনার ভাষায়",
            "বাংলা ভাষায় দেশের সেরা ইনস্ট্রাক্টরদের কাছ থেকে প্রফেশনাল স্কিলস শিখুন যেকোনো সময়, যেকোনো স্থানে।",
            Icons.Default.School
        ),
        OnboardingSlideData(
            "ইনস্ট্রাক্টর হয়ে ক্যারিয়ার গড়ুন",
            "আপনার জ্ঞান শেয়ার করুন এবং নতুন শিক্ষার্থী তৈরির মাধ্যমে ঘরে বসেই উপার্জন করুন।",
            Icons.Default.Work
        ),
        OnboardingSlideData(
            "সার্টিফিকেট অর্জন করুন",
            "কোর্স শেষ করার সাথে সাথেই একটি ভেরিফাইড সার্টিফিকেট অর্জন করুন যা আপনার সিভি এবং পোর্টফোলিওকে সমৃদ্ধ করবে।",
            Icons.Default.CardMembership
        )
    )
    var currentSlide by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("onboarding_screen"),
        containerColor = PoraTheme.BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onFinish,
                    modifier = Modifier.testTag("skip_button")
                ) {
                    Text("Skip", color = PoraTheme.DeepPurple, fontWeight = FontWeight.Bold)
                }
            }

            // Body illustration and Text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Rounded illustration background
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(PoraTheme.DeepPurple.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = slides[currentSlide].icon,
                        contentDescription = "Illustration",
                        tint = PoraTheme.DeepPurple,
                        modifier = Modifier.size(90.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = slides[currentSlide].title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PoraTheme.TextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = slides[currentSlide].description,
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Dots & Next actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    slides.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (index == currentSlide) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (index == currentSlide) PoraTheme.DeepPurple else Color.LightGray)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentSlide < slides.size - 1) {
                            currentSlide++
                        } else {
                            onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("next_slide_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (currentSlide == slides.size - 1) "শুরু করুন" else "পরবর্তী",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

data class OnboardingSlideData(val title: String, val description: String, val icon: ImageVector)

// --- Login / Signup Panel ---
@Composable
fun LoginRegisterScreen(
    viewModel: AppViewModel,
    onLoginSuccess: () -> Unit
) {
    // States: "login", "student_register", "instructor_register"
    var screenState by remember { mutableStateOf("login") }
    
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Common registration fields
    var name by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    
    // Instructor fields
    var bio by remember { mutableStateOf("") }
    var expertArea by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    
    var isForgotPassword by remember { mutableStateOf(false) }

    // Interests checklist for student sign up
    val categories = listOf("Development", "Design", "Business", "Marketing", "IT & Software", "Language")
    val selectedInterests = remember { mutableStateListOf<String>() }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("login_screen"),
        containerColor = PoraTheme.BackgroundLight
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            
            // Branding Logo using poralekha_logo_1784580055117
            Image(
                painter = painterResource(id = com.example.R.drawable.poralekha_logo_1784580055117),
                contentDescription = "পড়ালেখা Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("পড়ালেখা", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (successMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xE8D1F2D9)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        successMessage,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            when (screenState) {
                "login" -> {
                    // --- LOGIN FORM ---
                    Text("লগইন করুন (Log In)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text("ইমেইল বা মোবাইল নম্বর") },
                        placeholder = { Text("আপনার ইমেইল বা মোবাইল নম্বর") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("username_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("পাসওয়ার্ড") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("password_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isForgotPassword = true }) {
                            Text("পাসওয়ার্ড ভুলে গেছেন?", color = PoraTheme.DeepPurple)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            errorMessage = ""
                            if (emailOrPhone.isBlank() || password.isBlank()) {
                                errorMessage = "সবগুলো ফিল্ড সঠিকভাবে পূরণ করুন।"
                                return@Button
                            }
                            viewModel.login(emailOrPhone, password, {
                                onLoginSuccess()
                            }, { error ->
                                errorMessage = error
                            })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("login_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("লগইন করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // --- REGISTRATION SELECTOR PANEL ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("নতুন একাউন্ট তৈরি করুন", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PoraTheme.TextDark)
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Button(
                                onClick = {
                                    screenState = "student_register"
                                    errorMessage = ""
                                    successMessage = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("signup_tab"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("শিক্ষার্থী হিসেবে নিবন্ধন (Student Register)", color = Color.White)
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            OutlinedButton(
                                onClick = {
                                    screenState = "instructor_register"
                                    errorMessage = ""
                                    successMessage = ""
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PoraTheme.DeepPurple),
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("join_instructor_btn"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, PoraTheme.DeepPurple)
                            ) {
                                Text("প্রশিক্ষক হিসেবে যোগ দিন (Join as Instructor)", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                "student_register" -> {
                    // --- STUDENT REGISTER FORM ---
                    Text("শিক্ষার্থী নিবন্ধন (Student Register)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("পূর্ণ নাম") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("name_signup_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("ইমেইল") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("email_signup_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("মোবাইল নম্বর") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        placeholder = { Text("e.g. 01712345678") },
                        modifier = Modifier.fillMaxWidth().testTag("phone_signup_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("পাসওয়ার্ড") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("password_signup_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("আপনার পছন্দের ক্যাটাগরি সিলেক্ট করুন:", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(modifier = Modifier.fillMaxWidth()) {
                        categories.chunked(2).forEach { pair ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                pair.forEach { cat ->
                                    val isChecked = selectedInterests.contains(cat)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f).clickable {
                                            if (isChecked) selectedInterests.remove(cat) else selectedInterests.add(cat)
                                        }
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = {
                                                if (isChecked) selectedInterests.remove(cat) else selectedInterests.add(cat)
                                            }
                                        )
                                        Text(cat, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            errorMessage = ""
                            if (name.isBlank() || regEmail.isBlank() || regPhone.isBlank() || regPassword.isBlank()) {
                                errorMessage = "সবগুলো ফিল্ড সঠিকভাবে পূরণ করুন।"
                                return@Button
                            }
                            viewModel.signUp(name, regEmail, regPhone, regPassword, selectedInterests.toList(), {
                                onLoginSuccess()
                            }, { error ->
                                errorMessage = error
                            })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("signup_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("নিবন্ধন সম্পন্ন করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = {
                        screenState = "login"
                        errorMessage = ""
                        successMessage = ""
                    }) {
                        Text("ইতিমধ্যে অ্যাকাউন্ট আছে? লগইন করুন", color = PoraTheme.DeepPurple, fontWeight = FontWeight.Bold)
                    }
                }
                
                "instructor_register" -> {
                    // --- INSTRUCTOR REGISTER FORM ---
                    Text("প্রশিক্ষক নিবন্ধন আবেদন (Instructor Apply)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("পূর্ণ নাম (Full Name)") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("instructor_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("ইমেইল (Email)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("instructor_email_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { regPhone = it },
                        label = { Text("মোবাইল নম্বর (Phone)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        placeholder = { Text("e.g. 01712345678") },
                        modifier = Modifier.fillMaxWidth().testTag("instructor_phone_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("পাসওয়ার্ড (Password)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("instructor_password_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = expertArea,
                        onValueChange = { expertArea = it },
                        label = { Text("দক্ষতার এরিয়া (e.g., Web Development)") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("instructor_expert_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = experience,
                        onValueChange = { experience = it },
                        label = { Text("অভিজ্ঞতা (e.g., 5+ Years)") },
                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("instructor_exp_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("আপনার সম্পর্কে সংক্ষিপ্ত পরিচিতি (Bio)") },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("instructor_bio_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            errorMessage = ""
                            if (name.isBlank() || regEmail.isBlank() || regPhone.isBlank() || regPassword.isBlank() || expertArea.isBlank() || experience.isBlank()) {
                                errorMessage = "সবগুলো আবশ্যক ফিল্ড সঠিকভাবে পূরণ করুন।"
                                return@Button
                            }
                            viewModel.signUpInstructor(name, regEmail, regPhone, regPassword, bio, expertArea, experience, {
                                successMessage = "আপনার শিক্ষক নিবন্ধন সফলভাবে জমা হয়েছে! এডমিন কর্তৃক আপনার আবেদনটি রিভিউ করার পর আপনি লগইন করতে পারবেন।"
                                screenState = "login"
                                // Clear inputs
                                name = ""
                                regEmail = ""
                                regPhone = ""
                                regPassword = ""
                                bio = ""
                                expertArea = ""
                                experience = ""
                            }, { error ->
                                errorMessage = error
                            })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("instructor_submit_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("নিবন্ধন আবেদন জমা দিন", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = {
                        screenState = "login"
                        errorMessage = ""
                        successMessage = ""
                    }) {
                        Text("ইতিমধ্যে অ্যাকাউন্ট আছে? লগইন করুন", color = PoraTheme.DeepPurple, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Forgot password Modal Dialog
    if (isForgotPassword) {
        AlertDialog(
            onDismissRequest = { isForgotPassword = false },
            title = { Text("পাসওয়ার্ড রিসেট ফ্লো", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("আপনার ইমেইল বা মোবাইল নম্বরটি দিন। আমরা একটি ওটিপি (OTP) ও পাসওয়ার্ড রিসেট লিংক পাঠাবো।")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        label = { Text("মোবাইল/ইমেইল") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isForgotPassword = false
                        successMessage = "আপনার রিসেট কোড OTP: 123456 আপনার নম্বরে পাঠানো হয়েছে।"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple)
                ) {
                    Text("রিসেট কোড পাঠান")
                }
            },
            dismissButton = {
                TextButton(onClick = { isForgotPassword = false }) {
                    Text("বাতিল করুন", color = Color.Gray)
                }
            }
        )
    }
}

// --- Skeleton Loaders ---
@Composable
fun SkeletonLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.LightGray.copy(alpha = 0.4f),
                        Color.LightGray.copy(alpha = 0.15f),
                        Color.LightGray.copy(alpha = 0.4f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    )
}

// --- Empty States Component ---
@Composable
fun EmptyStatePlaceholder(
    title: String,
    subtitle: String,
    icon: ImageVector,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(PoraTheme.DeepPurple.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PoraTheme.DeepPurple,
                modifier = Modifier.size(50.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Text(subtitle, fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
        
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(actionText, color = Color.White)
            }
        }
    }
}

// --- Canvas-Based Certificate Rendering ---
@Composable
fun DynamicCertificateRenderer(
    studentName: String,
    courseTitle: String,
    certificateCode: String,
    dateString: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().background(Color(0xFFFFFDF5))) {
            val width = size.width
            val height = size.height

            // 1. Draw outer gold borders
            val borderPadding = 12.dp.toPx()
            val outerPath = Path().apply {
                moveTo(borderPadding, borderPadding)
                lineTo(width - borderPadding, borderPadding)
                lineTo(width - borderPadding, height - borderPadding)
                lineTo(borderPadding, height - borderPadding)
                close()
            }
            drawPath(
                path = outerPath,
                color = Color(0xFFD4AF37),
                style = Stroke(width = 3.dp.toPx())
            )

            // Inner border
            val innerPadding = borderPadding + 6.dp.toPx()
            val innerPath = Path().apply {
                moveTo(innerPadding, innerPadding)
                lineTo(width - innerPadding, innerPadding)
                lineTo(width - innerPadding, height - innerPadding)
                lineTo(innerPadding, height - innerPadding)
                close()
            }
            drawPath(
                path = innerPath,
                color = Color(0xFFD4AF37).copy(alpha = 0.5f),
                style = Stroke(width = 1.dp.toPx())
            )

            // Decorative corner vectors (simulated via triangles)
            drawRect(
                color = Color(0xFF5E35B1),
                topLeft = androidx.compose.ui.geometry.Offset(borderPadding, borderPadding),
                size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 20.dp.toPx())
            )
            drawRect(
                color = Color(0xFF5E35B1),
                topLeft = androidx.compose.ui.geometry.Offset(width - borderPadding - 20.dp.toPx(), borderPadding),
                size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 20.dp.toPx())
            )
            drawRect(
                color = Color(0xFF5E35B1),
                topLeft = androidx.compose.ui.geometry.Offset(borderPadding, height - borderPadding - 20.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 20.dp.toPx())
            )
            drawRect(
                color = Color(0xFF5E35B1),
                topLeft = androidx.compose.ui.geometry.Offset(width - borderPadding - 20.dp.toPx(), height - borderPadding - 20.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 20.dp.toPx())
            )
        }

        // Overlay text contents elegantly
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "CERTIFICATE OF COMPLETION",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5E35B1),
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "This is proudly presented to",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = studentName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E1E2C)
                )
                Text(
                    text = "for successfully finishing the course",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = courseTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF12121E),
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Verify Code", fontSize = 8.sp, color = Color.Gray)
                        Text(certificateCode, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(Color.White)
                            .border(1.dp, Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        // Tiny QR illustration
                        Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Black)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Date", fontSize = 8.sp, color = Color.Gray)
                        Text(dateString, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}
