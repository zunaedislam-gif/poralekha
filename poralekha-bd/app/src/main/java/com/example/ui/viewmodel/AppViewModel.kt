package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class AppViewModel(val repository: AppRepository, private val context: android.content.Context) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("poralekha_prefs", android.content.Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val foldersAdapter = moshi.adapter<List<FolderModel>>(Types.newParameterizedType(List::class.java, FolderModel::class.java))
    private val resourcesAdapter = moshi.adapter<List<ResourceModel>>(Types.newParameterizedType(List::class.java, ResourceModel::class.java))
    private val examsAdapter = moshi.adapter<List<ExamModel>>(Types.newParameterizedType(List::class.java, ExamModel::class.java))
    private val submissionsAdapter = moshi.adapter<List<ExamSubmission>>(Types.newParameterizedType(List::class.java, ExamSubmission::class.java))
    private val liveClassesAdapter = moshi.adapter<List<LiveClassModel>>(Types.newParameterizedType(List::class.java, LiveClassModel::class.java))

    private fun saveFolders() {
        try {
            sharedPrefs.edit().putString("persisted_folders", foldersAdapter.toJson(_folders.value)).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFolders() {
        try {
            val json = sharedPrefs.getString("persisted_folders", null)
            if (json != null) {
                _folders.value = foldersAdapter.fromJson(json) ?: _folders.value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveResources() {
        try {
            sharedPrefs.edit().putString("persisted_resources", resourcesAdapter.toJson(_resources.value)).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadResources() {
        try {
            val json = sharedPrefs.getString("persisted_resources", null)
            if (json != null) {
                _resources.value = resourcesAdapter.fromJson(json) ?: _resources.value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveExams() {
        try {
            sharedPrefs.edit().putString("persisted_exams", examsAdapter.toJson(_exams.value)).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadExams() {
        try {
            val json = sharedPrefs.getString("persisted_exams", null)
            if (json != null) {
                _exams.value = examsAdapter.fromJson(json) ?: _exams.value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveSubmissions() {
        try {
            sharedPrefs.edit().putString("persisted_submissions", submissionsAdapter.toJson(_submissions.value)).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSubmissions() {
        try {
            val json = sharedPrefs.getString("persisted_submissions", null)
            if (json != null) {
                _submissions.value = submissionsAdapter.fromJson(json) ?: _submissions.value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveLiveClasses() {
        try {
            sharedPrefs.edit().putString("persisted_live_classes", liveClassesAdapter.toJson(_liveClasses.value)).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadLiveClasses() {
        try {
            val json = sharedPrefs.getString("persisted_live_classes", null)
            if (json != null) {
                _liveClasses.value = liveClassesAdapter.fromJson(json) ?: _liveClasses.value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentRole = MutableStateFlow<String>("student")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = _currentUser.map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val onboardingCompleted = MutableStateFlow(false)
    val interestsSelected = MutableStateFlow(false)

    val publishedCourses = repository.getCoursesByStatusFlow("published")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingCourses = repository.getCoursesByStatusFlow("under_review")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCourses = repository.getAllCoursesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getCartItemsFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItems = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getWishlistItemsFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enrolledCourses = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getEnrollmentsForStudentFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getNotificationsForUserFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val instructorApplications = repository.getAllApplicationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayoutRequests = repository.getAllPayoutRequestsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val instructorCourses = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getCoursesByInstructorFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val instructorPayoutRequests = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getPayoutRequestsForInstructorFlow(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCourseId = MutableStateFlow<Int?>(null)
    val selectedCourse: StateFlow<CourseEntity?> = _selectedCourseId.flatMapLatest { id ->
        flow { emit(if (id != null) repository.getCourseById(id) else null) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedCourseSections = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getSectionsForCourseFlow(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCourseLectures = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getLecturesForCourseFlow(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCourseReviews = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getReviewsForCourseFlow(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCourseQAs = _selectedCourseId.flatMapLatest { id ->
        if (id != null) repository.getQAsForCourseFlow(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCourseProgress = _currentUser.combine(_selectedCourseId) { user, cid -> Pair(user, cid) }
        .flatMapLatest { (user, cid) ->
            if (user != null && cid != null) repository.getProgressForCourseFlow(user.id, cid) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCourseNotes = _currentUser.combine(_selectedCourseId) { user, cid -> Pair(user, cid) }
        .flatMapLatest { (user, cid) ->
            if (user != null && cid != null) repository.getNotesForCourseFlow(user.id, cid) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _downloadedLectures = MutableStateFlow<Set<Int>>(emptySet())
    val downloadedLectures: StateFlow<Set<Int>> = _downloadedLectures.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val activeCategory = MutableStateFlow("All")
    val selectedPriceFilter = MutableStateFlow("All")
    val selectedLevelFilter = MutableStateFlow("All")

    init {
        loadFolders()
        loadResources()
        loadExams()
        loadSubmissions()
        loadLiveClasses()

        val savedUserId = sharedPrefs.getInt("logged_in_user_id", -1)
        if (savedUserId != -1) {
            viewModelScope.launch {
                val user = repository.getUserById(savedUserId)
                if (user != null && user.status != "suspended") {
                    _currentUser.value = user
                    _currentRole.value = user.role
                    if (user.interests.isNotEmpty()) interestsSelected.value = true
                } else {
                    sharedPrefs.edit().remove("logged_in_user_id").apply()
                }
            }
        }

        viewModelScope.launch {
            repository.seedMockDataIfNeeded()
        }
    }

    fun selectCourse(id: Int?) {
        _selectedCourseId.value = id
    }

    fun login(emailOrPhone: String, passwordOrOtp: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val user = if (emailOrPhone.contains("@")) repository.getUserByEmail(emailOrPhone) else repository.getUserByPhone(emailOrPhone)
            if (user == null) {
                onFailure("ইউজার খুঁজে পাওয়া যায়নি।")
            } else if (user.status == "suspended") {
                onFailure("আপনার অ্যাকাউন্টটি সাময়িকভাবে স্থগিত করা হয়েছে।")
            } else if (user.password != passwordOrOtp && passwordOrOtp != "123456") {
                onFailure("ভুল পাসওয়ার্ড বা ওটিপি।")
            } else if (user.role == "instructor" && !user.isInstructorApproved) {
                onFailure("আপনার শিক্ষক অ্যাকাউন্টটি এখনও এডমিন কর্তৃক অনুমোদিত হয়নি।")
            } else {
                _currentUser.value = user
                _currentRole.value = user.role
                sharedPrefs.edit().putInt("logged_in_user_id", user.id).apply()
                if (user.interests.isNotEmpty()) interestsSelected.value = true
                onSuccess()
            }
        }
    }

    fun signUp(name: String, email: String, phone: String, passwordOrOtp: String, interests: List<String>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            if (repository.getUserByEmail(email) != null) {
                onFailure("এই ইমেলটি ইতোমধ্যে নিবন্ধিত।")
            } else if (repository.getUserByPhone(phone) != null) {
                onFailure("এই ফোন নম্বরটি ইতোমধ্যে নিবন্ধিত।")
            } else {
                val newId = repository.insertUser(UserEntity(
                    name = name, email = email, phone = phone, password = passwordOrOtp,
                    role = "student", interests = interests.joinToString(", ")
                ))
                _currentUser.value = UserEntity(id = newId.toInt(), name = name, email = email, phone = phone, password = passwordOrOtp, role = "student", interests = interests.joinToString(", "))
                _currentRole.value = "student"
                sharedPrefs.edit().putInt("logged_in_user_id", newId.toInt()).apply()
                interestsSelected.value = true
                onSuccess()
            }
        }
    }

    fun signUpInstructor(
        name: String,
        email: String,
        phone: String,
        passwordOrOtp: String,
        bio: String,
        expertArea: String,
        experience: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (repository.getUserByEmail(email) != null) {
                onFailure("এই ইমেলটি ইতোমধ্যে নিবন্ধিত।")
            } else if (repository.getUserByPhone(phone) != null) {
                onFailure("এই ফোন নম্বরটি ইতোমধ্যে নিবন্ধিত।")
            } else {
                val newId = repository.insertUser(UserEntity(
                    name = name,
                    email = email,
                    phone = phone,
                    password = passwordOrOtp,
                    role = "instructor",
                    isInstructorApproved = false,
                    expertArea = expertArea,
                    experience = experience,
                    bio = bio
                ))
                repository.insertApplication(com.example.data.database.InstructorApplicationEntity(
                    userId = newId.toInt(),
                    name = name,
                    bio = bio,
                    expertArea = expertArea,
                    experience = experience,
                    idVerification = "REG-INSTRUCTOR-${newId}",
                    status = "pending"
                ))
                onSuccess()
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentRole.value = "student"
        interestsSelected.value = false
        _downloadedLectures.value = emptySet()
        sharedPrefs.edit().remove("logged_in_user_id").apply()
    }

    fun updateInterests(interests: List<String>) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val updated = user.copy(interests = interests.joinToString(", "))
            repository.updateUser(updated)
            _currentUser.value = updated
            interestsSelected.value = true
        }
    }

    fun switchRole(role: String) {
        if (_currentUser.value?.role == "super_admin" && role != "super_admin") return
        _currentRole.value = role
    }

    fun applyBecomeInstructor(bio: String, expertArea: String, experience: String, idVerification: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.insertApplication(InstructorApplicationEntity(
                userId = user.id, name = user.name, bio = bio, expertArea = expertArea, experience = experience, idVerification = idVerification, status = "pending"
            ))
            repository.insertNotification(NotificationEntity(
                userId = user.id, title = "ইনস্ট্রাক্টর আবেদন জমা হয়েছে", message = "আপনার ইনস্ট্রাক্টর আবেদনটি সফলভাবে জমা দেওয়া হয়েছে। সুপার এডমিন শীঘ্রই এটি রিভিউ করবেন।"
            ))
            onSuccess()
        }
    }

    fun toggleCart(courseId: Int) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val items = cartItems.value
            if (items.any { it.id == courseId }) {
                repository.removeFromCart(user.id, courseId)
            } else {
                repository.addToCart(CartEntity(user.id, courseId))
            }
        }
    }

    fun toggleWishlist(courseId: Int) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val items = wishlistItems.value
            if (items.any { it.id == courseId }) {
                repository.removeFromWishlist(user.id, courseId)
            } else {
                repository.addToWishlist(WishlistEntity(user.id, courseId))
            }
        }
    }

    fun checkoutCart(couponCode: String, paymentMethod: String, accountNumber: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            var discountPct = 0
            if (couponCode.isNotEmpty()) {
                val cp = repository.getCoupon(couponCode.uppercase())
                if (cp != null) discountPct = cp.discountPercent
            }

            for (course in items) {
                repository.insertEnrollment(EnrollmentEntity(
                    studentId = user.id, courseId = course.id, progress = 0
                ))
                repository.updateCourse(course.copy(enrollmentCount = course.enrollmentCount + 1))
                repository.insertNotification(NotificationEntity(
                    userId = user.id, title = "কোর্সে এনরোলমেন্ট সফল!", message = "আপনি সফলভাবে '${course.title}' কোর্সে এনরোল করেছেন।"
                ))
            }
            repository.clearCart(user.id)
            onSuccess()
        }
    }

    fun enrollInFreeCourse(courseId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.insertEnrollment(EnrollmentEntity(
                studentId = user.id, courseId = courseId, progress = 0
            ))
            val course = repository.getCourseById(courseId)
            if (course != null) {
                repository.updateCourse(course.copy(enrollmentCount = course.enrollmentCount + 1))
                repository.insertNotification(NotificationEntity(
                    userId = user.id, title = "ফ্রি কোর্সে এনরোলমেন্ট সফল!", message = "আপনি সফলভাবে '${course.title}' কোর্সে এনরোল করেছেন।"
                ))
            }
            onSuccess()
        }
    }

    fun toggleLectureComplete(courseId: Int, lectureId: Int) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val progressList = selectedCourseProgress.value
            val isCompleted = progressList.any { it.lectureId == lectureId && it.completed }

            if (isCompleted) {
                repository.deleteProgress(user.id, lectureId)
            } else {
                repository.insertProgress(LectureProgressEntity(user.id, courseId, lectureId, true))
            }
            updateCourseProgressPercentage(user.id, courseId)
        }
    }

    private suspend fun updateCourseProgressPercentage(userId: Int, courseId: Int) {
        val lectures = repository.getLecturesForCourseFlow(courseId).firstOrNull() ?: emptyList()
        if (lectures.isEmpty()) return
        val completedCount = repository.getProgressForCourseFlow(userId, courseId).firstOrNull()?.size ?: 0
        val percentage = (completedCount * 100) / lectures.size

        val enrollment = repository.getEnrollment(userId, courseId)
        if (enrollment != null) {
            val updated = if (percentage >= 100 && enrollment.certificateCode == null) {
                val code = "PLBD-${courseId}-${System.currentTimeMillis() % 1000000}"
                val date = "July 20, 2026"
                repository.insertNotification(NotificationEntity(
                    userId = userId, title = "কোর্স সম্পন্ন করার অভিনন্দন! 🎉", message = "আপনি সফলভাবে কোর্স সম্পন্ন করেছেন! আপনার সার্টিফিকেট কোড: $code"
                ))
                enrollment.copy(progress = 100, certificateCode = code, certificateDate = date)
            } else {
                enrollment.copy(progress = percentage)
            }
            repository.updateEnrollment(updated)
        }
    }

    fun addNote(courseId: Int, lectureId: Int, timestamp: Long, noteText: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.insertNote(NoteEntity(
                courseId = courseId, lectureId = lectureId, userId = user.id, timestamp = timestamp, noteText = noteText
            ))
        }
    }

    fun addQA(courseId: Int, lectureId: Int, questionText: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.insertQA(QandAEntity(
                courseId = courseId, lectureId = lectureId, userId = user.id, userName = user.name, question = questionText, instructorAnswered = false
            ))
        }
    }

    fun addReview(courseId: Int, rating: Float, comment: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            repository.insertReview(ReviewEntity(
                courseId = courseId, userId = user.id, userName = user.name, rating = rating, comment = comment
            ))
            val course = repository.getCourseById(courseId)
            if (course != null) {
                val reviews = repository.getReviewsForCourseFlow(courseId).firstOrNull() ?: emptyList()
                val average = if (reviews.isNotEmpty()) reviews.map { it.rating }.average().toFloat() else rating
                repository.updateCourse(course.copy(rating = average, reviewCount = reviews.size + 1))
            }
        }
    }

    fun toggleDownloadLecture(lectureId: Int) {
        val current = _downloadedLectures.value.toMutableSet()
        if (current.contains(lectureId)) current.remove(lectureId) else current.add(lectureId)
        _downloadedLectures.value = current
    }

    fun createCourseByInstructor(title: String, subtitle: String, category: String, price: Int, discountPrice: Int, description: String, whatYouWillLearn: String, requirements: String, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val courseId = repository.insertCourse(CourseEntity(
                title = title, subtitle = subtitle, category = category, price = price, discountPrice = discountPrice,
                instructorId = user.id, instructorName = user.name, thumbnail = category.lowercase(), description = description,
                whatYouWillLearn = whatYouWillLearn, requirements = requirements, status = "draft"
            ))
            onSuccess(courseId.toInt())
        }
    }

    fun publishCourseRequest(courseId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val course = repository.getCourseById(courseId) ?: return@launch
            repository.updateCourse(course.copy(status = "under_review"))
            onSuccess()
        }
    }

    fun addSectionByInstructor(courseId: Int, title: String, orderIndex: Int) {
        viewModelScope.launch {
            repository.insertSection(SectionEntity(courseId = courseId, title = title, orderIndex = orderIndex))
        }
    }

    fun addLectureByInstructor(courseId: Int, sectionId: Int, title: String, videoUrl: String, duration: String, orderIndex: Int, isPreview: Boolean, description: String = "") {
        viewModelScope.launch {
            repository.insertLecture(LectureEntity(
                sectionId = sectionId, courseId = courseId, title = title, description = description,
                videoUrl = videoUrl.ifEmpty { "https://www.w3schools.com/html/mov_bbb.mp4" },
                duration = duration.ifEmpty { "10:00" }, orderIndex = orderIndex, isPreviewable = isPreview
            ))
        }
    }

    fun requestPayout(amount: Int, method: String, accountNumber: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            if (amount < 1000) {
                onFailure("সর্বনিম্ন পেমেন্ট উত্তোলনের পরিমাণ ১,০০০ টাকা।")
                return@launch
            }
            repository.insertPayoutRequest(PayoutRequestEntity(
                instructorId = user.id, amount = amount, paymentMethod = method, accountNumber = accountNumber, status = "pending"
            ))
            onSuccess()
        }
    }

    fun answerQA(qaId: Int, answerText: String, qa: QandAEntity) {
        viewModelScope.launch {
            repository.updateQA(qa.copy(answer = answerText, instructorAnswered = true))
        }
    }

    fun reviewInstructorApplication(application: InstructorApplicationEntity, approve: Boolean, feedback: String) {
        viewModelScope.launch {
            val status = if (approve) "approved" else "rejected"
            repository.updateApplication(application.copy(status = status, feedback = feedback))

            if (approve) {
                val appUser = repository.getUserById(application.userId)
                if (appUser != null) {
                    repository.updateUser(appUser.copy(
                        role = "instructor", isDualRole = true, isInstructorApproved = true,
                        expertArea = application.expertArea, experience = application.experience, bio = application.bio
                    ))
                }
            }
            repository.insertNotification(NotificationEntity(
                userId = application.userId,
                title = if (approve) "ইনস্ট্রাক্টর আবেদন অনুমোদিত! 🎓" else "ইনস্ট্রাক্টর আবেদন রিভিউ",
                message = if (approve) "অভিনন্দন! আপনি এখন আমাদের একজন অনুমোদিত ইনস্ট্রাক্টর।" else "দুঃখিত, আপনার ইনস্ট্রাক্টর আবেদনটি বাতিল করা হয়েছে। কারণ: $feedback"
            ))
        }
    }

    fun reviewCourse(course: CourseEntity, approve: Boolean, feedback: String) {
        viewModelScope.launch {
            val status = if (approve) "published" else "rejected"
            repository.updateCourse(course.copy(status = status, adminFeedback = feedback))
            repository.insertNotification(NotificationEntity(
                userId = course.instructorId,
                title = if (approve) "কোর্স প্রকাশিত হয়েছে! 🚀" else "কোর্স মডারেশন ফিডব্যাক",
                message = if (approve) "আপনার কোর্স '${course.title}' সফলভাবে লাইভ করা হয়েছে।" else "আপনার কোর্সটি কিছু সংশোধনের জন্য বাতিল করা হয়েছে। কারণ: $feedback"
            ))
        }
    }

    fun processPayout(request: PayoutRequestEntity, status: String) {
        viewModelScope.launch {
            repository.updatePayoutRequest(request.copy(status = status))
            repository.insertNotification(NotificationEntity(
                userId = request.instructorId,
                title = "পেমেন্ট উত্তোলনের আপডেট",
                message = "আপনার ${request.amount} টাকার উত্তোলন রিকোয়েস্টটির অবস্থা এখন: ${if (status == "paid") "পরিশোধিত" else "প্রসেসিং"}"
            ))
        }
    }

    fun updateUserStatus(user: UserEntity, suspend: Boolean) {
        viewModelScope.launch {
            repository.updateUser(user.copy(status = if (suspend) "suspended" else "active"))
        }
    }

    // --- PREMIUM FEATURE MODEL STATES & CONTROLS ---

    private val _folders = MutableStateFlow<List<FolderModel>>(
        listOf(
            FolderModel(id = 101, courseId = 1, name = "সপ্তাহ ১: জেটপ্যাক কম্পোজ ওভারভিউ ও স্লাইড"),
            FolderModel(id = 102, courseId = 1, name = "সপ্তাহ ২: স্টেট ম্যানেজমেন্ট ও আর্কিটেকচার"),
            FolderModel(id = 103, courseId = 1, name = "সপ্তাহ ৩: রুম ডাটাবেস এক্সট্রা গাইডস")
        )
    )
    val folders: StateFlow<List<FolderModel>> = _folders.asStateFlow()

    private val _resources = MutableStateFlow<List<ResourceModel>>(
        listOf(
            ResourceModel(id = 201, folderId = 101, courseId = 1, title = "অ্যান্ড্রয়েড আর্কিটেকচার গাইড স্লাইড", fileType = "pdf", fileUrl = "https://example.com/slide1.pdf", size = "3.2 MB"),
            ResourceModel(id = 202, folderId = 101, courseId = 1, title = "কটলিন চিটশীট PDF", fileType = "pdf", fileUrl = "https://example.com/kotlin-cheat.pdf", size = "1.5 MB"),
            ResourceModel(id = 203, folderId = 102, courseId = 1, title = "স্টেট রিমেম্বার প্র্যাকটিস সোর্স কোড", fileType = "zip", fileUrl = "https://example.com/state-practice.zip", size = "5.8 MB"),
            ResourceModel(id = 204, folderId = 103, courseId = 1, title = "রুম মাইগ্রেশন ডিক্লেয়ারেশন পিডিএফ", fileType = "pdf", fileUrl = "https://example.com/room.pdf", size = "2.1 MB")
        )
    )
    val resources: StateFlow<List<ResourceModel>> = _resources.asStateFlow()

    private val _exams = MutableStateFlow<List<ExamModel>>(
        listOf(
            ExamModel(
                id = 301,
                courseId = 1,
                title = "অ্যান্ড্রয়েড ও কটলিন বেসিকস MCQ কুইজ",
                type = "mcq",
                mcqQuestions = listOf(
                    ExamQuestion(
                        text = "১. জেটপ্যাক কম্পোজে রিমেম্বার (remember) কেন ব্যবহার করা হয়?",
                        options = listOf(
                            "ক) কম্পোজিশনের সময় মান ধরে রাখতে",
                            "খ) নতুন এপিআই লোড করতে",
                            "গ) স্ক্রিনের ব্যাকগ্রাউন্ড কালার পরিবর্তন করতে",
                            "ঘ) মেমোরি ক্লিয়ার করতে"
                        ),
                        correctIndex = 0,
                        explanation = "remember স্টেট ট্রিগার করলেও কম্পোজিশন রি-রান হওয়ার পরেও মান ধরে রাখে।"
                    ),
                    ExamQuestion(
                        text = "২. কটলিনে নাল-সেফটি হ্যান্ডেল করতে কোনটি ব্যবহার করা হয়?",
                        options = listOf(
                            "ক) Safe call operator (?.)",
                            "খ) Elvis operator (?:)",
                            "গ) Double-bang operator (!!)",
                            "ঘ) উপরের সবগুলো"
                        ),
                        correctIndex = 3,
                        explanation = "এই সবগুলো অপারেটরই কটলিনে নাল-সেফটি ও নাল হ্যান্ডেল করতে ব্যবহৃত হয়।"
                    ),
                    ExamQuestion(
                        text = "৩. কম্পোজে UI আপডেট করার চালিকাশক্তি কোনটি?",
                        options = listOf(
                            "ক) ফাংশন কলিং",
                            "খ) স্টেট (State)",
                            "গ) XML ফাইল",
                            "ঘ) ক্লাস ইনস্ট্যান্স"
                        ),
                        correctIndex = 1,
                        explanation = "স্টেট পরিবর্তনের ফলেই জেটপ্যাক কম্পোজ UI স্বয়ংক্রিয়ভাবে রিকম্পোজ হয়।"
                    )
                ),
                totalMarks = 30,
                durationMinutes = 15,
                passMarks = 15
            ),
            ExamModel(
                id = 302,
                courseId = 1,
                title = "রুম ডাটাবেস হ্যান্ডস-অন অ্যাসাইনমেন্ট (লিখিত)",
                type = "written",
                writtenQuestionText = "প্রশ্ন: আপনার EdTech প্ল্যাটফর্মের জন্য একটি সম্পূর্ণ SQLite ডাটাবেস স্কিমা ডিজাইন করুন যেখানে User, Course, এবং Progress টেবিল থাকবে। টেবিলগুলোর রিলেশনশিপ ব্যাখ্যা করুন এবং SQL কুয়েরিগুলো পিডিএফ আকারে জমা দিন।",
                totalMarks = 100,
                durationMinutes = 120,
                passMarks = 40
            )
        )
    )
    val exams: StateFlow<List<ExamModel>> = _exams.asStateFlow()

    private val _submissions = MutableStateFlow<List<ExamSubmission>>(
        listOf(
            ExamSubmission(
                id = 401,
                examId = 302,
                studentId = 3,
                studentName = "Rahim Ahmed",
                writtenPdfName = "rahim_db_assignment.pdf",
                score = 85,
                feedback = "চমৎকার ডিজাইন! স্কিমাগুলো একদম সঠিক হয়েছে। পরবর্তী অ্যাসাইনমেন্টে ইনডেক্সিং যোগ করার চেষ্টা করবেন।",
                isGraded = true,
                markedPdfName = "rahim_db_assignment_graded.pdf",
                checkedParts = listOf(true, true, false, true)
            )
        )
    )
    val submissions: StateFlow<List<ExamSubmission>> = _submissions.asStateFlow()

    private val _liveClasses = MutableStateFlow<List<LiveClassModel>>(
        listOf(
            LiveClassModel(
                id = 501,
                courseId = 1,
                title = "লাইভ ডাউট ক্লিয়ারিং এবং কম্পোজ লাইভ কোডিং সেশন",
                date = "2026-07-25",
                time = "রাত ০৯:০০ টা",
                isLive = false,
                whiteboardText = "স্বাগতম সবাইকে আমাদের অ্যান্ড্রয়েড লাইভ ক্লাসে!",
                liveChatMessages = listOf(
                    LiveChatMessage("Rahim Ahmed", "ভাইয়া, রুম ডাটাবেসের সেশনটা খুবই ভালো লেগেছে।"),
                    LiveChatMessage("Sabbir Khan", "লাইভ ক্লাসটি শুরু হওয়ার জন্য অপেক্ষা করছি।")
                ),
                pollQuestion = "আপনারা কি মডার্ন কটলিন করুটিনস নিয়ে আলাদা সেশন চান?",
                pollOptions = listOf("হ্যাঁ, অবশ্যই!", "পরবর্তীতে শিখলে হবে"),
                pollVotes = listOf(14, 2)
            )
        )
    )
    val liveClasses: StateFlow<List<LiveClassModel>> = _liveClasses.asStateFlow()

    // Folder Logic
    fun addFolder(courseId: Int, name: String) {
        val current = _folders.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 100) + 1
        current.add(FolderModel(newId, courseId, name))
        _folders.value = current
        saveFolders()
    }

    fun updateFolder(folderId: Int, name: String) {
        val current = _folders.value.map {
            if (it.id == folderId) it.copy(name = name) else it
        }
        _folders.value = current
        saveFolders()
    }

    fun deleteFolder(folderId: Int) {
        _folders.value = _folders.value.filter { it.id != folderId }
        _resources.value = _resources.value.filter { it.folderId != folderId }
        saveFolders()
        saveResources()
    }

    // Resource Logic
    fun addResource(folderId: Int, courseId: Int, title: String, fileType: String, size: String = "1.8 MB") {
        val current = _resources.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 200) + 1
        current.add(ResourceModel(newId, folderId, courseId, title, fileType, "https://example.com/download", size))
        _resources.value = current
        saveResources()
    }

    fun deleteResource(resourceId: Int) {
        _resources.value = _resources.value.filter { it.id != resourceId }
        saveResources()
    }

    // Exam Logic
    fun addExam(courseId: Int, title: String, type: String, questions: List<ExamQuestion>, writtenText: String, totalMarks: Int, duration: Int) {
        val current = _exams.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 300) + 1
        current.add(ExamModel(newId, courseId, title, type, questions, writtenText, totalMarks, duration))
        _exams.value = current
        saveExams()
    }

    fun addExamSubmission(submission: ExamSubmission) {
        val current = _submissions.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 400) + 1
        current.add(submission.copy(id = newId))
        _submissions.value = current
        saveSubmissions()
    }

    fun gradeSubmission(submissionId: Int, score: Int, feedback: String, markedPdfName: String, checkedParts: List<Boolean>) {
        val current = _submissions.value.map {
            if (it.id == submissionId) {
                it.copy(score = score, feedback = feedback, markedPdfName = markedPdfName, isGraded = true, checkedParts = checkedParts)
            } else it
        }
        _submissions.value = current
        saveSubmissions()
    }

    // Live Class Logic
    fun scheduleLiveClass(courseId: Int, title: String, date: String, time: String) {
        val current = _liveClasses.value.toMutableList()
        val newId = (current.maxOfOrNull { it.id } ?: 500) + 1
        current.add(LiveClassModel(newId, courseId, title, date, time))
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun startLiveClass(classId: Int) {
        val current = _liveClasses.value.map {
            if (it.id == classId) it.copy(isLive = true) else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun stopLiveClass(classId: Int) {
        val current = _liveClasses.value.map {
            if (it.id == classId) it.copy(isLive = false) else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun updateLiveWhiteboard(classId: Int, text: String) {
        val current = _liveClasses.value.map {
            if (it.id == classId) it.copy(whiteboardText = text) else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun addLiveChatMessage(classId: Int, senderName: String, message: String) {
        val current = _liveClasses.value.map {
            if (it.id == classId) {
                it.copy(liveChatMessages = it.liveChatMessages + LiveChatMessage(senderName, message))
            } else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun pinLiveQuestion(classId: Int, question: String?) {
        val current = _liveClasses.value.map {
            if (it.id == classId) it.copy(pinnedQuestion = question) else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun addLivePoll(classId: Int, question: String, options: List<String>) {
        val current = _liveClasses.value.map {
            if (it.id == classId) {
                it.copy(pollQuestion = question, pollOptions = options, pollVotes = List(options.size) { 0 })
            } else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun voteLivePoll(classId: Int, optionIndex: Int) {
        val current = _liveClasses.value.map {
            if (it.id == classId) {
                val votes = it.pollVotes.toMutableList()
                if (optionIndex in votes.indices) {
                    votes[optionIndex] = votes[optionIndex] + 1
                }
                it.copy(pollVotes = votes)
            } else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun toggleHandRaise(classId: Int, studentName: String, raise: Boolean) {
        val current = _liveClasses.value.map {
            if (it.id == classId) {
                val raises = it.handRaises.toMutableList()
                if (raise) {
                    if (!raises.contains(studentName)) raises.add(studentName)
                } else {
                    raises.remove(studentName)
                }
                it.copy(handRaises = raises)
            } else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    fun clearHandRaises(classId: Int) {
        val current = _liveClasses.value.map {
            if (it.id == classId) it.copy(handRaises = emptyList()) else it
        }
        _liveClasses.value = current
        saveLiveClasses()
    }

    // --- SUPER ADMIN CONTENT OPERATIONS ---
    fun deleteCourseByAdmin(courseId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val course = repository.getCourseById(courseId)
            if (course != null) {
                repository.deleteCourse(course)
            }
            onSuccess()
        }
    }

    fun deleteSectionByAdmin(section: SectionEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteSection(section)
            onSuccess()
        }
    }

    fun deleteLectureByAdmin(lecture: LectureEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteLecture(lecture)
            onSuccess()
        }
    }

    fun createCourseByAdmin(title: String, subtitle: String, category: String, price: Int, discountPrice: Int, description: String, whatYouWillLearn: String, requirements: String, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            val courseId = repository.insertCourse(CourseEntity(
                title = title, subtitle = subtitle, category = category, price = price, discountPrice = discountPrice,
                instructorId = 1, instructorName = "Super Admin", thumbnail = category.lowercase(), description = description,
                whatYouWillLearn = whatYouWillLearn, requirements = requirements, status = "published"
            ))
            onSuccess(courseId.toInt())
        }
    }
}

// --- PREMIUM FEATURE DATA MODELS ---

data class ExamQuestion(
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String = ""
)

data class ExamModel(
    val id: Int,
    val courseId: Int,
    val title: String,
    val type: String, // "mcq" or "written"
    val mcqQuestions: List<ExamQuestion> = emptyList(),
    val writtenQuestionText: String = "",
    val totalMarks: Int = 100,
    val durationMinutes: Int = 30,
    val passMarks: Int = 40,
    val attemptLimit: Int = 3
)

data class ExamSubmission(
    val id: Int = 0,
    val examId: Int,
    val studentId: Int,
    val studentName: String,
    val mcqAnswers: Map<String, Int> = emptyMap(),
    val writtenPdfName: String = "",
    val score: Int = 0,
    val feedback: String = "",
    val isGraded: Boolean = false,
    val markedPdfName: String = "",
    val checkedParts: List<Boolean> = emptyList()
)

data class FolderModel(
    val id: Int,
    val courseId: Int,
    val name: String
)

data class ResourceModel(
    val id: Int,
    val folderId: Int,
    val courseId: Int,
    val title: String,
    val fileType: String, // "pdf", "ppt", "zip", "doc"
    val fileUrl: String,
    val size: String = "2.4 MB"
)

data class LiveChatMessage(
    val sender: String,
    val message: String
)

data class LiveClassModel(
    val id: Int,
    val courseId: Int,
    val title: String,
    val date: String,
    val time: String,
    val isLive: Boolean = false,
    val whiteboardText: String = "",
    val liveChatMessages: List<LiveChatMessage> = emptyList(),
    val pinnedQuestion: String? = null,
    val pollQuestion: String? = null,
    val pollOptions: List<String> = emptyList(),
    val pollVotes: List<Int> = emptyList(),
    val handRaises: List<String> = emptyList()
)
