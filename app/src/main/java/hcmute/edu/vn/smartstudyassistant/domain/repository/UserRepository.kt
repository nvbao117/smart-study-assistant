package hcmute.edu.vn.smartstudyassistant.domain.repository

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity

interface UserRepository {
    suspend fun insert(user: UserEntity): Long
    suspend fun getByEmail(email: String): UserEntity?
    suspend fun getByUsername(username: String): UserEntity?
    suspend fun getById(id: Long): UserEntity?
    suspend fun updateLastLogin(id: Long, timestamp: Long)
    suspend fun delete(user: UserEntity)
}
