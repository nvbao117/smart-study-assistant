package hcmute.edu.vn.smartstudyassistant.data.repository

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.dao.UserDao
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.UserRepository

class UserRepositoryImpl @Inject constructor(private val userDao: UserDao) : UserRepository {

    override suspend fun insert(user: UserEntity): Long = userDao.insert(user)

    override suspend fun getByEmail(email: String): UserEntity? = userDao.getByEmail(email)

    override suspend fun getByUsername(username: String): UserEntity? = userDao.getByUsername(username)

    override suspend fun getById(id: Long): UserEntity? = userDao.getById(id)

    override suspend fun updateLastLogin(id: Long, timestamp: Long) =
        userDao.updateLastLogin(id, timestamp)

    override suspend fun delete(user: UserEntity) = userDao.delete(user)
}
