package hcmute.edu.vn.smartstudyassistant.domain.repository

import hcmute.edu.vn.smartstudyassistant.data.local.dao.SubjectWithTaskCount
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    suspend fun createSubject(subject: SubjectEntity): Long
    suspend fun updateSubject(subject: SubjectEntity)
    suspend fun deleteSubject(subject: SubjectEntity)
    suspend fun getSubjectById(id: Long): SubjectEntity?
    fun getSubjectsByUserId(userId: Long): Flow<List<SubjectEntity>>
    fun getSubjectsWithTaskCount(userId: Long): Flow<List<SubjectWithTaskCount>>
}
