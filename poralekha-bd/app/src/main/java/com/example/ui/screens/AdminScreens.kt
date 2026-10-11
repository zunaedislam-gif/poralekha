package com.example.ui.screens

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.*
import com.example.ui.viewmodel.AppViewModel

// --- SUPER ADMIN DASHBOARD ---
@Composable
fun AdminDashboardScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val users by viewModel.allUsers.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val applications by viewModel.instructorApplications.collectAsState()
    val payouts by viewModel.allPayoutRequests.collectAsState()

    val totalStudents = users.filter { it.role == "student" }.size
    val totalInstructors = users.filter { it.role == "instructor" }.size
    val totalCourses = courses.size
    
    // Total gross sales calculation
    val grossSales = courses.sumOf { it.enrollmentCount * (if (it.discountPrice > 0) it.discountPrice else it.price) }
    val commissionShare = (grossSales * 0.30).toInt() // 30% Platform Share

    var platformCommPercent by remember { mutableStateOf(30) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
            .testTag("admin_dashboard")
    ) {
        // Welcome Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E2C))
                .padding(20.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("সুপার এডমিন কন্ট্রোল (Platform Oversight)", color = Color.White.copy(0.7f), fontSize = 12.sp)
                        Text("Super Admin Panel", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2E7D32), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Active", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Metrics cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricCard("মোট শিক্ষার্থী", totalStudents.toString(), Icons.Default.People, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                MetricCard("মোট শিক্ষক", totalInstructors.toString(), Icons.Default.School, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MetricCard("মোট কোর্সসমূহ", totalCourses.toString(), Icons.Default.Book, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                MetricCard("মোট রেভিনিউ (30%)", "৳$commissionShare", Icons.Default.Savings, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Commission configuration slider
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("প্ল্যাটফর্ম কমিশন সেটিংস (Platform Commission)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("বর্তমান রেট: $platformCommPercent% (প্রতি বিক্রয় থেকে কর্তন করা হবে)", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = platformCommPercent.toFloat(),
                        onValueChange = { platformCommPercent = it.toInt() },
                        valueRange = 10f..50f,
                        colors = SliderDefaults.colors(thumbColor = PoraTheme.DeepPurple, activeTrackColor = PoraTheme.DeepPurple)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Admin Actions Toolbar grid
            Text("এডমিন অ্যাকশনস (Administrative Modules)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PoraTheme.TextDark)
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminActionRow(
                    title = "ইউজার ম্যানেজমেন্ট",
                    desc = "${users.size} জন রেজিস্টার্ড শিক্ষার্থীর তালিকা ও ব্যান অপশন",
                    icon = Icons.Default.ManageAccounts,
                    onClick = { onNavigate(Screen.AdminUsers) },
                    testTag = "admin_users_action"
                )
                AdminActionRow(
                    title = "শিক্ষক আবেদন যাচাই",
                    desc = "${applications.filter { it.status == "pending" }.size}টি নতুন আবেদন পেন্ডিং রিভিউ",
                    icon = Icons.Default.VerifiedUser,
                    onClick = { onNavigate(Screen.AdminInstructorApps) },
                    testTag = "admin_instructor_apps_action"
                )
                AdminActionRow(
                    title = "কোর্স মডারেশন পেন্ডিং",
                    desc = "${courses.filter { it.status == "under_review" }.size}টি কোর্স পাবলিশ রিভিউতে আছে",
                    icon = Icons.Default.RateReview,
                    onClick = { onNavigate(Screen.AdminCourseMod) },
                    testTag = "admin_course_mod_action"
                )
                AdminActionRow(
                    title = "পেমেন্ট ও পে-আউট হিস্ট্রি",
                    desc = "${payouts.filter { it.status == "pending" }.size}টি টাকা উত্তোলন আবেদন পেন্ডিং",
                    icon = Icons.Default.ReceiptLong,
                    onClick = { onNavigate(Screen.AdminPayouts) },
                    testTag = "admin_payouts_action"
                )
            }
        }
    }
}

@Composable
fun AdminActionRow(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(Color.LightGray.copy(0.3f)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = PoraTheme.DeepPurple, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PoraTheme.TextDark)
                Text(desc, fontSize = 11.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

// --- USER MANAGEMENT SCREEN ---
@Composable
fun AdminUsersScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val users by viewModel.allUsers.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_users_screen")
    ) {
        Text("ইউজার তালিকা ও অ্যাকাউন্ট ওভারভিউ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            users.forEach { user ->
                val isSuspended = user.status == "suspended"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("রোল: " + user.role.uppercase(), fontSize = 11.sp, color = Color.Gray)
                            Text("যোগাযোগ: " + user.email + " | " + user.phone, fontSize = 10.sp, color = Color.DarkGray)
                        }
                        
                        // Action buttons to Suspend / Ban
                        Button(
                            onClick = { viewModel.updateUserStatus(user, !isSuspended) },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSuspended) Color(0xFF2E7D32) else Color.Red),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(if (isSuspended) "সচল করুন" else "স্থগিত", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- INSTRUCTOR APPLICATIONS SCREEN ---
@Composable
fun AdminInstructorAppsScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val applications by viewModel.instructorApplications.collectAsState()
    var reviewFeedback by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_instructor_apps_screen")
    ) {
        Text("শিক্ষক হওয়ার পেন্ডিং আবেদনসমূহ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Spacer(modifier = Modifier.height(12.dp))

        val pendings = applications.filter { it.status == "pending" }

        if (pendings.isEmpty()) {
            EmptyStatePlaceholder(
                title = "কোনো আবেদন পেন্ডিং নেই!",
                subtitle = "এই মুহূর্তে যাচাই করার মতো নতুন কোনো ইনস্ট্রাক্টর আবেদন জমা নেই।",
                icon = Icons.Default.VerifiedUser
            )
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                pendings.forEach { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("আবেদনকারী: ${app.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("দক্ষতার এরিয়া: ${app.expertArea}", fontSize = 13.sp, color = Color.DarkGray)
                            Text("অভিজ্ঞতা: ${app.experience}", fontSize = 13.sp, color = Color.DarkGray)
                            Text("পরিচিতি: ${app.bio}", fontSize = 12.sp, color = Color.Gray)
                            Text("ভেরিফিকেশন আইডি: ${app.idVerification}", fontSize = 11.sp, color = Color.Blue)

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = reviewFeedback,
                                onValueChange = { reviewFeedback = it },
                                label = { Text("মতামত বা রিজেকশনের কারণ") },
                                modifier = Modifier.fillMaxWidth().testTag("app_feedback_input"),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Button(
                                    onClick = {
                                        viewModel.reviewInstructorApplication(app, false, reviewFeedback)
                                        reviewFeedback = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.testTag("reject_app_button")
                                ) {
                                    Text("বাতিল (Reject)")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.reviewInstructorApplication(app, true, reviewFeedback)
                                        reviewFeedback = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    modifier = Modifier.testTag("approve_app_button")
                                ) {
                                    Text("অনুমোদন (Approve)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- COURSE MODERATION & CURRICULUM MANAGEMENT SCREEN ---
@Composable
fun AdminCourseModScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val pendingCoursesList by viewModel.pendingCourses.collectAsState()
    val allCoursesList by viewModel.allCourses.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Moderation, 1: Manage Courses
    var reviewFeedback by remember { mutableStateOf("") }

    // Course Creation Dialog States
    var showCreateCourseDialog by remember { mutableStateOf(false) }
    var newCourseTitle by remember { mutableStateOf("") }
    var newCourseSubtitle by remember { mutableStateOf("") }
    var newCourseCategory by remember { mutableStateOf("Development") }
    var newCoursePrice by remember { mutableStateOf("") }
    var newCourseDiscPrice by remember { mutableStateOf("") }
    var newCourseDesc by remember { mutableStateOf("") }
    var newCourseLearningPoints by remember { mutableStateOf("") }
    var newCourseRequirements by remember { mutableStateOf("") }

    // Expanded course curriculum state
    var expandedCourseId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_course_mod_screen")
    ) {
        Text("প্ল্যাটফর্ম কোর্স ও কারিকুলাম কন্ট্রোল", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Spacer(modifier = Modifier.height(12.dp))

        // Custom Navigation Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(0.15f), shape = RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { selectedTab = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 0) PoraTheme.DeepPurple else Color.Transparent,
                    contentColor = if (selectedTab == 0) Color.White else Color.Gray
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).testTag("tab_moderation_btn")
            ) {
                Text("পেন্ডিং রিভিউ (${pendingCoursesList.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = { selectedTab = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 1) PoraTheme.DeepPurple else Color.Transparent,
                    contentColor = if (selectedTab == 1) Color.White else Color.Gray
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).testTag("tab_manage_courses_btn")
            ) {
                Text("সকল কোর্স ও ডিলিট (${allCoursesList.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // ==================== TAB 0: MODERATION REVIEW ====================
            if (pendingCoursesList.isEmpty()) {
                EmptyStatePlaceholder(
                    title = "কোনো কোর্স পেন্ডিং নেই!",
                    subtitle = "নতুন তৈরি করা রিভিউয়ের উপযোগী কোনো কোর্স বর্তমানে পেন্ডিং তালিকায় নেই।",
                    icon = Icons.Default.RateReview
                )
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    pendingCoursesList.forEach { course ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(course.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("সাবটাইটেল: ${course.subtitle}", fontSize = 12.sp, color = Color.Gray)
                                Text("ইনস্ট্রাক্টর: ${course.instructorName}", fontSize = 13.sp, color = Color.DarkGray)
                                Text("ক্যাটাগরি: ${course.category} | মূল্য: ৳${course.price}", fontSize = 13.sp, color = Color.DarkGray)
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                OutlinedTextField(
                                    value = reviewFeedback,
                                    onValueChange = { reviewFeedback = it },
                                    label = { Text("ফিডব্যাক (সংশোধন বা বাতিলের কারণ)") },
                                    modifier = Modifier.fillMaxWidth().testTag("course_feedback_input"),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    Button(
                                        onClick = {
                                            viewModel.reviewCourse(course, false, reviewFeedback)
                                            reviewFeedback = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                        modifier = Modifier.testTag("reject_course_button")
                                    ) {
                                        Text("সংশোধন রিকোয়েস্ট")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            viewModel.reviewCourse(course, true, reviewFeedback)
                                            reviewFeedback = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        modifier = Modifier.testTag("approve_course_button")
                                    ) {
                                        Text("লাইভ পাবলিশ (Approve)")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ==================== TAB 1: ALL COURSES & DELETIONS ====================
            Column {
                // Course Creation FAB / Header Button
                Button(
                    onClick = { showCreateCourseDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_create_course_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("এডমিন প্যানেল থেকে সরাসরি কোর্স তৈরি করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (showCreateCourseDialog) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB)),
                        border = BorderStroke(1.2.dp, PoraTheme.DeepPurple)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("নতুন কোর্স সংযুক্তি ফর্ম (Support / Admin)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PoraTheme.DeepPurple)
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newCourseTitle,
                                onValueChange = { newCourseTitle = it },
                                label = { Text("কোর্সের নাম (Title)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newCourseSubtitle,
                                onValueChange = { newCourseSubtitle = it },
                                label = { Text("কোর্সের সাবটাইটেল (Subtitle)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newCoursePrice,
                                onValueChange = { newCoursePrice = it },
                                label = { Text("রেগুলার মূল্য (৳)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newCourseDiscPrice,
                                onValueChange = { newCourseDiscPrice = it },
                                label = { Text("ডিসকাউন্ট মূল্য (৳)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = newCourseDesc,
                                onValueChange = { newCourseDesc = it },
                                label = { Text("কোর্সের বিবরণ (Description)...") },
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { showCreateCourseDialog = false }) {
                                    Text("বাতিল করুন", color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newCourseTitle.isNotBlank()) {
                                            viewModel.createCourseByAdmin(
                                                title = newCourseTitle,
                                                subtitle = newCourseSubtitle,
                                                category = newCourseCategory,
                                                price = newCoursePrice.toIntOrNull() ?: 1200,
                                                discountPrice = newCourseDiscPrice.toIntOrNull() ?: 500,
                                                description = newCourseDesc,
                                                whatYouWillLearn = "সম্পূর্ণ স্কিল ট্র্যাকিং ও প্র্যাক্টিক্যাল কোডিং সেশন।",
                                                requirements = "বেসিক কম্পিউটার চালনা এবং ইন্টারনেট কানেকশন।",
                                                onSuccess = { showCreateCourseDialog = false }
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple)
                                ) {
                                    Text("কোর্স যোগ করুন")
                                }
                            }
                        }
                    }
                }

                // Scrollable Course List
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    allCoursesList.forEach { course ->
                        val isExpanded = expandedCourseId == course.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(course.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("শিক্ষক: ${course.instructorName} | স্ট্যাটাস: ${course.status.uppercase()}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                    
                                    Row {
                                        IconButton(
                                            onClick = { expandedCourseId = if (isExpanded) null else course.id },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = "Expand Curriculum",
                                                tint = PoraTheme.DeepPurple
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = {
                                                viewModel.deleteCourseByAdmin(course.id, onSuccess = {})
                                            },
                                            modifier = Modifier.size(28.dp).testTag("delete_course_btn_${course.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Course",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }

                                // Drilldown Curriculum Details
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Divider(color = Color.LightGray.copy(0.4f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("কারিকুলাম ও লেকচার মডারেশন (Drill-down Edit):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PoraTheme.DeepPurple)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    val sectionsFlow = remember(course.id) { viewModel.repository.getSectionsForCourseFlow(course.id) }
                                    val sections by sectionsFlow.collectAsState(initial = emptyList())

                                    if (sections.isEmpty()) {
                                        Text("এই কোর্সে কোনো সেকশন বা লেকচার সংযোজন করা হয়নি।", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
                                    } else {
                                        sections.forEach { sec ->
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color(0xFFF9F9FB), shape = RoundedCornerShape(4.dp))
                                                    .padding(8.dp)
                                                    .padding(bottom = 6.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("সেকশন: ${sec.title}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.DarkGray)
                                                    IconButton(
                                                        onClick = { viewModel.deleteSectionByAdmin(sec, onSuccess = {}) },
                                                        modifier = Modifier.size(20.dp)
                                                    ) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete Section", tint = Color.Red.copy(0.7f), modifier = Modifier.size(14.dp))
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))

                                                val lecturesFlow = remember(sec.id) { viewModel.repository.getLecturesForSectionFlow(sec.id) }
                                                val lectures by lecturesFlow.collectAsState(initial = emptyList())

                                                lectures.forEach { lec ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 12.dp)
                                                            .padding(vertical = 2.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(lec.title, fontSize = 10.sp, color = Color.DarkGray)
                                                        }
                                                        IconButton(
                                                            onClick = { viewModel.deleteLectureByAdmin(lec, onSuccess = {}) },
                                                            modifier = Modifier.size(18.dp)
                                                        ) {
                                                            Icon(Icons.Default.Close, contentDescription = "Delete Lecture", tint = Color.Red.copy(0.7f), modifier = Modifier.size(12.dp))
                                                        }
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- PAYOUT REVIEW & SETTINGS SCREEN ---
@Composable
fun AdminPayoutsScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val payouts by viewModel.allPayoutRequests.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_payouts_screen")
    ) {
        Text("শিক্ষকদের পেমেন্ট উত্তোলন রিকোয়েস্ট", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Spacer(modifier = Modifier.height(12.dp))

        val pendings = payouts.filter { it.status == "pending" }

        if (pendings.isEmpty()) {
            EmptyStatePlaceholder(
                title = "কোনো উত্তোলন আবেদন পেন্ডিং নেই!",
                subtitle = "সকল ইনস্ট্রাক্টর পেমেন্ট উত্তোলন রিকোয়েস্ট পরিশোধ সম্পন্ন করা হয়েছে।",
                icon = Icons.Default.ReceiptLong
            )
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                pendings.forEach { request ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("আবেদনকারী ID: " + request.instructorId, fontWeight = FontWeight.Bold)
                                Text("পরিমাণ: ৳" + request.amount, fontSize = 14.sp, color = PoraTheme.DeepPurple, fontWeight = FontWeight.Bold)
                                Text("মাধ্যম: " + request.paymentMethod + " (" + request.accountNumber + ")", fontSize = 11.sp, color = Color.Gray)
                            }
                            Button(
                                onClick = { viewModel.processPayout(request, "paid") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier.height(32.dp).testTag("approve_payout_button")
                            ) {
                                Text("পরিশোধ (Mark Paid)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
