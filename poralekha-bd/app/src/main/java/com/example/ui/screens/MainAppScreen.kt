package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.*
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: AppViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val isUserLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()

    // Custom Backstack navigation state (Guarantees absolute crash-proof, fast transitions)
    val screenHistory = remember { mutableStateListOf<Screen>(Screen.Onboarding) }
    val currentScreen = screenHistory.lastOrNull() ?: Screen.Onboarding

    fun navigateTo(screen: Screen) {
        screenHistory.add(screen)
    }

    fun navigateBack() {
        if (screenHistory.size > 1) {
            screenHistory.removeAt(screenHistory.lastIndex)
        }
    }

    BackHandler(enabled = screenHistory.size > 1) {
        navigateBack()
    }

    // Direct routing: If user is not logged in, force Login screen (except onboarding)
    LaunchedEffect(isUserLoggedIn, currentScreen) {
        if (!isUserLoggedIn && currentScreen != Screen.Onboarding && currentScreen != Screen.Login) {
            screenHistory.clear()
            screenHistory.add(Screen.Login)
        } else if (isUserLoggedIn && currentScreen == Screen.Login) {
            screenHistory.clear()
            // Direct to dashboard based on approved role
            when (user?.role) {
                "super_admin" -> screenHistory.add(Screen.AdminDashboard)
                "instructor" -> screenHistory.add(Screen.InstructorDashboard)
                else -> screenHistory.add(Screen.StudentHome)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
        containerColor = PoraTheme.BackgroundLight,
        topBar = {
            // Display Top Bar only if not in Onboarding/Login
            if (currentScreen != Screen.Onboarding && currentScreen != Screen.Login) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                Screen.StudentHome -> "পড়ালেখা"
                                Screen.StudentSearch -> "কোর্স সার্চ করুন"
                                Screen.CourseDetail -> "কোর্স বিবরণী"
                                Screen.Cart -> "চেকআউট কার্ট"
                                Screen.MyLearning -> "আমার শেখা"
                                Screen.VideoPlayer -> "ক্লাসরুম প্লেয়ার"
                                Screen.Certificate -> "ডিজিটাল সার্টিফিকেট"
                                Screen.Profile -> "আপনার প্রোফাইল"
                                Screen.NotificationCenter -> "বিজ্ঞপ্তি কেন্দ্র"
                                Screen.BecomeInstructor -> "শিক্ষক হন"
                                Screen.InstructorDashboard -> "শিক্ষক প্যানেল"
                                Screen.CourseWizard -> "সিলেবাস উইজার্ড"
                                Screen.PayoutRequest -> "টাকা উত্তোলন"
                                Screen.QAInbox -> "প্রশ্ন ইনবক্স"
                                Screen.AdminDashboard -> "এডমিন ড্যাশবোর্ড"
                                Screen.AdminUsers -> "ইউজার ম্যানেজমেন্ট"
                                Screen.AdminInstructorApps -> "শিক্ষক আবেদন রিভিউ"
                                Screen.AdminCourseMod -> "কোর্স মডারেশন"
                                Screen.AdminPayouts -> "পে-আউট প্রসেসিং"
                                else -> "পড়ালেখা"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PoraTheme.DeepPurple
                    ),
                    navigationIcon = {
                        if (screenHistory.size > 1) {
                            IconButton(onClick = { navigateBack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        }
                    },
                    actions = {
                        // Quick role switch menu for Super Admin or approved Instructors
                        if (user != null) {
                            if (user!!.role == "super_admin") {
                                IconButton(onClick = {
                                    if (currentScreen is Screen.AdminDashboard) {
                                        screenHistory.clear()
                                        screenHistory.add(Screen.StudentHome)
                                    } else {
                                        screenHistory.clear()
                                        screenHistory.add(Screen.AdminDashboard)
                                    }
                                }) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Switch", tint = Color.White)
                                }
                            } else if (user!!.isDualRole || user!!.role == "instructor") {
                                IconButton(onClick = {
                                    if (currentScreen is Screen.InstructorDashboard) {
                                        screenHistory.clear()
                                        screenHistory.add(Screen.StudentHome)
                                    } else {
                                        screenHistory.clear()
                                        screenHistory.add(Screen.InstructorDashboard)
                                    }
                                }) {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = "Teacher Switch", tint = Color.White)
                                }
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            // Show Student Navigation bottom bar if user is logged in and active screen is student dashboard
            val showStudentBottomBar = isUserLoggedIn && (
                currentScreen == Screen.StudentHome ||
                currentScreen == Screen.StudentSearch ||
                currentScreen == Screen.MyLearning ||
                currentScreen == Screen.Profile
            )

            if (showStudentBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = PoraTheme.DeepPurple,
                    modifier = Modifier.testTag("student_bottom_navigation")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.StudentHome,
                        onClick = {
                            screenHistory.clear()
                            screenHistory.add(Screen.StudentHome)
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("হোম", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.StudentSearch,
                        onClick = {
                            screenHistory.clear()
                            screenHistory.add(Screen.StudentSearch)
                        },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("সার্চ", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_search")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.MyLearning,
                        onClick = {
                            screenHistory.clear()
                            screenHistory.add(Screen.MyLearning)
                        },
                        icon = { Icon(Icons.Default.School, contentDescription = "My Learning") },
                        label = { Text("আমার কোর্স", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_learning")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.Profile,
                        onClick = {
                            screenHistory.clear()
                            screenHistory.add(Screen.Profile)
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("প্রোফাইল", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Render corresponding screen based on animated navigation transitions
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    Screen.Onboarding -> OnboardingScreen(onFinish = {
                        screenHistory.clear()
                        screenHistory.add(Screen.Login)
                    })
                    Screen.Login -> LoginRegisterScreen(viewModel = viewModel, onLoginSuccess = {
                        screenHistory.clear()
                        if (user?.role == "super_admin") {
                            screenHistory.add(Screen.AdminDashboard)
                        } else if (user?.role == "instructor") {
                            screenHistory.add(Screen.InstructorDashboard)
                        } else {
                            screenHistory.add(Screen.StudentHome)
                        }
                    })
                    Screen.StudentHome -> StudentHomeScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.StudentSearch -> StudentSearchScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.CourseDetail -> CourseDetailScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.Cart -> StudentCartScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.MyLearning -> StudentMyLearningScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.VideoPlayer -> VideoPlayerScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.Certificate -> StudentCertificateScreen(viewModel = viewModel)
                    Screen.Profile -> StudentProfileScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.NotificationCenter -> NotificationCenterScreen(viewModel = viewModel)
                    Screen.BecomeInstructor -> BecomeInstructorScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    
                    // Instructor views
                    Screen.InstructorDashboard -> InstructorDashboardScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.CourseWizard -> CourseWizardScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.PayoutRequest -> PayoutRequestScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.QAInbox -> QAInboxScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    
                    // Super Admin views
                    Screen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.AdminUsers -> AdminUsersScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.AdminInstructorApps -> AdminInstructorAppsScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.AdminCourseMod -> AdminCourseModScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                    Screen.AdminPayouts -> AdminPayoutsScreen(viewModel = viewModel, onNavigate = { navigateTo(it) })
                }
            }
        }
    }
}

// --- STUDENT CERTIFICATE SCREEN ---
@Composable
fun StudentCertificateScreen(viewModel: AppViewModel) {
    val course by viewModel.selectedCourse.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val enrolled by viewModel.enrolledCourses.collectAsState()

    val myEnrollment = enrolled.find { it.courseId == course?.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("certificate_screen")
    ) {
        Text("ডিজিটাল সার্টিফিকেট (Verified Certificate)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Text("অভিনন্দন! আপনি সফলভাবে কোর্স সম্পন্ন করেছেন। আপনার ভেরিফাইড সার্টিফিকেটটি নিচে দেওয়া হলো।", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        if (myEnrollment != null && course != null) {
            val code = myEnrollment.certificateCode ?: "PLBD-MOCK-9921"
            val date = myEnrollment.certificateDate ?: "July 20, 2026"

            // Render Canvas-Based Certificate
            DynamicCertificateRenderer(
                studentName = user?.name ?: "শিক্ষার্থী",
                courseTitle = course!!.title,
                certificateCode = code,
                dateString = date
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = { /* simulated pdf download */ },
                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("সার্টিফিকেট পিডিএফ ডাউনলোড করুন")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { /* simulated social share */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("বন্ধুদের সাথে শেয়ার করুন (Share)")
            }
        } else {
            EmptyStatePlaceholder(
                title = "সার্টিফিকেট উপলব্ধ নয়",
                subtitle = "এই কোর্সের সার্টিফিকেটটি জেনারেট করতে অনুগ্রহ করে শতভাগ লেকচার সম্পন্ন করুন।",
                icon = Icons.Default.Badge
            )
        }
    }
}

// --- STUDENT PROFILE & SETTINGS ---
@Composable
fun StudentProfileScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
            .testTag("student_profile_screen")
    ) {
        // User Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PoraTheme.DeepPurple.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PoraTheme.DeepPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user?.name?.take(1)?.uppercase() ?: "P",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(user?.name ?: "শিক্ষার্থী", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
                    Text("রোল: " + (user?.role?.uppercase() ?: "STUDENT"), fontSize = 12.sp, color = Color.Gray)
                    Text("ইমেইল: " + (user?.email ?: "student@poralekha.bd"), fontSize = 11.sp, color = Color.DarkGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("সাধারণ সেটিংস (Settings Drawer)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // Role Switcher Options
        if (user != null) {
            if (user!!.role != "instructor" && !user!!.isDualRole && user!!.role != "super_admin") {
                ProfileOptionRow(
                    title = "শিক্ষক হয়ে ক্যারিয়ার গড়ুন (Become an Instructor)",
                    icon = Icons.Default.AddBusiness,
                    onClick = { onNavigate(Screen.BecomeInstructor) }
                )
            } else if (user!!.role == "super_admin") {
                ProfileOptionRow(
                    title = "এডমিন ড্যাশবোর্ড সুইচ",
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = { onNavigate(Screen.AdminDashboard) }
                )
            } else {
                ProfileOptionRow(
                    title = "শিক্ষক ড্যাশবোর্ড সুইচ",
                    icon = Icons.Default.SwapHoriz,
                    onClick = {
                        viewModel.switchRole("instructor")
                        onNavigate(Screen.InstructorDashboard)
                    }
                )
            }
        }

        ProfileOptionRow(
            title = "ক্রয় ও পেমেন্ট হিস্ট্রি (Purchase Ledger)",
            icon = Icons.Default.Receipt,
            onClick = { /* ledger flow click */ }
        )

        ProfileOptionRow(
            title = "পুশ নোটিফিকেশন সেটিংস",
            icon = Icons.Default.NotificationsActive,
            onClick = { /* notification setting flow */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Logout
        Button(
            onClick = {
                viewModel.logout()
                onNavigate(Screen.Login)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth().testTag("logout_button"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("লগআউট করুন (Log Out)", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileOptionRow(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = PoraTheme.DeepPurple, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontSize = 13.sp, modifier = Modifier.weight(1f), color = PoraTheme.TextDark)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

// --- NOTIFICATION CENTER SCREEN ---
@Composable
fun NotificationCenterScreen(viewModel: AppViewModel) {
    val list by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("notifications_screen")
    ) {
        Text("বিজ্ঞপ্তি কেন্দ্র (Notification Hub)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Spacer(modifier = Modifier.height(12.dp))

        if (list.isEmpty()) {
            EmptyStatePlaceholder(
                title = "কোনো বিজ্ঞপ্তি নেই",
                subtitle = "আপনার অ্যাকাউন্টের সাম্প্রতিক যেকোনো আপডেট বা নোটিফিকেশন এখানে প্রদর্শিত হবে।",
                icon = Icons.Default.NotificationsOff
            )
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                list.forEach { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PoraTheme.DeepPurple.copy(0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = PoraTheme.DeepPurple, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(notif.message, fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
        }
    }
}
