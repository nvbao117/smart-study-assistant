package hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class ExportChatUseCase @Inject constructor(private val chatRepository: ChatRepository) {

    suspend operator fun invoke(sessionId: Long): String {
        val session = chatRepository.getSessionById(sessionId)
            ?: return "Session not found"
        val messages = chatRepository.getMessagesBySessionId(sessionId).first()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        val sb = StringBuilder()
        sb.appendLine("=== ${session.title} ===")
        sb.appendLine("Provider: ${session.aiProvider.name}")
        sb.appendLine("Date: ${sdf.format(Date(session.createdAt))}")
        sb.appendLine("─".repeat(40))

        for (msg in messages) {
            val role = when (msg.role) {
                hcmute.edu.vn.smartstudyassistant.data.local.db.entity.MessageRole.USER -> "You"
                hcmute.edu.vn.smartstudyassistant.data.local.db.entity.MessageRole.ASSISTANT -> session.aiProvider.name
                hcmute.edu.vn.smartstudyassistant.data.local.db.entity.MessageRole.SYSTEM -> "System"
            }
            sb.appendLine("[${sdf.format(Date(msg.sentAt))}] $role:")
            sb.appendLine(msg.content)
            sb.appendLine()
        }
        return sb.toString()
    }
}
