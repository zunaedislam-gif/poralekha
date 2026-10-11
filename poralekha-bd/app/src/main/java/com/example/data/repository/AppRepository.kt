package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val dao: AppDao) {

    // --- Users ---
    suspend fun getUserByEmail(email: String): UserEntity? = dao.getUserByEmail(email)
    suspend fun getUserByPhone(phone: String): UserEntity? = dao.getUserByPhone(phone)
    suspend fun getUserById(id: Int): UserEntity? = dao.getUserById(id)
    fun getAllUsersFlow(): Flow<List<UserEntity>> = dao.getAllUsersFlow()
    suspend fun insertUser(user: UserEntity): Long = dao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = dao.updateUser(user)

    // --- Courses ---
    fun getCoursesByStatusFlow(status: String): Flow<List<CourseEntity>> = dao.getCoursesByStatusFlow(status)
    fun getAllCoursesFlow(): Flow<List<CourseEntity>> = dao.getAllCoursesFlow()
    fun getCoursesByInstructorFlow(instructorId: Int): Flow<List<CourseEntity>> = dao.getCoursesByInstructorFlow(instructorId)
    suspend fun getCourseById(id: Int): CourseEntity? = dao.getCourseById(id)
    suspend fun insertCourse(course: CourseEntity): Long = dao.insertCourse(course)
    suspend fun updateCourse(course: CourseEntity) = dao.updateCourse(course)
    suspend fun deleteCourse(course: CourseEntity) = dao.deleteCourse(course)

    // --- Sections ---
    fun getSectionsForCourseFlow(courseId: Int): Flow<List<SectionEntity>> = dao.getSectionsForCourseFlow(courseId)
    suspend fun insertSection(section: SectionEntity): Long = dao.insertSection(section)
    suspend fun updateSection(section: SectionEntity) = dao.updateSection(section)
    suspend fun deleteSection(section: SectionEntity) = dao.deleteSection(section)

    // --- Lectures ---
    fun getLecturesForSectionFlow(sectionId: Int): Flow<List<LectureEntity>> = dao.getLecturesForSectionFlow(sectionId)
    fun getLecturesForCourseFlow(courseId: Int): Flow<List<LectureEntity>> = dao.getLecturesForCourseFlow(courseId)
    suspend fun insertLecture(lecture: LectureEntity): Long = dao.insertLecture(lecture)
    suspend fun updateLecture(lecture: LectureEntity) = dao.updateLecture(lecture)
    suspend fun deleteLecture(lecture: LectureEntity) = dao.deleteLecture(lecture)

    // --- Enrollments ---
    fun getEnrollmentsForStudentFlow(studentId: Int): Flow<List<EnrollmentEntity>> = dao.getEnrollmentsForStudentFlow(studentId)
    fun getEnrollmentsForCourseFlow(courseId: Int): Flow<List<EnrollmentEntity>> = dao.getEnrollmentsForCourseFlow(courseId)
    suspend fun getEnrollment(studentId: Int, courseId: Int): EnrollmentEntity? = dao.getEnrollment(studentId, courseId)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity): Long = dao.insertEnrollment(enrollment)
    suspend fun updateEnrollment(enrollment: EnrollmentEntity) = dao.updateEnrollment(enrollment)

    // --- Lecture Progress ---
    fun getProgressForCourseFlow(userId: Int, courseId: Int): Flow<List<LectureProgressEntity>> = dao.getProgressForCourseFlow(userId, courseId)
    suspend fun insertProgress(progress: LectureProgressEntity) = dao.insertProgress(progress)
    suspend fun deleteProgress(userId: Int, lectureId: Int) = dao.deleteProgress(userId, lectureId)

    // --- Cart ---
    fun getCartItemsFlow(userId: Int): Flow<List<CourseEntity>> = dao.getCartItemsFlow(userId)
    suspend fun addToCart(cart: CartEntity) = dao.addToCart(cart)
    suspend fun removeFromCart(userId: Int, courseId: Int) = dao.removeFromCart(userId, courseId)
    suspend fun clearCart(userId: Int) = dao.clearCart(userId)

    // --- Wishlist ---
    fun getWishlistItemsFlow(userId: Int): Flow<List<CourseEntity>> = dao.getWishlistItemsFlow(userId)
    suspend fun addToWishlist(wishlist: WishlistEntity) = dao.addToWishlist(wishlist)
    suspend fun removeFromWishlist(userId: Int, courseId: Int) = dao.removeFromWishlist(userId, courseId)
    suspend fun isInWishlist(userId: Int, courseId: Int): Boolean = dao.isInWishlist(userId, courseId)

    // --- Reviews ---
    fun getReviewsForCourseFlow(courseId: Int): Flow<List<ReviewEntity>> = dao.getReviewsForCourseFlow(courseId)
    suspend fun insertReview(review: ReviewEntity): Long = dao.insertReview(review)

    // --- Q&A ---
    fun getQAsForCourseFlow(courseId: Int): Flow<List<QandAEntity>> = dao.getQAsForCourseFlow(courseId)
    fun getQAsForLectureFlow(lectureId: Int): Flow<List<QandAEntity>> = dao.getQAsForLectureFlow(lectureId)
    suspend fun insertQA(qa: QandAEntity): Long = dao.insertQA(qa)
    suspend fun updateQA(qa: QandAEntity) = dao.updateQA(qa)

    // --- Notes ---
    fun getNotesForCourseFlow(userId: Int, courseId: Int): Flow<List<NoteEntity>> = dao.getNotesForCourseFlow(userId, courseId)
    suspend fun insertNote(note: NoteEntity): Long = dao.insertNote(note)
    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    // --- Payout Requests ---
    fun getAllPayoutRequestsFlow(): Flow<List<PayoutRequestEntity>> = dao.getAllPayoutRequestsFlow()
    fun getPayoutRequestsForInstructorFlow(instructorId: Int): Flow<List<PayoutRequestEntity>> = dao.getPayoutRequestsForInstructorFlow(instructorId)
    suspend fun insertPayoutRequest(payout: PayoutRequestEntity): Long = dao.insertPayoutRequest(payout)
    suspend fun updatePayoutRequest(payout: PayoutRequestEntity) = dao.updatePayoutRequest(payout)

    // --- Coupons ---
    suspend fun getCoupon(code: String): CouponEntity? = dao.getCoupon(code)
    fun getAllCouponsFlow(): Flow<List<CouponEntity>> = dao.getAllCouponsFlow()
    suspend fun insertCoupon(coupon: CouponEntity) = dao.insertCoupon(coupon)
    suspend fun updateCoupon(coupon: CouponEntity) = dao.updateCoupon(coupon)
    suspend fun deleteCoupon(coupon: CouponEntity) = dao.deleteCoupon(coupon)

    // --- Notifications ---
    fun getNotificationsForUserFlow(userId: Int): Flow<List<NotificationEntity>> = dao.getNotificationsForUserFlow(userId)
    suspend fun insertNotification(notification: NotificationEntity): Long = dao.insertNotification(notification)
    suspend fun markAllNotificationsAsRead(userId: Int) = dao.markAllNotificationsAsRead(userId)

    // --- Instructor Applications ---
    fun getAllApplicationsFlow(): Flow<List<InstructorApplicationEntity>> = dao.getAllApplicationsFlow()
    suspend fun getApplicationByUserId(userId: Int): InstructorApplicationEntity? = dao.getApplicationByUserId(userId)
    suspend fun insertApplication(app: InstructorApplicationEntity): Long = dao.insertApplication(app)
    suspend fun updateApplication(app: InstructorApplicationEntity) = dao.updateApplication(app)

    // --- Data Seeding ---
    suspend fun seedMockDataIfNeeded() {
        val adminUser = dao.getUserByEmail("admin@poralekha.bd")
        if (adminUser != null) {
            // Seeding already done
            return
        }

        // 1. Seed Users
        // Admin user (id=1)
        val adminId = dao.insertUser(UserEntity(
            email = "admin@poralekha.bd",
            phone = "01700000000",
            password = "admin",
            name = "Super Admin",
            role = "super_admin",
            interests = "Web Development, Design, Business, Marketing, IT & Software"
        ))

        // Instructor user (id=2)
        val instructorId = dao.insertUser(UserEntity(
            email = "instructor@poralekha.bd",
            phone = "01722222222",
            password = "instructor",
            name = "Abir Rahman",
            role = "instructor",
            isDualRole = true,
            isInstructorApproved = true,
            expertArea = "Mobile App Development",
            experience = "8+ Years",
            bio = "অ্যান্ড্রয়েড এবং জেটপ্যাক কম্পোজ স্পেশালিস্ট। বর্তমানে একটি গ্লোবাল সফটওয়্যার ফার্মে কর্মরত আছেন।",
            interests = "Web Development, IT & Software"
        ))

        // Student user (id=3)
        val studentId = dao.insertUser(UserEntity(
            email = "student@poralekha.bd",
            phone = "01711111111",
            password = "student",
            name = "Rahim Ahmed",
            role = "student",
            interests = "Web Development, Design, IT & Software"
        ))

        // 2. Seed Coupons
        dao.insertCoupon(CouponEntity("SAVE50", null, 50, 1000, 0))
        dao.insertCoupon(CouponEntity("PLBD20", null, 20, 500, 0))

        // 3. Seed Courses
        // Course 1
        val c1Id = dao.insertCourse(CourseEntity(
            title = "Android App Development with Jetpack Compose (বাংলা)",
            subtitle = "সম্পূর্ণ প্রজেক্ট-ভিত্তিক শূন্য থেকে অ্যাডভান্সড অ্যান্ড্রয়েড অ্যাপ ডেভেলপমেন্ট কোর্স।",
            category = "Development",
            language = "Bangla",
            level = "Beginner",
            price = 3000,
            discountPrice = 1500,
            instructorId = instructorId.toInt(),
            instructorName = "Abir Rahman",
            thumbnail = "compose",
            previewVideoUrl = "https://assets.mixkit.co/videos/preview/mixkit-online-learning-concept-animated-illustration-33433-large.mp4",
            description = "ডিজাইন এবং পারফরম্যান্সের দিক থেকে জেটপ্যাক কম্পোজ বর্তমানে অ্যান্ড্রয়েড ডেভেলপমেন্টের ভবিষ্যৎ। এই কোর্সে আমরা ব্যবহার করব কটলিন এবং কোড করব একদম শূন্য থেকে। মডার্ন আর্কিটেকচার (MVVM), রুম ডাটাবেস, রেট্রোফিট এপিআই কলিং এবং লাইভ প্রজেক্ট ডেভেলপমেন্ট শিখানো হবে যা আপনাকে প্রফেশনাল জব রেডি করে তুলবে।",
            whatYouWillLearn = "Jetpack Compose, MVVM Architecture, Room Database, Retrofit Networking, State Management, Custom Navigation, Animation & Motion, Build Release App",
            requirements = "যেকোনো একটি প্রোগ্রামিং ভাষার বেসিক ধারণা (যেমন: Java, C++, Python, Javascript), একটি কম্পিউটার (কমপক্ষে 8GB RAM রিকমেন্ডেড), অ্যান্ড্রয়েড স্টুডিও সেটআপ করা",
            status = "published",
            enrollmentCount = 1240,
            rating = 4.8f,
            reviewCount = 5
        ))

        // Course 2
        val c2Id = dao.insertCourse(CourseEntity(
            title = "Complete UI/UX Design Masterclass (বাংলা)",
            subtitle = "Figma দিয়ে প্রফেশনাল মোবাইল এবং ওয়েব UI/UX ডিজাইন শিখুন ও ফ্রিল্যান্সিং ক্যারিয়ার শুরু করুন।",
            category = "Design",
            language = "Bangla",
            level = "Beginner",
            price = 2500,
            discountPrice = 1200,
            instructorId = instructorId.toInt(),
            instructorName = "Abir Rahman",
            thumbnail = "figma",
            previewVideoUrl = "https://assets.mixkit.co/videos/preview/mixkit-woman-working-at-office-with-laptop-and-notepad-39986-large.mp4",
            description = "প্রফেশনাল ডিজিটাল ডিজাইনের সবচেয়ে সেরা টুল ফিগমা ব্যবহার করে কীভাবে নান্দনিক ও কাজের ইউজার ইন্টারফেস তৈরি করবেন এবং কীভাবে ইউজার রিসার্চ পরিচালনা করে প্রফেশনাল পোর্টফোলিও বানাবেন, সবকিছুই এই এক কোর্সে বিস্তারিত পাবেন।",
            whatYouWillLearn = "Figma interface, UI Design rules, Wireframing & Prototyping, Component & Auto Layout, Color Theory & Typography, Real Project Mockups",
            requirements = "ডিজাইনের প্রতি তীব্র আগ্রহ, সাধারণ কম্পিউটার বা ল্যাপটপ এবং সচল ইন্টারনেট সংযোগ, ফিগমা ফ্রি অ্যাকাউন্ট",
            status = "published",
            enrollmentCount = 850,
            rating = 4.6f,
            reviewCount = 3
        ))

        // Course 3
        val c3Id = dao.insertCourse(CourseEntity(
            title = "Digital Marketing Guide for Bangladesh",
            subtitle = "ফেসবুক, গুগল এবং ইউটিউব মার্কেটিং এর সম্পূর্ণ প্র্যাক্টিক্যাল গাইডলাইন।",
            category = "Marketing",
            language = "Bangla",
            level = "All Levels",
            price = 2000,
            discountPrice = 800,
            instructorId = instructorId.toInt(),
            instructorName = "Abir Rahman",
            thumbnail = "marketing",
            previewVideoUrl = "https://assets.mixkit.co/videos/preview/mixkit-hand-holding-a-smartphone-in-vertical-position-48135-large.mp4",
            description = "বর্তমান ডিজিটাল যুগে বিজনেস গ্রোথ অনেকাংশে ডিজিটাল মার্কেটিং এর উপর নির্ভরশীল। এই কোর্সে আমরা দেখব ফেসবুক বুস্টিং এবং ক্যাম্পেইন অপ্টিমাইজেশন, গুগল এডস এবং ইউটিউব এসইও এর বাস্তবমুখী কাজ।",
            whatYouWillLearn = "Facebook Advertising, Google Ads, SEO Strategies, Copywriting Basics, Conversion Rate Optimization, Analytics & Tracking",
            requirements = "যেকোনো বেসিক কম্পিউটার ব্যবহার করতে পারা, ফেসবুক বিজনেস ম্যানেজার বা অ্যাড ম্যানেজার অ্যাকাউন্ট (ফ্রি)",
            status = "published",
            enrollmentCount = 620,
            rating = 4.5f,
            reviewCount = 2
        ))

        // 4. Seed Sections & Lectures for Course 1
        val s1Id = dao.insertSection(SectionEntity(courseId = c1Id.toInt(), title = "কোর্সের ভূমিকা ও সেটআপ", orderIndex = 1))
        val s2Id = dao.insertSection(SectionEntity(courseId = c1Id.toInt(), title = "জেটপ্যাক কম্পোজ বেসিকস", orderIndex = 2))
        val s3Id = dao.insertSection(SectionEntity(courseId = c1Id.toInt(), title = "ডাটাবেস ও এপিআই ইন্টিগ্রেশন", orderIndex = 3))

        dao.insertLecture(LectureEntity(sectionId = s1Id.toInt(), courseId = c1Id.toInt(), title = "কোর্সের সিলেবাস ও পরিচিতি", videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4", duration = "05:12", orderIndex = 1, isPreviewable = true))
        dao.insertLecture(LectureEntity(sectionId = s1Id.toInt(), courseId = c1Id.toInt(), title = "অ্যান্ড্রয়েড স্টুডিও ডাউনলোড ও ইনস্টলেশন", videoUrl = "https://www.w3schools.com/html/movie.mp4", duration = "08:45", orderIndex = 2, isPreviewable = true))
        
        dao.insertLecture(LectureEntity(sectionId = s2Id.toInt(), courseId = c1Id.toInt(), title = "Column, Row এবং Box লেআউট", videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4", duration = "12:30", orderIndex = 3, isPreviewable = false))
        dao.insertLecture(LectureEntity(sectionId = s2Id.toInt(), courseId = c1Id.toInt(), title = "জেটপ্যাক কম্পোজ এ স্টেট ম্যানেজমেন্ট", videoUrl = "https://www.w3schools.com/html/movie.mp4", duration = "15:20", orderIndex = 4, isPreviewable = false))

        dao.insertLecture(LectureEntity(sectionId = s3Id.toInt(), courseId = c1Id.toInt(), title = "রুম ডাটাবেস সেটআপ ও কনফিগারেশন", videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4", duration = "18:40", orderIndex = 5, isPreviewable = false))

        // Sections & Lectures for Course 2
        val s4Id = dao.insertSection(SectionEntity(courseId = c2Id.toInt(), title = "ডিজাইন থিওরি ও ফিগমা ইন্টারফেস", orderIndex = 1))
        dao.insertLecture(LectureEntity(sectionId = s4Id.toInt(), courseId = c2Id.toInt(), title = "ফিগমা পরিচিতি ও বেসিক ড্রয়িং", videoUrl = "https://www.w3schools.com/html/movie.mp4", duration = "07:15", orderIndex = 1, isPreviewable = true))
        dao.insertLecture(LectureEntity(sectionId = s4Id.toInt(), courseId = c2Id.toInt(), title = "কালার এবং টাইপোগ্রাফি রুলস", videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4", duration = "10:30", orderIndex = 2, isPreviewable = false))

        // 5. Seed Reviews for Course 1
        dao.insertReview(ReviewEntity(courseId = c1Id.toInt(), userId = studentId.toInt(), userName = "Rahim Ahmed", rating = 5.0f, comment = "অসাধারণ কোর্স! এত সুন্দর বোঝানোর টেকনিক সত্যি প্রশংসনীয়। বিশেষ করে জেটপ্যাক কম্পোজের স্টেট বোঝার বিষয়গুলো অনেক ক্লিয়ার হয়েছে।"))
        dao.insertReview(ReviewEntity(courseId = c1Id.toInt(), userId = studentId.toInt(), userName = "Sabbir Khan", rating = 4.5f, comment = "অনেক প্র্যাক্টিক্যাল কোর্স। অ্যান্ড্রয়েড ডেভেলপমেন্টের লাইভ টাস্কগুলো করতে অনেক ভালো লেগেছে। মেন্টর অনেক হেল্পফুল।"))
        dao.insertReview(ReviewEntity(courseId = c1Id.toInt(), userId = studentId.toInt(), userName = "Mizanur Rahman", rating = 5.0f, comment = "বাংলাদেশি কনটেক্সটে এর চেয়ে ভালো কটলিন ও কম্পোজ কোর্স হয় না। অনেক নতুন বিষয় শিখেছি। ধন্যবাদ!"))
        dao.insertReview(ReviewEntity(courseId = c1Id.toInt(), userId = studentId.toInt(), userName = "Nusrat Jahan", rating = 4.0f, comment = "খুবই গোছানো সিলেবাস। কিছু কিছু লেকচার আরেকটু সংক্ষেপ করলে ভালো হত, তবে বিস্তারিত জানার জন্য দারুণ।"))
        dao.insertReview(ReviewEntity(courseId = c1Id.toInt(), userId = studentId.toInt(), userName = "Imran Sajjad", rating = 5.0f, comment = "রুম ডাটাবেস এবং রেট্রোফিটের সেকশনটা ছিল অসাধারণ। সরাসরি প্রোডাকশন লেভেলের কাজ শেখা গেছে।"))

        // Reviews for Course 2
        dao.insertReview(ReviewEntity(courseId = c2Id.toInt(), userId = studentId.toInt(), userName = "Sadia Islam", rating = 4.8f, comment = "ফিগমার অটো লেআউট নিয়ে অনেক ভয় ছিল, এই কোর্সটি দেখার পর এখন পুরো ক্লিয়ার! ধন্যবাদ Abir ভাইকে।"))
        dao.insertReview(ReviewEntity(courseId = c2Id.toInt(), userId = studentId.toInt(), userName = "Sakib Ahmed", rating = 4.5f, comment = "খুবই সুন্দর কোর্স। প্র্যাক্টিক্যাল মোবাইল অ্যাপ UI বানানোর সেকশনটি আমার সবচেয়ে ভালো লেগেছে।"))

        // Reviews for Course 3
        dao.insertReview(ReviewEntity(courseId = c3Id.toInt(), userId = studentId.toInt(), userName = "Riyad Khan", rating = 4.5f, comment = "ফেসবুক পিক্সেল সেটআপ এবং ট্র্যাকিং দারুণভাবে শেখানো হয়েছে। রিয়েল বিজনেস গ্রোথে এই গাইড দারুণ কাজ দিচ্ছে।"))

        // 6. Seed QA for Course 1, Lecture 1
        dao.insertQA(QandAEntity(courseId = c1Id.toInt(), lectureId = 1, userId = studentId.toInt(), userName = "Rahim Ahmed", question = "ভাইয়া, ল্যাপটপে 4GB RAM হলে কি অ্যান্ড্রয়েড স্টুডিও স্মুথলি চালানো যাবে?", answer = "একটু স্লো হবে ভাইয়া। মিনিমাম ৪জিবিতে কাজ করা যায় তবে প্রজেক্ট রান করার সময় সময় লাগবে। আপনি পারলে একটি SSD লাগিয়ে নিতে পারেন এবং এমুলেটর ব্যবহার না করে সরাসরি ফিজিক্যাল ফোন দিয়ে রান করবেন তাহলে অনেক ফাস্ট কাজ করা যাবে।", instructorAnswered = true))
    }
}
