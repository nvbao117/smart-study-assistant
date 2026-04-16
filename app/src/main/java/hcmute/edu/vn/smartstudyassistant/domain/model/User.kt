package hcmute.edu.vn.smartstudyassistant.domain.model

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val displayName: String,
    val createdAt: Long,
    val lastLoginAt: Long
)
