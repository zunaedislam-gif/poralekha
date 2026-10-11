package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ------------------ ENTITIES ------------------

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val phone: String,
    val password: String,
    val name: String,
    val profileImage: String = "",
    val role: String = "student", // "student", "instructor", "super_admin"
    val isDualRole: Boolean = false,
    val isInstructorApproved: Boolean = false,
    val interests: String = "", // Comma-separated interests
    val expertArea: String = "",
    val experience: String = "",
    val bio: String = "",
    val status: String = "active" // "active", "suspended"
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subtitle: String,
    val category: String,
    val language: String = "Bangla",
    val level: String = "Beginner", // "Beginner", "Intermediate", "Advanced", "All Levels"
    val price: Int = 0,
    val discountPrice: Int = 0,
    val instructorId: Int,
    val instructorName: String,
    val thumbnail: String = "",
    val previewVideoUrl: String = "",
    val description: String,
    val whatYouWillLearn: String = "", // Comma-separated or bulleted
    val requirements: String = "", // Comma-separated
    val status: String = "draft", // "draft", "under_review", "published", "rejected"
    val adminFeedback: String = "",
    val enrollmentCount: Int = 0,
    val rating: Float = 4.5f,
    val reviewCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sections")
data class SectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val title: String,
    val orderIndex: Int
)

@Entity(tableName = "lectures")
data class LectureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sectionId: Int,
    val courseId: Int,
    val title: String,
    val description: String = "",
    val videoUrl: String = "",
    val duration: String = "10:00",
    val orderIndex: Int,
    val isPreviewable: Boolean = false,
    val resources: String = "" // Comma-separated resource URLs or names
)

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val courseId: Int,
    val progress: Int = 0, // Percentage 0-100
    val enrolledAt: Long = System.currentTimeMillis(),
    val certificateCode: String? = null,
    val certificateDate: String? = null
)

@Entity(tableName = "lecture_progress", primaryKeys = ["userId", "lectureId"])
data class LectureProgressEntity(
    val userId: Int,
    val courseId: Int,
    val lectureId: Int,
    val completed: Boolean = true
)

@Entity(tableName = "cart", primaryKeys = ["userId", "courseId"])
data class CartEntity(
    val userId: Int,
    val courseId: Int
)

@Entity(tableName = "wishlist", primaryKeys = ["userId", "courseId"])
data class WishlistEntity(
    val userId: Int,
    val courseId: Int
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val userId: Int,
    val userName: String,
    val rating: Float,
    val comment: String,
    val helpfulCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "q_and_a")
data class QandAEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val lectureId: Int,
    val userId: Int,
    val userName: String,
    val question: String,
    val answer: String? = null,
    val instructorAnswered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseId: Int,
    val lectureId: Int,
    val userId: Int,
    val timestamp: Long, // video playback time in seconds
    val noteText: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payout_requests")
data class PayoutRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val instructorId: Int,
    val amount: Int,
    val paymentMethod: String, // "bKash", "Nagad", "Bank"
    val accountNumber: String,
    val status: String = "pending", // "pending", "processing", "paid"
    val requestedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey val code: String,
    val courseId: Int? = null, // null means global coupon
    val discountPercent: Int,
    val maxUses: Int = 100,
    val uses: Int = 0,
    val expiryDate: String = "2026-12-31"
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val message: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "instructor_applications")
data class InstructorApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val name: String,
    val bio: String,
    val expertArea: String,
    val experience: String,
    val idVerification: String, // ID number or link
    val status: String = "pending", // "pending", "approved", "rejected"
    val feedback: String = "",
    val appliedAt: Long = System.currentTimeMillis()
)

// ------------------ DAO ------------------

@Dao
interface AppDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Courses ---
    @Query("SELECT * FROM courses WHERE status = :status ORDER BY createdAt DESC")
    fun getCoursesByStatusFlow(status: String): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    fun getAllCoursesFlow(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE instructorId = :instructorId ORDER BY createdAt DESC")
    fun getCoursesByInstructorFlow(instructorId: Int): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: Int): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    // --- Sections ---
    @Query("SELECT * FROM sections WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getSectionsForCourseFlow(courseId: Int): Flow<List<SectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: SectionEntity): Long

    @Update
    suspend fun updateSection(section: SectionEntity)

    @Delete
    suspend fun deleteSection(section: SectionEntity)

    // --- Lectures ---
    @Query("SELECT * FROM lectures WHERE sectionId = :sectionId ORDER BY orderIndex ASC")
    fun getLecturesForSectionFlow(sectionId: Int): Flow<List<LectureEntity>>

    @Query("SELECT * FROM lectures WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getLecturesForCourseFlow(courseId: Int): Flow<List<LectureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: LectureEntity): Long

    @Update
    suspend fun updateLecture(lecture: LectureEntity)

    @Delete
    suspend fun deleteLecture(lecture: LectureEntity)

    // --- Enrollments ---
    @Query("SELECT * FROM enrollments WHERE studentId = :studentId")
    fun getEnrollmentsForStudentFlow(studentId: Int): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE courseId = :courseId")
    fun getEnrollmentsForCourseFlow(courseId: Int): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE studentId = :studentId AND courseId = :courseId LIMIT 1")
    suspend fun getEnrollment(studentId: Int, courseId: Int): EnrollmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity): Long

    @Update
    suspend fun updateEnrollment(enrollment: EnrollmentEntity)

    // --- Lecture Progress ---
    @Query("SELECT * FROM lecture_progress WHERE userId = :userId AND courseId = :courseId")
    fun getProgressForCourseFlow(userId: Int, courseId: Int): Flow<List<LectureProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LectureProgressEntity)

    @Query("DELETE FROM lecture_progress WHERE userId = :userId AND lectureId = :lectureId")
    suspend fun deleteProgress(userId: Int, lectureId: Int)

    // --- Cart ---
    @Query("SELECT * FROM courses WHERE id IN (SELECT courseId FROM cart WHERE userId = :userId)")
    fun getCartItemsFlow(userId: Int): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cart: CartEntity)

    @Query("DELETE FROM cart WHERE userId = :userId AND courseId = :courseId")
    suspend fun removeFromCart(userId: Int, courseId: Int)

    @Query("DELETE FROM cart WHERE userId = :userId")
    suspend fun clearCart(userId: Int)

    // --- Wishlist ---
    @Query("SELECT * FROM courses WHERE id IN (SELECT courseId FROM wishlist WHERE userId = :userId)")
    fun getWishlistItemsFlow(userId: Int): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(wishlist: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE userId = :userId AND courseId = :courseId")
    suspend fun removeFromWishlist(userId: Int, courseId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE userId = :userId AND courseId = :courseId)")
    suspend fun isInWishlist(userId: Int, courseId: Int): Boolean

    // --- Reviews ---
    @Query("SELECT * FROM reviews WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getReviewsForCourseFlow(courseId: Int): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    // --- Q&A ---
    @Query("SELECT * FROM q_and_a WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getQAsForCourseFlow(courseId: Int): Flow<List<QandAEntity>>

    @Query("SELECT * FROM q_and_a WHERE lectureId = :lectureId ORDER BY createdAt DESC")
    fun getQAsForLectureFlow(lectureId: Int): Flow<List<QandAEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQA(qa: QandAEntity): Long

    @Update
    suspend fun updateQA(qa: QandAEntity)

    // --- Notes ---
    @Query("SELECT * FROM notes WHERE userId = :userId AND courseId = :courseId ORDER BY timestamp ASC")
    fun getNotesForCourseFlow(userId: Int, courseId: Int): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    // --- Payout Requests ---
    @Query("SELECT * FROM payout_requests ORDER BY requestedAt DESC")
    fun getAllPayoutRequestsFlow(): Flow<List<PayoutRequestEntity>>

    @Query("SELECT * FROM payout_requests WHERE instructorId = :instructorId ORDER BY requestedAt DESC")
    fun getPayoutRequestsForInstructorFlow(instructorId: Int): Flow<List<PayoutRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayoutRequest(payout: PayoutRequestEntity): Long

    @Update
    suspend fun updatePayoutRequest(payout: PayoutRequestEntity)

    // --- Coupons ---
    @Query("SELECT * FROM coupons WHERE code = :code LIMIT 1")
    suspend fun getCoupon(code: String): CouponEntity?

    @Query("SELECT * FROM coupons")
    fun getAllCouponsFlow(): Flow<List<CouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)

    @Update
    suspend fun updateCoupon(coupon: CouponEntity)

    @Delete
    suspend fun deleteCoupon(coupon: CouponEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUserFlow(userId: Int): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: Int)

    // --- Instructor Applications ---
    @Query("SELECT * FROM instructor_applications ORDER BY appliedAt DESC")
    fun getAllApplicationsFlow(): Flow<List<InstructorApplicationEntity>>

    @Query("SELECT * FROM instructor_applications WHERE userId = :userId LIMIT 1")
    suspend fun getApplicationByUserId(userId: Int): InstructorApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: InstructorApplicationEntity): Long

    @Update
    suspend fun updateApplication(app: InstructorApplicationEntity)
}

// ------------------ DATABASE ------------------

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        SectionEntity::class,
        LectureEntity::class,
        EnrollmentEntity::class,
        LectureProgressEntity::class,
        CartEntity::class,
        WishlistEntity::class,
        ReviewEntity::class,
        QandAEntity::class,
        NoteEntity::class,
        PayoutRequestEntity::class,
        CouponEntity::class,
        NotificationEntity::class,
        InstructorApplicationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
