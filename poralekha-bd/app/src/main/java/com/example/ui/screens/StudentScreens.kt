package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.*
import com.example.ui.viewmodel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.net.Uri
import android.widget.VideoView
import android.widget.MediaController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

// --- STUDENT HOME SCREEN ---
@Composable
fun StudentHomeScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val courses by viewModel.publishedCourses.collectAsState()
    val enrolled by viewModel.enrolledCourses.collectAsState()
    val allCoursesList by viewModel.allCourses.collectAsState()
    val cart by viewModel.cartItems.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    var activeCat by remember { mutableStateOf("All") }
    val categories = listOf("All", "Development", "Design", "Business", "Marketing", "IT & Software")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
            .testTag("student_home_screen")
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PoraTheme.DeepPurple)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("আসসালামু আলাইকুম,", color = Color.White.copy(0.7f), fontSize = 13.sp)
                        Text(user?.name ?: "শিক্ষার্থী", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Row {
                        IconButton(onClick = { onNavigate(Screen.NotificationCenter) }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                        IconButton(onClick = { onNavigate(Screen.Cart) }) {
                            Box {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                                if (cart.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(PoraTheme.AccentYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(cart.size.toString(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar Trigger
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.StudentSearch) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("যেকোনো কোর্স সার্চ করুন...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        }

        // Horizontal Category Chips
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == activeCat
                FilterChip(
                    selected = isSelected,
                    onClick = { activeCat = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PoraTheme.DeepPurple,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("category_chip_$cat")
                )
            }
        }

        // Promotional Banner / Carousel
        PromotionalBanner()

        // "Continue Learning" section (Courses enrolled partially)
        if (enrolled.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("শেখা চালিয়ে যান (Continue Learning)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
                Spacer(modifier = Modifier.height(10.dp))
                
                enrolled.take(2).forEach { enrollment ->
                    val course = allCoursesList.find { it.id == enrollment.courseId }
                    if (course != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    viewModel.selectCourse(course.id)
                                    onNavigate(Screen.VideoPlayer)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Thumbnail dummy icon
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PoraTheme.DeepPurple.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = PoraTheme.DeepPurple, modifier = Modifier.size(36.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(course.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("ইনস্ট্রাক্টর: ${course.instructorName}", fontSize = 12.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        LinearProgressIndicator(
                                            progress = { enrollment.progress.toFloat() / 100f },
                                            modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                                            color = PoraTheme.DeepPurple,
                                            trackColor = Color.LightGray.copy(alpha = 0.5f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${enrollment.progress}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recommended / Recommended Sections
        val filtered = if (activeCat == "All") courses else courses.filter { it.category == activeCat }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text("আপনার জন্য স্পেশাল রেকমেন্ডেড", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
            Spacer(modifier = Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                EmptyStatePlaceholder(
                    title = "কোনো কোর্স পাওয়া যায়নি",
                    subtitle = "এই ক্যাটাগরিতে বর্তমানে কোনো প্রকাশিত কোর্স নেই। শীঘ্রই নতুন কোর্স যুক্ত হবে!",
                    icon = Icons.Default.SearchOff
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filtered) { course ->
                        CourseGridCard(course = course, onClick = {
                            viewModel.selectCourse(course.id)
                            onNavigate(Screen.CourseDetail)
                        })
                    }
                }
            }
        }

        // Trending / Best Sellers
        if (courses.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("সেরা বিক্রিত কোর্সসমূহ (Best Sellers)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
                Spacer(modifier = Modifier.height(12.dp))

                courses.sortedByDescending { it.enrollmentCount }.take(3).forEach { course ->
                    CourseHorizontalCard(course = course, onClick = {
                        viewModel.selectCourse(course.id)
                        onNavigate(Screen.CourseDetail)
                    })
                }
            }
        }
    }
}

@Composable
fun PromotionalBanner() {
    var promoIndex by remember { mutableStateOf(0) }
    val promos = listOf(
        Pair("৫০% ফ্ল্যাশ ডিসকাউন্ট!", "কুপন কোড ব্যবহার করুন: SAVE50"),
        Pair("রোজার ঈদ স্পেশাল অফার", "সবগুলো আইটি ও লার্নিং কোর্সে ৬০% পর্যন্ত ছাড় চলছে!"),
        Pair("মেন্টরশিপ সাপোর্ট গ্রুপ", "প্রতিটি প্রফেশনাল কোর্সের সাথে লাইভ সাপোর্ট সেশন একদম ফ্রী।")
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            promoIndex = (promoIndex + 1) % promos.size
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Campaign, contentDescription = null, tint = PoraTheme.AccentOrange, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(promos[promoIndex].first, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
                Text(promos[promoIndex].second, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun CourseGridCard(course: CourseEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("course_grid_card_${course.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column {
            // Thumbnail Mock
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(PoraTheme.DeepPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(course.category) {
                        "Design" -> Icons.Default.Brush
                        "Marketing" -> Icons.Default.Campaign
                        "Business" -> Icons.Default.Analytics
                        else -> Icons.Default.Code
                    },
                    contentDescription = null,
                    tint = PoraTheme.DeepPurple,
                    modifier = Modifier.size(45.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(0.65f), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(course.category, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = course.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PoraTheme.TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(38.dp)
                )
                Text(
                    text = "ইনস্ট্রাক্টর: ${course.instructorName}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = PoraTheme.AccentYellow, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(course.rating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
                    Text(" (${course.reviewCount})", fontSize = 10.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (course.discountPrice > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("৳${course.discountPrice}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("৳${course.price}", fontSize = 11.sp, textDecoration = TextDecoration.LineThrough, color = Color.Gray)
                        }
                    } else if (course.price == 0) {
                        Text("ফ্রি (Free)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    } else {
                        Text("৳${course.price}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                    }
                }
            }
        }
    }
}

@Composable
fun CourseHorizontalCard(course: CourseEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
            .testTag("course_row_card_${course.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PoraTheme.DeepPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = PoraTheme.DeepPurple, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("ইনস্ট্রাক্টর: ${course.instructorName}", fontSize = 11.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = PoraTheme.AccentYellow, modifier = Modifier.size(12.dp))
                    Text(" ${course.rating} (${course.reviewCount} রিভিউ)", fontSize = 11.sp, color = Color.DarkGray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (course.discountPrice > 0) {
                        Text("৳${course.discountPrice}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("৳${course.price}", fontSize = 10.sp, textDecoration = TextDecoration.LineThrough, color = Color.Gray)
                    } else if (course.price == 0) {
                        Text("ফ্রি", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    } else {
                        Text("৳${course.price}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                    }
                }
            }
        }
    }
}

// --- SEARCH & BROWSE SCREEN ---
@Composable
fun StudentSearchScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val courses by viewModel.publishedCourses.collectAsState()
    
    // Filters
    var selectedCat by remember { mutableStateOf("All") }
    var selectedLevel by remember { mutableStateOf("All") }
    var selectedPrice by remember { mutableStateOf("All") }

    val filtered = courses.filter { course ->
        (query.isEmpty() || course.title.contains(query, ignoreCase = true) || course.instructorName.contains(query, ignoreCase = true)) &&
        (selectedCat == "All" || course.category == selectedCat) &&
        (selectedLevel == "All" || course.level == selectedLevel) &&
        (selectedPrice == "All" || (selectedPrice == "Free" && course.price == 0) || (selectedPrice == "Paid" && course.price > 0))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("student_search_screen")
    ) {
        // Search Input Box
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("কী শিখতে চান? সার্চ করুন...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("search_text_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Rows
        Text("ফিল্টারসমূহ:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Dropdown Filter
            FilterChip(
                selected = selectedCat != "All",
                onClick = { selectedCat = if (selectedCat == "All") "Development" else "All" },
                label = { Text("ক্যাটাগরি: $selectedCat") }
            )
            FilterChip(
                selected = selectedLevel != "All",
                onClick = { selectedLevel = if (selectedLevel == "All") "Beginner" else "All" },
                label = { Text("লেভেল: $selectedLevel") }
            )
            FilterChip(
                selected = selectedPrice != "All",
                onClick = { selectedPrice = if (selectedPrice == "All") "Paid" else "All" },
                label = { Text("ফি: $selectedPrice") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("সার্চ ফলাফল (${filtered.size}টি কোর্স পাওয়া গেছে)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (filtered.isEmpty()) {
            EmptyStatePlaceholder(
                title = "কোনো কোর্স মিল মেলেনি",
                subtitle = "ভিন্ন কী-ওয়ার্ড বা ভিন্ন ক্যাটাগরি ফিল্টার সিলেক্ট করে পুনরায় চেষ্টা করুন।",
                icon = Icons.Default.SearchOff
            )
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(bottom = 60.dp)) {
                filtered.forEach { course ->
                    CourseHorizontalCard(course = course, onClick = {
                        viewModel.selectCourse(course.id)
                        onNavigate(Screen.CourseDetail)
                    })
                }
            }
        }
    }
}

// --- COURSE DETAILS SCREEN ---
@Composable
fun CourseDetailScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val course by viewModel.selectedCourse.collectAsState()
    val sections by viewModel.selectedCourseSections.collectAsState()
    val lectures by viewModel.selectedCourseLectures.collectAsState()
    val reviews by viewModel.selectedCourseReviews.collectAsState()
    val enrolled by viewModel.enrolledCourses.collectAsState()
    val cart by viewModel.cartItems.collectAsState()

    if (course == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PoraTheme.DeepPurple)
        }
        return
    }

    val isEnrolled = enrolled.any { it.courseId == course!!.id }
    val isInCart = cart.any { it.id == course!!.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
            .testTag("course_detail_screen")
    ) {
        // Mock Video Player Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayCircleOutline, contentDescription = "Preview", tint = Color.White, modifier = Modifier.size(60.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(0.7f), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("কোর্স প্রিভিউ ভিডিও (Preview)", color = Color.White, fontSize = 11.sp)
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // Category Badge
            Box(
                modifier = Modifier
                    .background(PoraTheme.DeepPurple.copy(0.12f), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(course!!.category, color = PoraTheme.DeepPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(course!!.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Text(course!!.subtitle, fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(12.dp))

            // Rating & Students Enrolled
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = PoraTheme.AccentYellow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(course!!.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(" (${course!!.reviewCount} রিভিউ)", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.People, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${course!!.enrollmentCount} শিক্ষার্থী এনরোলড", fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("তৈরি করেছেন: ${course!!.instructorName}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)

            Spacer(modifier = Modifier.height(20.dp))

            // Price / Buy Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                border = BorderStroke(1.dp, Color.LightGray.copy(0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (course!!.discountPrice > 0) {
                            Text("৳${course!!.discountPrice}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("৳${course!!.price}", fontSize = 16.sp, textDecoration = TextDecoration.LineThrough, color = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(PoraTheme.AccentOrange, shape = RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("অফার শেষ হবে শীঘ্রই!", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (course!!.price == 0) {
                            Text("ফ্রি (Free Course)", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        } else {
                            Text("৳${course!!.price}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEnrolled) {
                        Button(
                            onClick = { onNavigate(Screen.VideoPlayer) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("লার্নিং শুরু করুন (Already Enrolled)", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    if (course!!.price == 0) {
                                        viewModel.enrollInFreeCourse(course!!.id, {
                                            onNavigate(Screen.MyLearning)
                                        })
                                    } else {
                                        if (!isInCart) {
                                            viewModel.toggleCart(course!!.id)
                                        }
                                        onNavigate(Screen.Cart)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                modifier = Modifier.weight(1f).height(48.dp).testTag("buy_now_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(if (course!!.price == 0) "এনরোল করুন" else "কোর্সটি কিনুন", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { viewModel.toggleWishlist(course!!.id) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                            ) {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // "What you'll learn" section
            Text("এই কোর্স থেকে কী শিখবেন? (What you'll learn)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
            Spacer(modifier = Modifier.height(10.dp))
            course!!.whatYouWillLearn.split(",").forEach { bullet ->
                if (bullet.isNotBlank()) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp).padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(bullet.trim(), fontSize = 13.sp, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Curriculum Accordion
            Text("কোর্স কারিকুলাম (Curriculum)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
            Spacer(modifier = Modifier.height(10.dp))
            
            if (sections.isEmpty()) {
                Text("বর্তমানে কোনো সিলেবাস তৈরি করা হয়নি।", fontSize = 13.sp, color = Color.Gray)
            } else {
                sections.forEach { section ->
                    var isExpanded by remember { mutableStateOf(true) }
                    val sectionLectures = lectures.filter { it.sectionId == section.id }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isExpanded = !isExpanded }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(section.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                            }

                            if (isExpanded) {
                                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                                sectionLectures.forEach { lecture ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(lecture.title, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(lecture.duration, fontSize = 11.sp, color = Color.Gray)
                                            if (lecture.isPreviewable) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(start = 8.dp)
                                                        .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Preview", color = Color(0xFF2E7D32), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.LightGray, modifier = Modifier.size(14.dp).padding(start = 6.dp))
                                            }
                                        }
                                    }
                                    Divider(color = Color.LightGray.copy(alpha = 0.2f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reviews list
            Text("শিক্ষার্থীদের রিভিউ ও রেটিংস", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
            Spacer(modifier = Modifier.height(12.dp))
            
            if (reviews.isEmpty()) {
                Text("বর্তমানে কোনো রিভিউ দেওয়া হয়নি।", fontSize = 13.sp, color = Color.Gray)
            } else {
                reviews.take(3).forEach { r ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(PoraTheme.DeepPurple.copy(0.15f)), contentAlignment = Alignment.Center) {
                                Text(r.userName.take(1), fontWeight = FontWeight.Bold, color = PoraTheme.DeepPurple)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(r.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(r.rating.toInt()) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = PoraTheme.AccentYellow, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                        Text(r.comment, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 6.dp, start = 42.dp))
                    }
                    Divider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

// --- CART & CHECKOUT SCREEN ---
@Composable
fun StudentCartScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val cart by viewModel.cartItems.collectAsState()
    var couponCode by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf(0) }
    var selectedMethod by remember { mutableStateOf("bKash") }
    var bKashNumber by remember { mutableStateOf("") }
    
    val coroutineScope = rememberCoroutineScope()
    var checkoutSuccess by remember { mutableStateOf(false) }

    if (checkoutSuccess) {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(64.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("পেমেন্ট সফল হয়েছে! 🎉", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Text("কোর্সটি আপনার 'My Learning' প্যানেলে যুক্ত করা হয়েছে।", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                
                Spacer(modifier = Modifier.height(30.dp))
                
                Button(
                    onClick = {
                        checkoutSuccess = false
                        onNavigate(Screen.MyLearning)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple)
                ) {
                    Text("পড়া শুরু করুন (Go to My Learning)")
                }
            }
        }
        return
    }

    if (cart.isEmpty()) {
        EmptyStatePlaceholder(
            title = "আপনার কার্ট খালি!",
            subtitle = "কোনো কোর্স পছন্দ করুন এবং সেটি কার্টে যোগ করুন চেকআউট করার জন্য।",
            icon = Icons.Default.ShoppingCart,
            actionText = "হোম পেজে যান",
            onAction = { onNavigate(Screen.StudentHome) }
        )
        return
    }

    val subtotal = cart.sumOf { if (it.discountPrice > 0) it.discountPrice else it.price }
    val discount = subtotal * discountPercent / 100
    val total = subtotal - discount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
            .testTag("cart_screen")
    ) {
        Text("আপনার কার্ট (${cart.size}টি কোর্স)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
        Spacer(modifier = Modifier.height(12.dp))

        cart.forEach { course ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).background(PoraTheme.DeepPurple.copy(0.12f), shape = RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Book, contentDescription = null, tint = PoraTheme.DeepPurple)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(course.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("৳${if (course.discountPrice > 0) course.discountPrice else course.price}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { viewModel.toggleCart(course.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Coupon input
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = couponCode,
                onValueChange = { couponCode = it },
                placeholder = { Text("কুপন কোড (SAVE50 / PLBD20)") },
                modifier = Modifier.weight(1f).testTag("coupon_input"),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (couponCode.uppercase() == "SAVE50") {
                        discountPercent = 50
                    } else if (couponCode.uppercase() == "PLBD20") {
                        discountPercent = 20
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("apply_coupon_button")
            ) {
                Text("প্রয়োগ")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bill details
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("সাবটোটাল:", color = Color.Gray)
                    Text("৳$subtotal", fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ডিসকাউন্ট ($discountPercent%):", color = Color.Gray)
                    Text("- ৳$discount", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
                Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("সর্বমোট মূল্য:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("৳$total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PoraTheme.DeepPurple)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Payment Methods (bKash, Nagad, Rocket, Card)
        Text("পেমেন্ট মেথড সিলেক্ট করুন:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))

        val paymentMethods = listOf("bKash", "Nagad", "Rocket", "Card")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            paymentMethods.forEach { method ->
                val isSelected = selectedMethod == method
                OutlinedButton(
                    onClick = { selectedMethod = method },
                    border = BorderStroke(1.5.dp, if (isSelected) PoraTheme.DeepPurple else Color.LightGray),
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                ) {
                    Text(method, fontSize = 11.sp, color = if (isSelected) PoraTheme.DeepPurple else Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bKashNumber,
            onValueChange = { bKashNumber = it },
            label = { Text("$selectedMethod অ্যাকাউন্ট নম্বর") },
            placeholder = { Text("e.g. 017XXXXXXXX") },
            modifier = Modifier.fillMaxWidth().testTag("payment_acc_input"),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (bKashNumber.isNotBlank()) {
                    viewModel.checkoutCart(couponCode, selectedMethod, bKashNumber, {
                        checkoutSuccess = true
                    })
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("checkout_button"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("৳$total পেমেন্ট সম্পন্ন করুন", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// --- STUDENT MY LEARNING PANEL ---
@Composable
fun StudentMyLearningScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val enrolled by viewModel.enrolledCourses.collectAsState()
    val allCoursesList by viewModel.allCourses.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("my_learning_screen")
    ) {
        Text("আমার কোর্সসমূহ (My Learning)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PoraTheme.TextDark)
        Spacer(modifier = Modifier.height(16.dp))

        if (enrolled.isEmpty()) {
            EmptyStatePlaceholder(
                title = "কোনো কোর্স নেই!",
                subtitle = "আপনি বর্তমানে কোনো কোর্সে ভর্তি হননি। আজই আপনার প্রথম কোর্সটি কিনুন।",
                icon = Icons.Default.School,
                actionText = "কোর্স ব্রাউজ করুন",
                onAction = { onNavigate(Screen.StudentHome) }
            )
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(bottom = 60.dp)) {
                enrolled.forEach { enrollment ->
                    val course = allCoursesList.find { it.id == enrollment.courseId }
                    if (course != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    viewModel.selectCourse(course.id)
                                    onNavigate(Screen.VideoPlayer)
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PoraTheme.DeepPurple.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PoraTheme.DeepPurple, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(course.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("অগ্রগতি (Progress): ${enrollment.progress}%", fontSize = 12.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        LinearProgressIndicator(
                                            progress = { enrollment.progress.toFloat() / 100f },
                                            modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                                            color = if (enrollment.progress >= 100) Color(0xFF2E7D32) else PoraTheme.DeepPurple,
                                            trackColor = Color.LightGray.copy(alpha = 0.3f)
                                        )
                                    }
                                    if (enrollment.progress >= 100) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Button(
                                            onClick = {
                                                viewModel.selectCourse(course.id)
                                                onNavigate(Screen.Certificate)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.AccentYellow),
                                            modifier = Modifier.height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("সার্টিফিকেট দেখুন", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

// --- VIDEO PLAYER & LEARNING AREA SCREEN ---
@Composable
fun VideoPlayerScreen(
    viewModel: AppViewModel,
    onNavigate: (Screen) -> Unit
) {
    val course by viewModel.selectedCourse.collectAsState()
    val sections by viewModel.selectedCourseSections.collectAsState()
    val lectures by viewModel.selectedCourseLectures.collectAsState()
    val progressList by viewModel.selectedCourseProgress.collectAsState()
    val qas by viewModel.selectedCourseQAs.collectAsState()
    val notes by viewModel.selectedCourseNotes.collectAsState()
    val downloads by viewModel.downloadedLectures.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var activeLecture by remember { mutableStateOf<LectureEntity?>(null) }
    var activeTab by remember { mutableStateOf("curriculum") }

    // Notes Taker state
    var noteInput by remember { mutableStateOf("") }
    
    // Q&A Taker state
    var questionInput by remember { mutableStateOf("") }

    // Mock Video time simulation
    var playTimeSeconds by remember { mutableStateOf(10L) }
    var isPlaying by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPdfName by remember { mutableStateOf("") }

    val studentPdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPdfUri = uri
            // Resolve file name
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
            selectedPdfName = result ?: "assignment_work.pdf"
        }
    }

    val studentStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        studentPdfPickerLauncher.launch("application/pdf")
    }

    // Exam taking states
    var activeExamToTake by remember { mutableStateOf<ExamModel?>(null) }
    val mcqAnswers = remember { mutableStateMapOf<Int, Int>() }
    var examSubmittedSuccessfully by remember { mutableStateOf(false) }
    var examScoreResult by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(lectures) {
        if (lectures.isNotEmpty() && activeLecture == null) {
            activeLecture = lectures.first()
        }
    }

    if (course == null || activeLecture == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("video_player_screen")
    ) {
        // Real Video Player using Android VideoView with key block
        val context = LocalContext.current
        key(activeLecture!!.id) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                val videoUrl = activeLecture!!.videoUrl
                if (videoUrl.isNotBlank()) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val mediaController = MediaController(ctx)
                                mediaController.setAnchorView(this)
                                setMediaController(mediaController)
                                try {
                                    setVideoURI(Uri.parse(videoUrl))
                                    setOnPreparedListener { mp ->
                                        mp.isLooping = false
                                        start()
                                    }
                                    setOnErrorListener { _, _, _ ->
                                        true
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("ভিডিও লিংক পাওয়া যায়নি!", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Active Lecture Info Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = activeLecture!!.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = PoraTheme.TextDark
            )
            if (activeLecture!!.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activeLecture!!.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Action Toolbar: Download offline mock, mark as complete
        val isLecCompleted = progressList.any { it.lectureId == activeLecture!!.id && it.completed }
        val isDownloaded = downloads.contains(activeLecture!!.id)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.2f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.toggleLectureComplete(course!!.id, activeLecture!!.id) },
                colors = ButtonDefaults.buttonColors(containerColor = if (isLecCompleted) Color(0xFF2E7D32) else PoraTheme.DeepPurple),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(if (isLecCompleted) Icons.Default.Check else Icons.Default.RadioButtonUnchecked, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isLecCompleted) " সম্পন্ন (Done)" else " সম্পন্ন মার্ক করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            IconButton(onClick = { viewModel.toggleDownloadLecture(activeLecture!!.id) }) {
                Icon(
                    imageVector = if (isDownloaded) Icons.Default.DownloadDone else Icons.Default.Download,
                    contentDescription = "Offline Download",
                    tint = if (isDownloaded) Color(0xFF2E7D32) else Color.DarkGray
                )
            }
        }

        // Tabs Row: Curriculum, Q&A, Notes, Resources
        val learningTabs = listOf("curriculum", "Q&A", "Notes", "Quiz")
        TabRow(
            selectedTabIndex = learningTabs.indexOf(activeTab),
            contentColor = PoraTheme.DeepPurple
        ) {
            learningTabs.forEach { tab ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    text = { Text(tab.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Tab views content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            when (activeTab) {
                "curriculum" -> {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        sections.forEach { section ->
                            val sectionLecs = lectures.filter { it.sectionId == section.id }
                            Text(section.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PoraTheme.TextDark, modifier = Modifier.padding(vertical = 6.dp))
                            sectionLecs.forEach { lec ->
                                val active = lec.id == activeLecture!!.id
                                val done = progressList.any { it.lectureId == lec.id && it.completed }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { activeLecture = lec }
                                        .background(if (active) PoraTheme.DeepPurple.copy(alpha = 0.1f) else Color.Transparent)
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(if (done) Icons.Default.CheckCircle else Icons.Default.PlayArrow, contentDescription = null, tint = if (done) Color(0xFF2E7D32) else Color.Gray, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(lec.title, fontSize = 13.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text(lec.duration, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Divider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 6.dp))
                        }
                    }
                }
                "Q&A" -> {
                    Column {
                        // Ask query
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = questionInput,
                                onValueChange = { questionInput = it },
                                placeholder = { Text("নতুন প্রশ্ন লিখুন...") },
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (questionInput.isNotBlank()) {
                                        viewModel.addQA(course!!.id, activeLecture!!.id, questionInput)
                                        questionInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple)
                            ) {
                                Text("জিজ্ঞেস")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Query list
                        Text("লেকচার সম্পর্কিত প্রশ্ন-উত্তর:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (qas.isEmpty()) {
                            Text("এই লেকচারে এখনো কোনো প্রশ্ন করা হয়নি।", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                qas.forEach { qa ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(qa.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PoraTheme.TextDark)
                                            Text(qa.question, fontSize = 13.sp, color = Color.DarkGray)
                                            if (qa.answer != null) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Box(modifier = Modifier.background(Color.LightGray.copy(0.2f)).padding(8.dp).fillMaxWidth()) {
                                                    Column {
                                                        Text("ইনস্ট্রাক্টর উত্তর:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = PoraTheme.DeepPurple)
                                                        Text(qa.answer, fontSize = 12.sp, color = Color.Black)
                                                    }
                                                }
                                            } else {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("কোনো উত্তর দেওয়া হয়নি এখনো।", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "Notes" -> {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = noteInput,
                                onValueChange = { noteInput = it },
                                placeholder = { Text("00:${playTimeSeconds} সময়ে নোট যোগ করুন...") },
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (noteInput.isNotBlank()) {
                                        viewModel.addNote(course!!.id, activeLecture!!.id, playTimeSeconds, noteInput)
                                        noteInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple)
                            ) {
                                Text("সংরক্ষণ")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("আপনার সংরক্ষিত নোটসমূহ:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (notes.isEmpty()) {
                            Text("আপনার কোনো নোট নেই। ভিডিও চলাকালীন গুরুত্বপূর্ণ সময়ে নোট নিতে পারেন।", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                notes.forEach { note ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { playTimeSeconds = note.timestamp },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(PoraTheme.AccentOrange, shape = RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("00:${note.timestamp}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(note.noteText, fontSize = 13.sp, color = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }
                }
                "Quiz" -> {
                    val currentCourse = course
                    if (currentCourse != null) {
                        val courseExams = exams.filter { it.courseId == currentCourse.id }
                        if (courseExams.isEmpty()) {
                            // Simple Multiple Choice Mock Quiz (Fallback if no custom exams are created yet)
                            var quizAnswered by remember { mutableStateOf(false) }
                            var selectedOpt by remember { mutableStateOf(-1) }
                            
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                Text("লেকচার সম্পর্কিত কুইজ:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("১. জেটপ্যাক কম্পোজে স্টেট রিমেম্বার (remember) করার কাজ কী?", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(10.dp))

                                val options = listOf(
                                    "ক) রিকম্পোজিশন এড়ানোর জন্য ভ্যালু ধরে রাখা",
                                    "খ) ডাটাবেজে ফাইল সেভ করা",
                                    "গ) স্ক্রিন রানিং বন্ধ করা",
                                    "ঘ) মেমোরি খালি করা"
                                )
                                options.forEachIndexed { idx, opt ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { if (!quizAnswered) selectedOpt = idx }
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(selected = selectedOpt == idx, onClick = { if (!quizAnswered) selectedOpt = idx })
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(opt, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (!quizAnswered) {
                                    Button(
                                        onClick = { if (selectedOpt != -1) quizAnswered = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple)
                                    ) {
                                        Text("উত্তর সাবমিট করুন")
                                    }
                                } else {
                                    val correct = selectedOpt == 0
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = if (correct) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(if (correct) "সঠিক উত্তর! 🎉" else "ভুল উত্তর! সঠিক উত্তর হবে ক", fontWeight = FontWeight.Bold, color = if (correct) Color(0xFF2E7D32) else Color.Red)
                                            Text("রিমেম্বার লজিকটি মূলত স্টেট পরিবর্তনের রিকম্পোজিশন ট্রিগার করলেও ভ্যালু রিটেইন করতে ব্যবহার করা হয়।", fontSize = 11.sp, color = Color.DarkGray)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Custom Course Exams (Mcq or Written Homework/Exams)
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                Text("কোর্স পরীক্ষা ও অ্যাসাইনমেন্ট সমূহ:", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PoraTheme.DeepPurple)
                                Spacer(modifier = Modifier.height(12.dp))

                                val currentExam = activeExamToTake
                                if (currentExam == null) {
                                    for (exam in courseExams) {
                                        val mySub = submissions.firstOrNull { it.examId == exam.id && it.studentName == (currentUser?.name ?: "Student") }
                                        
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            border = BorderStroke(1.dp, Color.LightGray.copy(0.4f))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(exam.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = if (exam.type == "mcq") "টাইপ: MCQ পরীক্ষা" else "টাইপ: লিখিত অ্যাসাইনমেন্ট (PDF)",
                                                            fontSize = 11.sp,
                                                            color = Color.Gray
                                                        )
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (mySub != null) {
                                                                    if (mySub.isGraded) Color(0xFFE8F5E9) else Color(0xFFFFF9C4)
                                                                } else Color(0xFFECEFF1),
                                                                RoundedCornerShape(4.dp)
                                                            )
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            text = if (mySub != null) {
                                                                if (mySub.isGraded) "ফলাফল: ${mySub.score}/${exam.totalMarks}" else "মূল্যায়ন পেন্ডিং"
                                                            } else "অংশ নেননি",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (mySub != null) {
                                                                if (mySub.isGraded) Color(0xFF2E7D32) else Color(0xFFF57F17)
                                                            } else Color.DarkGray
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))
                                                Divider(color = Color.LightGray.copy(0.3f))
                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text("পূর্ণমান: ${exam.totalMarks} | সময়কাল: ${exam.durationMinutes} মিনিট", fontSize = 11.sp, color = Color.DarkGray)

                                                if (mySub != null) {
                                                    if (mySub.feedback.isNotEmpty()) {
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text("শিক্ষকের মন্তব্য: ${mySub.feedback}", fontSize = 11.sp, color = PoraTheme.DeepPurple, fontWeight = FontWeight.Medium)
                                                    }
                                                    if (mySub.markedPdfName.isNotEmpty()) {
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("চিহ্নিত সংশোধিত খাতা: ${mySub.markedPdfName}", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                                        }
                                                    }
                                                } else {
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Button(
                                                        onClick = {
                                                            activeExamToTake = exam
                                                            mcqAnswers.clear()
                                                            examSubmittedSuccessfully = false
                                                            examScoreResult = null
                                                            selectedPdfUri = null
                                                            selectedPdfName = ""
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                                        modifier = Modifier.align(Alignment.End),
                                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                                    ) {
                                                        Text("পরীক্ষা শুরু করুন", fontSize = 11.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, PoraTheme.DeepPurple.copy(0.3f))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(currentExam.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PoraTheme.DeepPurple)
                                                IconButton(onClick = { activeExamToTake = null }) {
                                                    Icon(Icons.Default.Close, contentDescription = "বন্ধ করুন")
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            if (currentExam.type == "mcq") {
                                                var qIdx = 0
                                                for (question in currentExam.mcqQuestions) {
                                                    val questionIndex = qIdx
                                                    Text("${questionIndex + 1}. ${question.text}", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    
                                                    var oIdx = 0
                                                    for (opt in question.options) {
                                                        val currentOIdx = oIdx
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable { mcqAnswers[questionIndex] = currentOIdx }
                                                                .padding(vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            RadioButton(
                                                                selected = mcqAnswers[questionIndex] == currentOIdx,
                                                                onClick = { mcqAnswers[questionIndex] = currentOIdx }
                                                            )
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text(opt, fontSize = 12.sp)
                                                        }
                                                        oIdx++
                                                    }
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    qIdx++
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                Button(
                                                    onClick = {
                                                        var score = 0
                                                        var idx = 0
                                                        for (q in currentExam.mcqQuestions) {
                                                            if (mcqAnswers[idx] == q.correctIndex) {
                                                                score += (currentExam.totalMarks / currentExam.mcqQuestions.size)
                                                            }
                                                            idx++
                                                        }
                                                        val sub = ExamSubmission(
                                                            id = 0,
                                                            examId = currentExam.id,
                                                            studentId = currentUser?.id ?: 99,
                                                            studentName = currentUser?.name ?: "Student",
                                                            score = score,
                                                            feedback = "অটো-গ্রেডেড কুইজ পরীক্ষা সম্পন্ন হয়েছে।",
                                                            writtenPdfName = "",
                                                            markedPdfName = "",
                                                            isGraded = true
                                                        )
                                                        viewModel.addExamSubmission(sub)
                                                        examScoreResult = score
                                                        examSubmittedSuccessfully = true
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                                    enabled = mcqAnswers.size == currentExam.mcqQuestions.size
                                                ) {
                                                    Text("MCQ উত্তরপত্র জমা দিন", fontWeight = FontWeight.Bold)
                                                }

                                                if (examSubmittedSuccessfully) {
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Card(
                                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Column(modifier = Modifier.padding(12.dp)) {
                                                            Text("অভিনন্দন! আপনার উত্তরপত্র সফলভাবে জমা হয়েছে।", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                                            Text("আপনার স্কোর: ${examScoreResult}/${currentExam.totalMarks}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF2E7D32))
                                                            Spacer(modifier = Modifier.height(8.dp))
                                                            Button(
                                                                onClick = { activeExamToTake = null },
                                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                                            ) {
                                                                Text("ফলাফল তালিকা দেখুন", color = Color.White)
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                // Written homework submission
                                                Text("লিখিত অ্যাসাইনমেন্টের নির্দেশনা:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(currentExam.writtenQuestionText, fontSize = 12.sp, color = Color.DarkGray)

                                                Spacer(modifier = Modifier.height(16.dp))
                                                Divider(color = Color.LightGray.copy(0.4f))
                                                Spacer(modifier = Modifier.height(16.dp))

                                                Text("আপনার উত্তরপত্র (PDF ফাইল) আপলোড করুন:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.height(10.dp))

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Button(
                                                        onClick = {
                                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                                                studentStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                                            } else {
                                                                studentStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                                        shape = RoundedCornerShape(6.dp),
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                                    ) {
                                                        Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text("পিডিএফ ফাইল নির্বাচন করুন", fontSize = 11.sp, color = Color.DarkGray)
                                                    }
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                    Text(
                                                        text = if (selectedPdfName.isEmpty()) "কোনো ফাইল সিলেক্ট করা হয়নি" else selectedPdfName,
                                                        fontSize = 11.sp,
                                                        color = if (selectedPdfName.isEmpty()) Color.Red else Color(0xFF2E7D32),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(20.dp))

                                                Button(
                                                    onClick = {
                                                        val sub = ExamSubmission(
                                                            id = 0,
                                                            examId = currentExam.id,
                                                            studentId = currentUser?.id ?: 99,
                                                            studentName = currentUser?.name ?: "Student",
                                                            score = 0,
                                                            feedback = "",
                                                            writtenPdfName = selectedPdfName,
                                                            markedPdfName = "",
                                                            isGraded = false
                                                        )
                                                        viewModel.addExamSubmission(sub)
                                                        examSubmittedSuccessfully = true
                                                    },
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = ButtonDefaults.buttonColors(containerColor = PoraTheme.DeepPurple),
                                                    enabled = selectedPdfName.isNotEmpty() && !examSubmittedSuccessfully
                                                ) {
                                                    Text("অ্যাসাইনমেন্ট সাবমিট করুন", fontWeight = FontWeight.Bold)
                                                }

                                                if (examSubmittedSuccessfully) {
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Card(
                                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Column(modifier = Modifier.padding(12.dp)) {
                                                            Text("অভিনন্দন! আপনার লিখিত অ্যাসাইনমেন্ট সফলভাবে জমা হয়েছে।", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                                            Text("শিক্ষক আপনার ফাইলটি মূল্যায়ন করে সংশোধিত কপিসহ গ্রেড প্রদান করবেন।", fontSize = 11.sp, color = Color.DarkGray)
                                                            Spacer(modifier = Modifier.height(8.dp))
                                                            Button(
                                                                onClick = { activeExamToTake = null },
                                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                                            ) {
                                                                Text("পরীক্ষা তালিকা দেখুন", color = Color.White)
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
                }
            }
        }
    }
}
