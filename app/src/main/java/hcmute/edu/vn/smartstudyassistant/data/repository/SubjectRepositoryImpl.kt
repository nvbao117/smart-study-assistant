package hcmute.edu.vn.smartstudyassistant.data.repository

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.dao.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import hcmute.edu.vn.smartstudyassistant.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow

class SubjectRepositoryImpl @Inject constructor(private val subjectDao: SubjectDao) : SubjectRepository {

    override suspend fun createSubject(subject: SubjectEntity): Long = subjectDao.insert(subject)

    override suspend fun updateSubject(subject: SubjectEntity) = subjectDao.update(subject)

    override suspend fun deleteSubject(subject: SubjectEntity) = subjectDao.delete(subject)

    override suspend fun getSubjectById(id: Long): SubjectEntity? = subjectDao.getById(id)

    override fun getSubjectsByUserId(userId: Long): Flow<List<SubjectEntity>> =
        subjectDao.getByUserId(userId)

    override fun getSubjectsWithTaskCount(userId: Long): Flow<List<SubjectWithTaskCount>> =
        subjectDao.getWithTaskCount(userId)
}
