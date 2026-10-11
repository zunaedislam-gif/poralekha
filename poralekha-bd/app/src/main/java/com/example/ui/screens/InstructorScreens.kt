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
import com.example.ui.viewmodel.*
import kotlinx.coroutines.launch

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

// --- BECOME AN INSTRUCTOR SCREEN ---
@Composable
fun BecomeInstructorScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val applications by viewModel.instructorApplications.collectAsState()
    
    val myApp = applications.firstOrNull { it.userId == user?.id }
    
    var bio by remember { mutableStateOf("") }
    var expertise by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var nidOrVerify by remember { mutableStateOf("") }
    
    var submitSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("become_instructor_screen")
    ) {
        Text("Become an Instructor 🎓", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Text("আপনার জ্ঞান ছড়িয়ে দিন এবং একটি বিশ্বস্ত শিক্ষক ক্যারিয়ার গড়ে তুলুন।", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        if (myApp != null) {
            // Display Application Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, PoraTheme.DeepPurple.copy(0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = when (myApp.status) {
                            "approved" -> Icons.Default.Verified
                            "rejected" -> Icons.Default.Cancel
                            else -> Icons.Default.HourglassEmpty
                        },
                        contentDescription = null,
                        tint = when (myApp.status) {
                            "approved" -> Color(0xFF2E7D32)
                            "rejected" -> Color.Red
                            else -> PoraTheme.AccentOrange
                        },
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "আবেদনের অবস্থা: " + when (myApp.status) {
                            "approved" -> "অনুমোদিত (Approved)"
                            "rejected" -> "বাতিল (Rejected)"
                            else -> "পর্যালোচনাধীন (Pending)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (myApp.status == "pending") "আমাদের এডমিন প্যানেল আপনার আবেদনটি রিভিউ করছে। ২৪-৪৮ ঘন্টার মধ্যে ফলাফল জানানো হবে।"
                               else if (myApp.status == "rejected") "দুঃখিত, আপনার আবেদনটি গ্রহণযোগ্য হয়নি। ফিডব্যাক: ${myApp.feedback}"
                               else "অভিনন্দন! আপনার আবেদনটি অনুমোদিত হয়েছে। প্রোফাইল থেকে সুইচ করে শিক্ষক ড্যাশবোর্ডে প্রবেশ করুন।",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            return
        }

        // Form fields
        OutlinedTextField(
            value = expertise,
            onValueChange = { expertise = it },
            label = { Text("দক্ষতার বিষয় (Expertise Area)") },
            placeholder = { Text("e.g. Android Development, UI/UX Design") },
            modifier = Modifier.fillMaxWidth().testTag("inst_exp_input"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = experience,
            onValueChange = { experience = it },
            label = { Text("অভিজ্ঞতার বিবরণ (Experience)") },
            placeholder = { Text("e.g. 5 Years as Lead Engineer") },
            modifier = Modifier.fillMaxWidth().testTag("inst_years_input"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("আপনার পরিচিতি (Bio)") },
            placeholder = { Text("আপনার সম্পর্কে সংক্ষিপ্ত পরিচিতি লিখুন...") },
            modifier = Modifier.fillMaxWidth().height(100.dp).testTag("inst_bio_input"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nidOrVerify,
            onValueChange = { nidOrVerify = it },
            label = { Text("NID / পাসপোর্ট নম্বর (আইডি ভেরিফিকেশন)") },
            placeholder = { Text("e.g. 199XXXXXXXXXX") },
            modifier = Modifier.fillMaxWidth().testTag("inst_nid_input"),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (bio.isNotBlank() && expertise.isNotBlank()) {
                    viewModel.applyBecomeInstructor(bio, expertise, experience, nidOrVerify, {
                        submitSuccess = true
                    })
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("submit_inst_app_button"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("আবেদন সাবমিট করুন", fontWeight = FontWeight.Bold)
        }
    }
}

// --- INSTRUCTOR DASHBOARD ---
@Composable
fun InstructorDashboardScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val courses by viewModel.instructorCourses.collectAsState()
    val payouts by viewModel.instructorPayoutRequests.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val folders by viewModel.folders.collectAsState()
    val resources by viewModel.resources.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val liveClasses by viewModel.liveClasses.collectAsState()

    val totalStudents = courses.sumOf { it.enrollmentCount }
    val averageRating = if (courses.isNotEmpty()) courses.map { it.rating }.average().toFloat() else 4.5f
    val totalRevenue = courses.sumOf { it.enrollmentCount * (if (it.discountPrice > 0) it.discountPrice else it.price) }
    val netRevenue = (totalRevenue * 0.70).toInt()

    var selectedCourseId by remember { mutableStateOf(1) }
    val activeCourse = courses.find { it.id == selectedCourseId } ?: courses.firstOrNull()

    // Tab states: 0: Overview, 1: Curriculum, 2: Live Hub, 3: Exams & Grading
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
            .testTag("instructor_dashboard")
    ) {
        // Welcome Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PoraTheme.DeepPurple)
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("শিক্ষক ড্যাশবোর্ড (পড়ালেখা Instructor Platform)", color = Color.White.copy(0.7f), fontSize = 11.sp)
                        Text(user?.name ?: "শিক্ষক", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewModel.switchRole("student") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f)),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Student View", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        // Active Course Picker Row
        if (courses.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PoraTheme.DeepPurple.copy(0.05f))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("কোর্স নির্বাচন: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                Spacer(modifier = Modifier.width(8.dp))
                courses.forEach { c ->
                    val isSelected = c.id == selectedCourseId
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(
                                color = if (isSelected) PoraTheme.DeepPurple else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PoraTheme.DeepPurple else Color.LightGray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedCourseId = c.id }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = c.title,
                            color = if (isSelected) Color.White else Color.DarkGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Horizontal Navigation Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = PoraTheme.DeepPurple
        ) {
            val tabs = listOf("ওভারভিউ", "কারিকুলাম ও মিডিয়া", "লাইভ ক্লাস ব্রডকাস্ট", "পরীক্ষা ও গ্রেডিং")
            val icons = listOf(Icons.Default.Dashboard, Icons.Default.Folder, Icons.Default.Podcasts, Icons.Default.Quiz)
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(icons[idx], contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }
        }

        // Tab Content Router
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF9F9FB))
        ) {
            when (selectedTab) {
                0 -> OverviewTabContent(
                    courses = courses,
                    payouts = payouts,
                    totalStudents = totalStudents,
                    averageRating = averageRating,
                    netRevenue = netRevenue,
                    onNavigate = onNavigate
                )
                1 -> CurriculumTabContent(
                    courseId = selectedCourseId,
                    viewModel = viewModel,
                    folders = folders.filter { it.courseId == selectedCourseId },
                    resources = resources.filter { it.courseId == selectedCourseId }
                )
                2 -> LiveHubTabContent(
                    courseId = selectedCourseId,
                    viewModel = viewModel,
                    liveClasses = liveClasses.filter { it.courseId == selectedCourseId }
                )
                3 -> ExamsGradingTabContent(
                    courseId = selectedCourseId,
                    viewModel = viewModel,
                    exams = exams.filter { it.courseId == selectedCourseId },
                    submissions = submissions.filter { sub -> exams.any { ex -> ex.id == sub.examId && ex.courseId == selectedCourseId } }
                )
            }
        }
    }
}

// ==================== TAB 0: OVERVIEW CONTENT ====================
@Composable
fun OverviewTabContent(
    courses: List<CourseEntity>,
    payouts: List<PayoutRequestEntity>,
    totalStudents: Int,
    averageRating: Float,
    netRevenue: Int,
    onNavigate: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricCard("মোট শিক্ষার্থী", totalStudents.toString(), Icons.Default.Group, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            MetricCard("মোট কোর্স", courses.size.toString(), Icons.Default.Book, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricCard("মোট উপার্জন (Net)", "৳$netRevenue", Icons.Default.Payments, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            MetricCard("গড় রেটিং", String.format("%.1f ★", averageRating), Icons.Default.Star, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Earnings chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("মাসিক আয়ের গতিধারা (Monthly Earnings)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PoraTheme.TextDark)
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(color = Color.LightGray, start = androidx.compose.ui.geometry.Offset(0f, size.height), end = androidx.compose.ui.geometry.Offset(size.width, size.height), strokeWidth = 2f)
                        val points = listOf(0.15f, 0.3f, 0.25f, 0.55f, 0.7f, 0.95f)
                        val stepX = size.width / (points.size - 1)
                        for (i in 0 until points.size - 1) {
                            val x1 = i * stepX
                            val y1 = size.height - (points[i] * size.height)
                            val x2 = (i + 1) * stepX
                            val y2 = size.height - (points[i+1] * size.height)
                            drawLine(color = PoraTheme.DeepPurple, start = androidx.compose.ui.geometry.Offset(x1, y1), end = androidx.compose.ui.geometry.Offset(x2, y2), strokeWidth = 5f)
                            drawCircle(color = PoraTheme.AccentYellow, radius = 6f, center = androidx.compose.ui.geometry.Offset(x1, y1))
                        }
                        drawCircle(color = PoraTheme.AccentYellow, radius = 6f, center = androidx.compose.ui.geometry.Offset(size.width, size.height - (points.last() * size.height)))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Feb", "Mar", "Apr", "May", "Jun", "Jul").forEach { Text(it, fontSize = 9.sp, color = Color.Gray) }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions Row
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { onNavigate(Screen.CourseWizard) },
                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন কোর্স", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(6.dp))
            OutlinedButton(
                onClick = { onNavigate(Screen.PayoutRequest) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("টাকা উত্তোলন", fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(6.dp))
            OutlinedButton(
                onClick = { onNavigate(Screen.QAInbox) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.QuestionAnswer, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("প্রশ্নোত্তর", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("আপনার কোর্স ও মডারেশন স্ট্যাটাস", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
        Spacer(modifier = Modifier.height(8.dp))

        courses.forEach { course ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(45.dp).background(PoraTheme.DeepPurple.copy(0.12f), shape = RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Class, contentDescription = null, tint = PoraTheme.DeepPurple)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(course.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("৳${course.price}", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("শিক্ষার্থী: ${course.enrollmentCount} জন", fontSize = 11.sp, color = PoraTheme.DeepPurple, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (course.status) {
                                    "published" -> Color(0xFFE8F5E9)
                                    "rejected" -> Color(0xFFFFEBEE)
                                    "under_review" -> Color(0xFFFFF3E0)
                                    else -> Color.LightGray
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = course.status.uppercase(),
                            color = when (course.status) {
                                "published" -> Color(0xFF2E7D32)
                                "rejected" -> Color.Red
                                "under_review" -> PoraTheme.AccentOrange
                                else -> Color.DarkGray
                            },
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==================== TAB 1: CURRICULUM & VIDEOS/SLIDES ====================
@Composable
fun CurriculumTabContent(
    courseId: Int,
    viewModel: AppViewModel,
    folders: List<FolderModel>,
    resources: List<ResourceModel>
) {
    var newFolderName by remember { mutableStateOf("") }
    var activeFolderIdForResources by remember { mutableStateOf<Int?>(null) }

    // Video form states
    var lecTitle by remember { mutableStateOf("") }
    var lecDesc by remember { mutableStateOf("") }
    var thumbnailPresetIndex by remember { mutableStateOf(0) }
    val thumbnailPresets = listOf("android_banner.png", "uiux_vector.jpg", "digi_market.png", "web_mockup.png")
    var videoFileName by remember { mutableStateOf("") }
    var selectedVideoUri by remember { mutableStateOf("") }
    var customVideoUrl by remember { mutableStateOf("") }
    var isPreviewLec by remember { mutableStateOf(false) }

    // Slide/Doc form states
    var resourceTitle by remember { mutableStateOf("") }
    var selectedFileType by remember { mutableStateOf("pdf") } // pdf, ppt, zip, doc

    val context = LocalContext.current
    var docFileName by remember { mutableStateOf("") }

    // Video picker launcher
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            videoFileName = getFileName(context, uri)
            selectedVideoUri = uri.toString()
        }
    }

    // Slide/Doc picker launcher
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = getFileName(context, uri)
            docFileName = name
            resourceTitle = name.substringBeforeLast(".")
            if (name.endsWith(".pdf", true)) {
                selectedFileType = "pdf"
            } else if (name.endsWith(".ppt", true) || name.endsWith(".pptx", true)) {
                selectedFileType = "ppt"
            } else if (name.endsWith(".zip", true)) {
                selectedFileType = "zip"
            }
        }
    }

    // Storage permission launcher for Videos
    val videoPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // On modern Android (Tiramisu+) we use the Photo/Video picker or GetContent directly.
        // But we always perform the permission check request if needed as requested.
        videoPickerLauncher.launch("video/*")
    }

    // Storage permission launcher for Documents
    val docPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        docPickerLauncher.launch("*/*")
    }

    // Simulation Upload States
    var isUploadingVideo by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableStateOf(0f) }
    var uploadStatusMessage by remember { mutableStateOf("") }

    var isUploadingDoc by remember { mutableStateOf(false) }
    var docProgress by remember { mutableStateOf(0f) }

    // Safe deletion toast warning for full course
    var showDeleteCourseToast by remember { mutableStateOf(false) }

    LaunchedEffect(isUploadingVideo) {
        if (isUploadingVideo) {
            videoProgress = 0f
            uploadStatusMessage = "ভিডিও প্রসেসিং ও অপ্টিমাইজেশন চলছে..."
            while (videoProgress < 1.0f) {
                kotlinx.coroutines.delay(150)
                videoProgress += 0.05f
                if (videoProgress >= 0.5f) {
                    uploadStatusMessage = "ভিডিও কম্প্রেশন এবং স্লাইড এনকোডিং হচ্ছে..."
                }
            }
            isUploadingVideo = false
            uploadStatusMessage = "ভিডিও আপলোড সম্পন্ন! ফোল্ডারে যোগ করা হয়েছে।"
            val finalVideoUrl = when {
                selectedVideoUri.isNotBlank() -> selectedVideoUri
                customVideoUrl.isNotBlank() -> customVideoUrl
                else -> "https://www.w3schools.com/html/mov_bbb.mp4"
            }
            viewModel.addLectureByInstructor(
                courseId = courseId,
                sectionId = activeFolderIdForResources ?: 1,
                title = lecTitle,
                description = lecDesc,
                videoUrl = finalVideoUrl,
                duration = "12:15",
                orderIndex = 1,
                isPreview = isPreviewLec
            )
            lecTitle = ""
            lecDesc = ""
            videoFileName = ""
            selectedVideoUri = ""
            customVideoUrl = ""
        }
    }

    LaunchedEffect(isUploadingDoc) {
        if (isUploadingDoc) {
            docProgress = 0f
            while (docProgress < 1.0f) {
                kotlinx.coroutines.delay(100)
                docProgress += 0.1f
            }
            isUploadingDoc = false
            viewModel.addResource(
                folderId = activeFolderIdForResources ?: 1,
                courseId = courseId,
                title = resourceTitle,
                fileType = selectedFileType
            )
            resourceTitle = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("সিলেবাস ফোল্ডার ও কারিকুলাম ম্যাটেরিয়াল ম্যানেজার 🗂️", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PoraTheme.DeepPurple)
        Text("লেকচার সাজাতে ফোল্ডার তৈরি করুন এবং ভিডিও, স্লাইড, অ্যাসাইনমেন্ট সোর্স ফাইল আপলোড করুন।", fontSize = 11.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))

        // Create Folder Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("নতুন চ্যাপ্টার ফোল্ডার তৈরি করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.TextDark)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newFolderName,
                        onValueChange = { newFolderName = it },
                        placeholder = { Text("e.g. সেকশন ২: অ্যাডভান্সড স্টেট") },
                        modifier = Modifier.weight(1f),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newFolderName.isNotBlank()) {
                                viewModel.addFolder(courseId, newFolderName)
                                newFolderName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("তৈরি করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (folders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("কোনো কারিকুলাম ফোল্ডার পাওয়া যায়নি। প্রথমে একটি ফোল্ডার তৈরি করুন।", fontSize = 12.sp, color = Color.Gray)
            }
        } else {
            folders.forEach { folder ->
                val isSelectedForAdd = activeFolderIdForResources == folder.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, if (isSelectedForAdd) PoraTheme.DeepPurple else Color.LightGray.copy(0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = PoraTheme.AccentOrange, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(folder.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PoraTheme.TextDark)
                            }
                            Row {
                                Box(
                                    modifier = Modifier
                                        .background(PoraTheme.DeepPurple.copy(0.1f), shape = RoundedCornerShape(4.dp))
                                        .clickable { activeFolderIdForResources = if (isSelectedForAdd) null else folder.id }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(if (isSelectedForAdd) "বন্ধ করুন" else "আপলোড প্যানেল", fontSize = 10.sp, color = PoraTheme.DeepPurple, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { viewModel.deleteFolder(folder.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Folder", tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Display resources in this folder
                        val folderResources = resources.filter { it.folderId == folder.id }
                        if (folderResources.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color.LightGray.copy(0.4f))
                            Spacer(modifier = Modifier.height(4.dp))
                            folderResources.forEach { res ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = when (res.fileType) {
                                                "pdf" -> Icons.Default.PictureAsPdf
                                                "zip" -> Icons.Default.FolderOpen
                                                else -> Icons.Default.Attachment
                                            },
                                            contentDescription = null,
                                            tint = PoraTheme.DeepPurple,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${res.title} (${res.fileType.uppercase()}) - ${res.size}", fontSize = 11.sp, color = Color.DarkGray)
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteResource(res.id) },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete file", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }

                        // Upload Form Expandable inside active folder
                        if (isSelectedForAdd) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = PoraTheme.DeepPurple.copy(0.3f))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Tab internally: Upload Video OR Slide/Docs
                            var uploadType by remember { mutableStateOf("video") }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { uploadType = "video" },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (uploadType == "video") PoraTheme.DeepPurple.copy(0.1f) else Color.Transparent
                                    ),
                                    border = BorderStroke(1.dp, if (uploadType == "video") PoraTheme.DeepPurple else Color.LightGray),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("লেকচার ভিডিও আপলোড", fontSize = 10.sp, color = PoraTheme.DeepPurple)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedButton(
                                    onClick = { uploadType = "doc" },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (uploadType == "doc") PoraTheme.DeepPurple.copy(0.1f) else Color.Transparent
                                    ),
                                    border = BorderStroke(1.dp, if (uploadType == "doc") PoraTheme.DeepPurple else Color.LightGray),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("স্লাইড/ডকুমেন্ট ফাইল", fontSize = 10.sp, color = PoraTheme.DeepPurple)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (uploadType == "video") {
                                // Video Lecture Builder
                                OutlinedTextField(
                                    value = lecTitle,
                                    onValueChange = { lecTitle = it },
                                    placeholder = { Text("লেকচারের নাম / Title (e.g. কটলিন সেফ এপিআই)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = lecDesc,
                                    onValueChange = { lecDesc = it },
                                    placeholder = { Text("লেকচারের বিবরণ / Description (e.g. এই ক্লাসে আমরা কটলিনের নাল সেফটি শিখবো)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                    shape = RoundedCornerShape(8.dp),
                                    maxLines = 3
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Video Thumbnail Preset Row
                                Text("লেকচার থাম্বনেইল ছবি সেট করুন:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    thumbnailPresets.forEachIndexed { index, name ->
                                        val isSel = thumbnailPresetIndex == index
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = if (isSel) PoraTheme.DeepPurple else Color.LightGray.copy(0.3f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .border(1.dp, if (isSel) PoraTheme.DeepPurple else Color.Transparent)
                                                .clickable { thumbnailPresetIndex = index }
                                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Text(name, fontSize = 9.sp, color = if (isSel) Color.White else Color.DarkGray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Pick Video File / Custom URL
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Button(
                                            onClick = {
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                                    videoPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                                                } else {
                                                    videoPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple.copy(0.12f)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp), tint = PoraTheme.DeepPurple)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("ভিডিও ফাইল আপলোড করুন (Select Video)", fontSize = 11.sp, color = PoraTheme.DeepPurple, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (videoFileName.isEmpty()) "কোনো ফাইল বাছা হয়নি" else videoFileName,
                                            fontSize = 11.sp,
                                            color = if (videoFileName.isEmpty()) Color.Gray else Color(0xFF2E7D32),
                                            fontWeight = if (videoFileName.isEmpty()) FontWeight.Normal else FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("অথবা অনলাইন ভিডিও লিংক দিন (ঐচ্ছিক):", fontSize = 11.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    OutlinedTextField(
                                        value = customVideoUrl,
                                        onValueChange = { customVideoUrl = it },
                                        placeholder = { Text("e.g. https://www.w3schools.com/html/mov_bbb.mp4") },
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                        shape = RoundedCornerShape(8.dp),
                                        singleLine = true
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(checked = isPreviewLec, onCheckedChange = { isPreviewLec = it })
                                    Text("ফ্রি ট্রায়াল প্রিভিউ হিসেবে রাখুন (Previewable)", fontSize = 11.sp, color = Color.DarkGray)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (isUploadingVideo) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        LinearProgressIndicator(
                                            progress = videoProgress,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = PoraTheme.DeepPurple
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$uploadStatusMessage (${(videoProgress * 100).toInt()}%)",
                                            fontSize = 10.sp,
                                            color = PoraTheme.DeepPurple,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    val canUpload = lecTitle.isNotBlank() && (videoFileName.isNotBlank() || customVideoUrl.isNotBlank() || true)
                                    Button(
                                        onClick = {
                                            if (lecTitle.isNotBlank()) {
                                                isUploadingVideo = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = lecTitle.isNotBlank()
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ভিডিও আপলোড ও কম্প্রেশন শুরু করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                            } else {
                                // Slides / Document Builder
                                OutlinedTextField(
                                    value = resourceTitle,
                                    onValueChange = { resourceTitle = it },
                                    placeholder = { Text("ফাইল/স্লাইডের নাম (e.g. লেকচার স্লাইড ১)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("ফাইল টাইপ: ", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    listOf("pdf", "ppt", "zip").forEach { t ->
                                        val selected = selectedFileType == t
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .background(
                                                    color = if (selected) PoraTheme.DeepPurple else Color.LightGray.copy(0.3f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .clickable { selectedFileType = t }
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(t.uppercase(), fontSize = 10.sp, color = if (selected) Color.White else Color.DarkGray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Pick Document File
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                                docPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                            } else {
                                                docPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.DarkGray)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ফাইল বাছুন (PDF/ZIP/PPT)", fontSize = 10.sp, color = Color.DarkGray)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (docFileName.isEmpty()) "কোনো ফাইল সিলেক্ট করা হয়নি" else docFileName,
                                        fontSize = 10.sp,
                                        color = if (docFileName.isEmpty()) Color.Red else Color(0xFF2E7D32),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (isUploadingDoc) {
                                    LinearProgressIndicator(progress = docProgress, modifier = Modifier.fillMaxWidth())
                                } else {
                                    Button(
                                        onClick = {
                                            if (resourceTitle.isNotBlank()) {
                                                isUploadingDoc = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = resourceTitle.isNotBlank()
                                    ) {
                                        Text("ম্যাটেরিয়াল ফাইল আপলোড ও সেভ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Role-Based Safety Warning: Instructor cannot delete full courses
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            border = BorderStroke(1.dp, PoraTheme.AccentOrange)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = PoraTheme.AccentOrange, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("গুরুত্বপূর্ণ নিরাপত্তা নির্দেশিকা", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.AccentOrange)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "আইন ও শর্তাবলী অনুযায়ী ইনস্ট্রাকটর বা শিক্ষকরা নিজেরা সরাসরি সম্পূর্ণ কোর্স ডিলিট করতে পারেন না। সম্পূর্ণ কোর্স ডিলিট করার জন্য সুপার এডমিনের কাছে আবেদন জানাতে হবে। তবে শিক্ষকরা নিজস্ব লেকচার, স্লাইড, অ্যাসাইনমেন্ট ও কুইজসমূহ যেকোনো সময় সম্পাদন বা ডিলিট করতে পারবেন।",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { showDeleteCourseToast = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(0.8f)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("সম্পূর্ণ কোর্স ডিলিট করার আবেদন", fontSize = 10.sp, color = Color.White)
                }

                if (showDeleteCourseToast) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("⚠️ রিকোয়েস্ট পাঠানো হয়েছে! সুপার এডমিন রিভিউ করে কোর্স ডিলিশন সম্পন্ন করবেন।", fontSize = 10.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== TAB 2: LIVE BROADCAST CONSOLE ====================
@Composable
fun LiveHubTabContent(
    courseId: Int,
    viewModel: AppViewModel,
    liveClasses: List<LiveClassModel>
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-07-26") }
    var time by remember { mutableStateOf("রাত ০৯:৩০ টা") }

    var activeLiveClassId by remember { mutableStateOf<Int?>(null) }
    val activeClass = liveClasses.firstOrNull { it.id == activeLiveClassId }

    // Broadcaster States
    var isBroadcasting by remember { mutableStateOf(false) }
    var whiteboardInput by remember { mutableStateOf("") }
    var lowBandwidthMode by remember { mutableStateOf(false) }

    // Live Poll state
    var pollQ by remember { mutableStateOf("") }
    var pollOpt1 by remember { mutableStateOf("") }
    var pollOpt2 by remember { mutableStateOf("") }
    var isPollActive by remember { mutableStateOf(false) }

    // Live chat reply
    var chatReplyText by remember { mutableStateOf("") }

    // Simulated microphone meter waveform animation
    var micWaveHeight by remember { mutableStateOf(10f) }
    LaunchedEffect(isBroadcasting) {
        if (isBroadcasting) {
            while (isBroadcasting) {
                kotlinx.coroutines.delay(100)
                micWaveHeight = (10f + Math.random() * 80f).toFloat()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (!isBroadcasting) {
            Text("লাইভ ক্লাস শিডিউলার ও ব্রডকাস্ট প্যানেল 🎙️", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PoraTheme.DeepPurple)
            Text("শিক্ষার্থীদের জন্য সরাসরি লাইভ ক্লাস শিডিউল করুন এবং ডিজিটাল হোয়াইটবোর্ড ও পোলের মাধ্যমে ইন্টারেক্টিভ সেশন নিন।", fontSize = 11.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Schedule form
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("নতুন লাইভ ক্লাস শিডিউল করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.TextDark)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("লাইভ ক্লাসের শিরোনাম (e.g. কম্পোজ রিকম্পোজিশন ডিপ ডাইভ)") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = { date = it },
                            label = { Text("তারিখ (YYYY-MM-DD)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = time,
                            onValueChange = { time = it },
                            label = { Text("সময়", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                viewModel.scheduleLiveClass(courseId, title, date, time)
                                title = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("লাইভ ক্লাস শিডিউল কনফার্ম", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("শিডিউলড লাইভ ক্লাসসমূহ:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.TextDark)
            Spacer(modifier = Modifier.height(8.dp))

            if (liveClasses.isEmpty()) {
                Text("বর্তমানে কোনো লাইভ ক্লাস শিডিউল করা নেই।", fontSize = 11.sp, color = Color.Gray)
            } else {
                liveClasses.forEach { lc ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(lc.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.TextDark)
                                    Text("তারিখ ও সময়: ${lc.date} | ${lc.time}", fontSize = 10.sp, color = Color.Gray)
                                }
                                Button(
                                    onClick = {
                                        activeLiveClassId = lc.id
                                        isBroadcasting = true
                                        viewModel.startLiveClass(lc.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ক্লাস শুরু", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ================== active BROADCASTING CONSOLE SCREEN ==================
            if (activeClass != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Red, shape = CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LIVE BROADCASTING ACTIVE", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Red)
                    }
                    Button(
                        onClick = {
                            isBroadcasting = false
                            viewModel.stopLiveClass(activeClass.id)
                            activeLiveClassId = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("ব্রডকাস্ট শেষ করুন", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Streaming View Mock Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(PoraTheme.SurfaceDark, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (lowBandwidthMode) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Hearing, contentDescription = null, tint = PoraTheme.AccentYellow, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("লো-ব্যান্ডউইথ অডিও ব্রডকাস্ট মোড সচল", fontSize = 11.sp, color = Color.White)
                            Text("ভিডিও ক্যামেরা ব্যান্ডউইথ বাঁচাতে অফ করা হয়েছে", fontSize = 9.sp, color = Color.LightGray)
                        }
                    } else {
                        // Real Camera Preview using CameraX
                        LiveCameraPreview(
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .background(Color.Black.copy(0.6f), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("লাইভ ক্যামেরা সচল 🎥 (১০৮০p ৩০fps)", color = Color.Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Low-Bandwidth Mode Switcher Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("লো-ব্যান্ডউইথ মোড চালু করুন (Audio Only)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("দুর্বল ইন্টারনেট বা ৩G এলাকায় স্মুথ ক্লাস নিতে ক্যামেরা অফ করে শুধুমাত্র অডিও ও হোয়াইটবোর্ড ব্রডকাস্ট হবে।", fontSize = 9.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = lowBandwidthMode,
                            onCheckedChange = { lowBandwidthMode = it },
                            thumbContent = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Whiteboard Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, PoraTheme.DeepPurple.copy(0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Gesture, contentDescription = null, tint = PoraTheme.DeepPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("শিক্ষক ইন্টারেক্টিভ হোয়াইটবোর্ড (Digital Canvas)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.DeepPurple)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFFDE7), shape = RoundedCornerShape(4.dp))
                                .border(1.dp, Color(0xFFFFF9C4))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = if (activeClass.whiteboardText.isEmpty()) "হোয়াইটবোর্ড ফাকা। কিছু লিখুন।" else activeClass.whiteboardText,
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            OutlinedTextField(
                                value = whiteboardInput,
                                onValueChange = { whiteboardInput = it },
                                placeholder = { Text("হোয়াইটবোর্ডে ড্রয়িং বা ইকুয়েশন টেক্সট লিখুন...", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                shape = RoundedCornerShape(6.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    viewModel.updateLiveWhiteboard(activeClass.id, whiteboardInput)
                                    whiteboardInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("আঁকুন", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Poll Creator Desk
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Poll, contentDescription = null, tint = PoraTheme.AccentOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("লাইভ পোল ও ইনস্ট্যান্ট পোল ক্রিয়েটর", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.AccentOrange)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        if (isPollActive && activeClass.pollQuestion != null) {
                            // Poll status showing votes live
                            Text("চলতি লাইভ পোল: ${activeClass.pollQuestion}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            activeClass.pollOptions.forEachIndexed { i, opt ->
                                val votes = activeClass.pollVotes.getOrElse(i) { 0 }
                                val total = activeClass.pollVotes.sum().coerceAtLeast(1)
                                val pct = (votes.toFloat() / total * 100).toInt()
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(opt, fontSize = 10.sp)
                                        Text("$votes ভোট ($pct%)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    LinearProgressIndicator(progress = votes.toFloat() / total, modifier = Modifier.fillMaxWidth().height(6.dp), color = PoraTheme.AccentOrange)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedButton(
                                onClick = { isPollActive = false },
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("পোল বন্ধ করুন", fontSize = 10.sp)
                            }
                        } else {
                            OutlinedTextField(
                                value = pollQ,
                                onValueChange = { pollQ = it },
                                placeholder = { Text("পোলের প্রশ্ন? (e.g. কালকে কি এক্সট্রা সেশন নিবো?)", fontSize = 10.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                shape = RoundedCornerShape(6.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row {
                                OutlinedTextField(
                                    value = pollOpt1,
                                    onValueChange = { pollOpt1 = it },
                                    placeholder = { Text("অপশন ১", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedTextField(
                                    value = pollOpt2,
                                    onValueChange = { pollOpt2 = it },
                                    placeholder = { Text("অপশন ২", fontSize = 10.sp) },
                                    modifier = Modifier.weight(1f),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp),
                                    shape = RoundedCornerShape(6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (pollQ.isNotBlank() && pollOpt1.isNotBlank() && pollOpt2.isNotBlank()) {
                                        viewModel.addLivePoll(activeClass.id, pollQ, listOf(pollOpt1, pollOpt2))
                                        isPollActive = true
                                        pollQ = ""
                                        pollOpt1 = ""
                                        pollOpt2 = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.AccentOrange),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("লাইভ পোল সম্প্রচার শুরু করুন", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hand-Raise approval Desk
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BackHand, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("শিক্ষার্থীর কথা বলার আবেদন (Hand-Raise Monitor)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF2E7D32))
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        if (activeClass.handRaises.isEmpty()) {
                            Text("বর্তমানে কোনো শিক্ষার্থীর হাত তোলার আবেদন নেই।", fontSize = 10.sp, color = Color.Gray)
                        } else {
                            activeClass.handRaises.forEach { student ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("$student কথা বলতে চান", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row {
                                        Button(
                                            onClick = { /* Simulated Approval */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("মাইক অন", fontSize = 9.sp, color = Color.White)
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.toggleHandRaise(activeClass.id, student, false) },
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("মিউট", fontSize = 9.sp, color = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Chat Panel & Question Pinning
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("লাইভ চ্যাট এবং প্রশ্নোত্তর পিনবোর্ড", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.TextDark)
                        Spacer(modifier = Modifier.height(6.dp))

                        if (activeClass.pinnedQuestion != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(6.dp))
                                    .border(1.dp, PoraTheme.AccentOrange.copy(0.4f), shape = RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("📌 পিন করা প্রশ্ন (Featured Question):", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PoraTheme.AccentOrange)
                                    Text(activeClass.pinnedQuestion, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "আন-পিন করুন",
                                        fontSize = 9.sp,
                                        color = Color.Red,
                                        modifier = Modifier
                                            .clickable { viewModel.pinLiveQuestion(activeClass.id, null) }
                                            .align(Alignment.End)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Chat messages lists
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(6.dp))
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            activeClass.liveChatMessages.forEach { chatMsg ->
                                val sender = chatMsg.sender
                                val msg = chatMsg.message
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("$sender: $msg", fontSize = 10.sp, color = Color.DarkGray)
                                    }
                                    Text(
                                        text = "পিন করুন",
                                        fontSize = 9.sp,
                                        color = PoraTheme.DeepPurple,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { viewModel.pinLiveQuestion(activeClass.id, msg) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row {
                            OutlinedTextField(
                                value = chatReplyText,
                                onValueChange = { chatReplyText = it },
                                placeholder = { Text("শিক্ষার্থীদের উদ্দেশ্যে চ্যাট লিখুন...", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                shape = RoundedCornerShape(6.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (chatReplyText.isNotBlank()) {
                                        viewModel.addLiveChatMessage(activeClass.id, "Instructor", chatReplyText)
                                        chatReplyText = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("পাঠান", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 3: EXAMS & GRADINGS CONSOLE ====================
@Composable
fun ExamsGradingTabContent(
    courseId: Int,
    viewModel: AppViewModel,
    exams: List<ExamModel>,
    submissions: List<ExamSubmission>
) {
    // Creator mode states
    var examTitle by remember { mutableStateOf("") }
    var examType by remember { mutableStateOf("mcq") } // mcq or written
    var totalMarksInput by remember { mutableStateOf("30") }
    var durationInput by remember { mutableStateOf("15") }

    // MCQ question builder state
    var qText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctOptIndex by remember { mutableStateOf(0) }
    var explanationText by remember { mutableStateOf("") }

    val createdQuestionsList = remember { mutableStateListOf<ExamQuestion>() }

    // Written question instructions
    var writtenQuestionText by remember { mutableStateOf("") }

    // Grading state
    var activeGradingSubmissionId by remember { mutableStateOf<Int?>(null) }
    val gradingSubmission = submissions.firstOrNull { it.id == activeGradingSubmissionId }

    // Interactive correction ticks simulating pdf correction sheet annotations
    val correctionTicks = remember { mutableStateListOf(false, false, false, false) }
    var gradeScore by remember { mutableStateOf("80") }
    var gradeFeedback by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (activeGradingSubmissionId == null) {
            Text("পরীক্ষা বিল্ডার ও উত্তরপত্র মূল্যায়ন কনসোল 🎓", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PoraTheme.DeepPurple)
            Text("নতুন MCQ পরীক্ষা তৈরি করুন অথবা শিক্ষার্থীদের জমা দেওয়া অ্যাসাইনমেন্টের লিখিত খাতা গ্রেড ও চেক করুন।", fontSize = 11.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Build new exam panel
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("১. নতুন পরীক্ষা বা অ্যাসাইনমেন্ট তৈরি করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.TextDark)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = examTitle,
                        onValueChange = { examTitle = it },
                        placeholder = { Text("পরীক্ষার নাম (e.g. চ্যাপ্টার ১ মূল্যায়ন কুইজ)") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("টাইপ: ", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        listOf("mcq" to "MCQ কুইজ", "written" to "লিখিত পরীক্ষা/PDF").forEach { (type, label) ->
                            val active = examType == type
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .background(
                                        color = if (active) PoraTheme.DeepPurple else Color.LightGray.copy(0.3f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        examType = type
                                        if (type == "mcq") {
                                            totalMarksInput = "30"
                                            durationInput = "15"
                                        } else {
                                            totalMarksInput = "100"
                                            durationInput = "120"
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(label, fontSize = 10.sp, color = if (active) Color.White else Color.DarkGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        OutlinedTextField(
                            value = totalMarksInput,
                            onValueChange = { totalMarksInput = it },
                            label = { Text("মোট মার্কস", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = durationInput,
                            onValueChange = { durationInput = it },
                            label = { Text("সময়সীমা (মিনিট)", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (examType == "mcq") {
                        // MCQ Builder Form
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(0.2f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("MCQ প্রশ্ন সেট নির্মাতা (Questions Added: ${createdQuestionsList.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PoraTheme.DeepPurple)
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = qText,
                                    onValueChange = { qText = it },
                                    placeholder = { Text("প্রশ্ন টাইপ করুন...", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                listOf(optA to "ক)", optB to "খ)", optC to "গ)", optD to "ঘ)").forEachIndexed { index, (value, prefix) ->
                                    OutlinedTextField(
                                        value = when (index) {
                                            0 -> optA
                                            1 -> optB
                                            2 -> optC
                                            else -> optD
                                        },
                                        onValueChange = { text ->
                                            when (index) {
                                                0 -> optA = text
                                                1 -> optB = text
                                                2 -> optC = text
                                                else -> optD = text
                                            }
                                        },
                                        placeholder = { Text("অপশন ${prefix}", fontSize = 10.sp) },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text("সঠিক অপশনটি সিলেক্ট করুন:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    listOf("ক", "খ", "গ", "ঘ").forEachIndexed { i, l ->
                                        val selected = correctOptIndex == i
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = if (selected) Color(0xFF2E7D32) else Color.LightGray.copy(0.3f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .clickable { correctOptIndex = i }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(l, color = if (selected) Color.White else Color.DarkGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = explanationText,
                                    onValueChange = { explanationText = it },
                                    placeholder = { Text("উত্তর সঠিক হওয়ার ব্যাখ্যা (Explanation)...", fontSize = 10.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                    shape = RoundedCornerShape(6.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (qText.isNotBlank() && optA.isNotBlank() && optB.isNotBlank()) {
                                            createdQuestionsList.add(
                                                ExamQuestion(
                                                    text = qText,
                                                    options = listOf("ক) $optA", "খ) $optB", "গ) $optC", "ঘ) $optD"),
                                                    correctIndex = correctOptIndex,
                                                    explanation = explanationText
                                                )
                                            )
                                            // Reset Question builder fields
                                            qText = ""
                                            optA = ""
                                            optB = ""
                                            optC = ""
                                            optD = ""
                                            correctOptIndex = 0
                                            explanationText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("প্রশ্ন যোগ করুন", fontSize = 10.sp)
                                }
                            }
                        }
                    } else {
                        // Written question paper details
                        OutlinedTextField(
                            value = writtenQuestionText,
                            onValueChange = { writtenQuestionText = it },
                            placeholder = { Text("লিখিত পরীক্ষার নির্দেশনাবলী এবং প্রশ্নাবলী এখানে লিখুন। (যেমন: ৪টি প্রশ্নের উত্তর দিন এবং খাতায় লিখে PDF ফাইল জমা দিন)", fontSize = 10.sp) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (examTitle.isNotBlank()) {
                                viewModel.addExam(
                                    courseId = courseId,
                                    title = examTitle,
                                    type = examType,
                                    questions = createdQuestionsList.toList(),
                                    writtenText = writtenQuestionText,
                                    totalMarks = totalMarksInput.toIntOrNull() ?: 100,
                                    duration = durationInput.toIntOrNull() ?: 30
                                )
                                examTitle = ""
                                writtenQuestionText = ""
                                createdQuestionsList.clear()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = examTitle.isNotBlank()
                    ) {
                        Text("পরীক্ষাটি পাবলিশ করুন (Publish Exam)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submissions Grading queue list
            Text("মূল্যায়নের জন্য জমা পড়া খাতা (Written Homework Queue):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.TextDark)
            Spacer(modifier = Modifier.height(8.dp))

            val pendingSubmissions = submissions.filter { !it.isGraded }
            val gradedSubmissions = submissions.filter { it.isGraded }

            if (submissions.isEmpty()) {
                Text("বর্তমানে কোনো শিক্ষার্থীর জমা দেওয়া খাতা নেই।", fontSize = 11.sp, color = Color.Gray)
            } else {
                Text("পেন্ডিং খাতা (${pendingSubmissions.size})", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = PoraTheme.AccentOrange)
                pendingSubmissions.forEach { sub ->
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
                                Text(sub.studentName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("জমাকৃত ফাইল: ${sub.writtenPdfName}", fontSize = 10.sp, color = PoraTheme.DeepPurple)
                            }
                            Button(
                                onClick = {
                                    activeGradingSubmissionId = sub.id
                                    gradeScore = sub.score.toString()
                                    gradeFeedback = sub.feedback
                                    // Reset correction ticks
                                    correctionTicks.clear()
                                    correctionTicks.addAll(listOf(false, false, false, false))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("খাতা দেখুন ও মার্কস দিন", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("গ্রেড করা সম্পন্ন হয়েছে (${gradedSubmissions.size})", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF2E7D32))
                gradedSubmissions.forEach { sub ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray.copy(0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(sub.studentName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("প্রাপ্ত নম্বর: ${sub.score} | ফিডব্যাক: ${sub.feedback}", fontSize = 10.sp, color = Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("GRADED", color = Color(0xFF2E7D32), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // ================== WRITTEN SCRIPT GRADING CONSOLE ==================
            if (gradingSubmission != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("খাতা মূল্যায়ন ডেক্স: ${gradingSubmission.studentName}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PoraTheme.DeepPurple)
                    OutlinedButton(
                        onClick = { activeGradingSubmissionId = null },
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("ফিরে যান", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // PDF Visual frame mockup
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .border(1.2.dp, Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.Red, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(gradingSubmission.writtenPdfName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.DarkGray)
                        Text("৪ পৃষ্ঠার উত্তরপত্র জমা দেওয়া হয়েছে", fontSize = 9.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Page Ticking correction simulator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (p in 0..3) {
                                val isChecked = correctionTicks.getOrElse(p) { false }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable {
                                        if (p < correctionTicks.size) {
                                            correctionTicks[p] = !isChecked
                                        }
                                    }
                                ) {
                                    Text("Page ${p+1}", fontSize = 9.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Icon(
                                        imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (isChecked) Color(0xFF2E7D32) else Color.Red,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(if (isChecked) "সঠিক" else "ভুল", fontSize = 8.sp, color = if (isChecked) Color(0xFF2E7D32) else Color.Red)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Marks and written feedback form
                OutlinedTextField(
                    value = gradeScore,
                    onValueChange = { gradeScore = it },
                    label = { Text("প্রাপ্ত মার্কস (নম্বর)") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = gradeFeedback,
                    onValueChange = { gradeFeedback = it },
                    label = { Text("শিক্ষকের মূল্যায়ন মন্তব্য (Feedback)...") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Graded Response Button
                var isGeneratingGradedPdf by remember { mutableStateOf(false) }
                LaunchedEffect(isGeneratingGradedPdf) {
                    if (isGeneratingGradedPdf) {
                        kotlinx.coroutines.delay(1200) // Simulation delay
                        viewModel.gradeSubmission(
                            submissionId = gradingSubmission.id,
                            score = gradeScore.toIntOrNull() ?: 80,
                            feedback = gradeFeedback,
                            markedPdfName = gradingSubmission.writtenPdfName.replace(".pdf", "_graded_marks.pdf"),
                            checkedParts = correctionTicks.toList()
                        )
                        isGeneratingGradedPdf = false
                        activeGradingSubmissionId = null
                    }
                }

                if (isGeneratingGradedPdf) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("রিভিউকৃত পিডিএফ ও মার্কিং সিগনেচার তৈরি হচ্ছে...", fontSize = 10.sp, color = PoraTheme.DeepPurple)
                    }
                } else {
                    Button(
                        onClick = {
                            isGeneratingGradedPdf = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("রিভিউড পিডিএফ জেনারেট করে সাবমিট করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(PoraTheme.DeepPurple.copy(0.1f)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = PoraTheme.DeepPurple, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 11.sp, color = Color.Gray)
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
            }
        }
    }
}

// --- COURSE CREATION WIZARD ---
@Composable
fun CourseWizardScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Info, 2: Syllabus, 3: Confirmation

    // Course state
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Development") }
    var price by remember { mutableStateOf("") }
    var discPrice by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var learningPoints by remember { mutableStateOf("") }
    var requisites by remember { mutableStateOf("") }

    // Section/lecture builders
    var newSectionTitle by remember { mutableStateOf("") }
    var newLecTitle by remember { mutableStateOf("") }
    var newLecDuration by remember { mutableStateOf("10:00") }
    var isPreviewLec by remember { mutableStateOf(false) }

    val createdCourseId = remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
            .testTag("course_wizard")
    ) {
        Text("কোর্স ক্রিয়েশন উইজার্ড (Course Creation Wizard)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Text("ধাপ $step অফ ৩ সম্পন্ন করুন", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        when (step) {
            1 -> {
                // Course Basic Info
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("কোর্সের নাম (Course Title)") },
                    modifier = Modifier.fillMaxWidth().testTag("wizard_title_input"),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("সাবটাইটেল (Short Subtitle)") },
                    modifier = Modifier.fillMaxWidth().testTag("wizard_subtitle_input"),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("মূল্য (Original Price in BDT)") },
                    modifier = Modifier.fillMaxWidth().testTag("wizard_price_input"),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = discPrice,
                    onValueChange = { discPrice = it },
                    label = { Text("ডিসকাউন্ট মূল্য (Discounted Price BDT)") },
                    modifier = Modifier.fillMaxWidth().testTag("wizard_disc_input"),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("বিস্তারিত বিবরণ (Description)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("wizard_desc_input"),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        if (title.isNotBlank() && price.isNotBlank() && desc.isNotBlank()) {
                            viewModel.createCourseByInstructor(
                                title = title, subtitle = subtitle, category = category,
                                price = price.toIntOrNull() ?: 0, discountPrice = discPrice.toIntOrNull() ?: 0,
                                description = desc, whatYouWillLearn = "Compose, Room DB", requirements = "PC",
                                onSuccess = { id ->
                                    createdCourseId.value = id
                                    step = 2
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                    modifier = Modifier.fillMaxWidth().testTag("wizard_step1_next")
                ) {
                    Text("পরবর্তী ধাপে যান (Syllabus Builder)")
                }
            }
            2 -> {
                // Curriculum builder
                Text("সিলেবাস কারিকুলাম বিল্ডার", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Add Section form
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f))) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("নতুন সেকশন যোগ করুন:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        OutlinedTextField(
                            value = newSectionTitle,
                            onValueChange = { newSectionTitle = it },
                            placeholder = { Text("সেকশন শিরোনাম (e.g. Chapter 1: Introduction)") },
                            modifier = Modifier.fillMaxWidth().testTag("wizard_section_title_input"),
                            shape = RoundedCornerShape(6.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                if (newSectionTitle.isNotBlank()) {
                                    viewModel.addSectionByInstructor(createdCourseId.value, newSectionTitle, 1)
                                    newSectionTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                            modifier = Modifier.align(Alignment.End).testTag("wizard_add_section_button")
                        ) {
                            Text("সেকশন যোগ")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Add Lecture Form
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f))) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("নতুন ভিডিও লেকচার যোগ করুন (অধ্যায় ১ এর অধীনে):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        OutlinedTextField(
                            value = newLecTitle,
                            onValueChange = { newLecTitle = it },
                            placeholder = { Text("লেকচারের শিরোনাম (e.g. Setting up IDE)") },
                            modifier = Modifier.fillMaxWidth().testTag("wizard_lecture_title_input"),
                            shape = RoundedCornerShape(6.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isPreviewLec, onCheckedChange = { isPreviewLec = it })
                            Text("বিনামূল্যে প্রিভিউ দেখার সুবিধা দিন", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                if (newLecTitle.isNotBlank()) {
                                    // Simulated sectionId = 1 or last inserted
                                    viewModel.addLectureByInstructor(createdCourseId.value, 1, newLecTitle, "", newLecDuration, 1, isPreviewLec)
                                    newLecTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                            modifier = Modifier.align(Alignment.End).testTag("wizard_add_lecture_button")
                        ) {
                            Text("লেকচার যোগ")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { step = 3 },
                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                    modifier = Modifier.fillMaxWidth().testTag("wizard_step2_next")
                ) {
                    Text("চূড়ান্ত পাবলিশ সাবমিশন")
                }
            }
            3 -> {
                // Final submission review status
                Text("কোর্সটি সফলভাবে তৈরি হয়েছে!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2E7D32))
                Spacer(modifier = Modifier.height(12.dp))
                Text("আপনার কোর্সটি রিভিউতে পাঠানোর জন্য রেডি। অনুগ্রহ করে সাবমিট করুন। সুপার এডমিন রিভিউ শেষ করে আপনার কোর্সটি লাইভ পাবলিশ করবেন।")
                
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.publishCourseRequest(createdCourseId.value, {
                            onNavigate(Screen.InstructorDashboard)
                        })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                    modifier = Modifier.fillMaxWidth().testTag("wizard_submit_for_review")
                ) {
                    Text("রিভিউয়ের জন্য জমা দিন (Submit for Review)")
                }
            }
        }
    }
}

// --- PAYOUT REQUEST SCREEN ---
@Composable
fun PayoutRequestScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val payouts by viewModel.instructorPayoutRequests.collectAsState()
    var amount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("bKash") }
    var accNo by remember { mutableStateOf("") }
    
    var errorMsg by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
            .testTag("payout_screen")
    ) {
        Text("উপার্জন উত্তোলন প্যানেল (Payout Drawer)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Text("সর্বনিম্ন উত্তোলনের থ্রেশহোল্ড ১,০০০ টাকা।", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp), fontSize = 13.sp)
        }
        if (successMsg.isNotEmpty()) {
            Text(successMsg, color = Color(0xFF2E7D32), modifier = Modifier.padding(vertical = 8.dp), fontSize = 13.sp)
        }

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("উত্তোলনের পরিমাণ (BDT Amount)") },
            placeholder = { Text("e.g. 5000") },
            modifier = Modifier.fillMaxWidth().testTag("payout_amount_input"),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("উত্তোলনের মাধ্যম:")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("bKash", "Nagad", "Bank").forEach { method ->
                val active = selectedMethod == method
                OutlinedButton(
                    onClick = { selectedMethod = method },
                    border = BorderStroke(1.5.dp, if (active) PoraTheme.DeepPurple else Color.LightGray),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Text(method, color = if (active) PoraTheme.DeepPurple else Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = accNo,
            onValueChange = { accNo = it },
            label = { Text("অ্যাকাউন্ট বা মোবাইল নম্বর") },
            modifier = Modifier.fillMaxWidth().testTag("payout_acc_input"),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                errorMsg = ""
                successMsg = ""
                val amt = amount.toIntOrNull() ?: 0
                if (amt < 1000) {
                    errorMsg = "সর্বনিম্ন উত্তোলনের সীমা ১,০০০ টাকা।"
                } else if (accNo.isBlank()) {
                    errorMsg = "অ্যাকাউন্ট নম্বরটি দিন।"
                } else {
                    viewModel.requestPayout(amt, selectedMethod, accNo, {
                        successMsg = "উত্তোলন আবেদন সফলভাবে পাঠানো হয়েছে। এডমিন রিভিউ করছেন।"
                        amount = ""
                        accNo = ""
                    }, { error ->
                        errorMsg = error
                    })
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
            modifier = Modifier.fillMaxWidth().testTag("submit_payout_button"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("টাকা উত্তোলনের রিকোয়েস্ট পাঠান")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("পূর্ববর্তী উত্তোলনের হিস্ট্রি (Withdrawal Logs)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))

        if (payouts.isEmpty()) {
            Text("কোনো উত্তোলনের ইতিহাস পাওয়া যায়নি।", fontSize = 12.sp, color = Color.Gray)
        } else {
            payouts.forEach { po ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("৳" + po.amount, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("মাধ্যম: " + po.paymentMethod + " ( " + po.accountNumber + " )", fontSize = 12.sp, color = Color.Gray)
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    color = when (po.status) {
                                        "paid" -> Color(0xFFE8F5E9)
                                        "processing" -> Color(0xFFFFF3E0)
                                        else -> Color.LightGray
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                po.status.uppercase(),
                                color = when (po.status) {
                                    "paid" -> Color(0xFF2E7D32)
                                    "processing" -> PoraTheme.AccentOrange
                                    else -> Color.DarkGray
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Q&A INBOX ANSWER ---
@Composable
fun QAInboxScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val courses by viewModel.instructorCourses.collectAsState()
    
    // Expand a simulated list of QAs since instructor manages all courses
    var answerInput by remember { mutableStateOf("") }
    var activeQA by remember { mutableStateOf<QandAEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("qa_inbox")
    ) {
        Text("শিক্ষার্থী প্রশ্ন-উত্তর ইনবক্স (Q&A Inbox)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
        Text("শিক্ষার্থীদের করা প্রশ্নের দ্রুত উত্তর দিয়ে তাদের লার্নিং সহজ করুন।", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        // Simulated QA row
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("শিক্ষার্থী: Rahim Ahmed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("কোর্স: Android App Development with Jetpack Compose", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("প্রশ্ন: ভাইয়া কটলিনের রিমেম্বার (remember) ছাড়া কি স্টেট রিটেইন করা যায়?", fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Divider(color = Color.LightGray)
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { answerInput = it },
                    label = { Text("আপনার উত্তর লিখুন...") },
                    modifier = Modifier.fillMaxWidth().testTag("qa_answer_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (answerInput.isNotBlank()) {
                            // Mock answering action
                            answerInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                    modifier = Modifier.align(Alignment.End).testTag("submit_answer_button")
                ) {
                    Text("উত্তর সাবমিট")
                }
            }
        }
    }
}

@Composable
fun LiveCameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: false
    }
    
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }
    
    if (!hasCameraPermission) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VideocamOff,
                    contentDescription = null,
                    tint = Color.White.copy(0.6f),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "লাইভ ক্লাস শুরু করতে ক্যামেরা পারমিশন প্রয়োজন",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        launcher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF)),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("অনুমতি দিন (Grant Permission)", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview
                        )
                    } catch (e: Exception) {
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview
                            )
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }, ContextCompat.getMainExecutor(ctx))
                
                previewView
            },
            modifier = modifier.fillMaxSize()
        )
    }
}

fun getFileName(context: android.content.Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result ?: "unnamed_file.mp4"
}

