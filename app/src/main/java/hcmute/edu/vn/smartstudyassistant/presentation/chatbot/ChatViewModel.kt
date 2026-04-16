package hcmute.edu.vn.smartstudyassistant.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiProviderType
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiSettingsEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatMessageEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatSessionEntity
import hcmute.edu.vn.smartstudyassistant.domain.usecase.auth.GetCurrentUserUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot.CreateChatSessionUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot.DeleteChatSessionUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot.GetChatHistoryUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot.ManageAiSettingsUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot.SendChatMessageUseCase
import hcmute.edu.vn.smartstudyassistant.domain.repository.ChatRepository
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val chatRepository: ChatRepository,
    private val createChatSessionUseCase: CreateChatSessionUseCase,
    private val deleteChatSessionUseCase: DeleteChatSessionUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val manageAiSettingsUseCase: ManageAiSettingsUseCase
) : ViewModel() {

    private val _sessionsState = MutableStateFlow<Resource<List<ChatSessionEntity>>>(Resource.Loading)
    val sessionsState: StateFlow<Resource<List<ChatSessionEntity>>> = _sessionsState

    private val _messagesState = MutableStateFlow<Resource<List<ChatMessageEntity>>>(Resource.Loading)
    val messagesState: StateFlow<Resource<List<ChatMessageEntity>>> = _messagesState

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    private val _sendError = MutableStateFlow<String?>(null)
    val sendError: StateFlow<String?> = _sendError

    private val _aiSettings = MutableStateFlow<AiSettingsEntity?>(null)
    val aiSettings: StateFlow<AiSettingsEntity?> = _aiSettings

    private val _newSessionId = MutableStateFlow<Long?>(null)
    val newSessionId: StateFlow<Long?> = _newSessionId

    init {
        loadSessions()
    }

    fun loadSessions() {
        viewModelScope.launch {
            _sessionsState.value = Resource.Loading
            val user = getCurrentUserUseCase() ?: run {
                _sessionsState.value = Resource.Error("Not logged in")
                return@launch
            }
            try {
                chatRepository.getSessionsByUserId(user.id).collect { sessions ->
                    _sessionsState.value = Resource.Success(sessions)
                }
            } catch (e: Exception) {
                _sessionsState.value = Resource.Error(e.message ?: "Failed to load sessions")
            }
        }
    }

    fun loadMessages(sessionId: Long) {
        viewModelScope.launch {
            _messagesState.value = Resource.Loading
            try {
                getChatHistoryUseCase(sessionId).collect { messages ->
                    _messagesState.value = Resource.Success(messages)
                }
            } catch (e: Exception) {
                _messagesState.value = Resource.Error(e.message ?: "Failed to load messages")
            }
        }
    }

    fun createNewSession(title: String = "New Chat") {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() ?: return@launch
            val settings = manageAiSettingsUseCase.getSettings(user.id)
            val session = ChatSessionEntity(
                userId = user.id,
                title = title,
                aiProvider = settings.selectedProvider
            )
            val result = createChatSessionUseCase(session)
            result.onSuccess { id -> _newSessionId.value = id }
        }
    }

    fun sendMessage(sessionId: Long, text: String) {
        if (text.isBlank() || _isSending.value) return
        viewModelScope.launch {
            _isSending.value = true
            _sendError.value = null
            val user = getCurrentUserUseCase() ?: run {
                _isSending.value = false
                return@launch
            }
            val result = sendChatMessageUseCase(sessionId, user.id, text)
            result.onFailure { e ->
                _sendError.value = e.message ?: "Failed to send message"
            }
            _isSending.value = false
        }
    }

    fun deleteSession(session: ChatSessionEntity) {
        viewModelScope.launch {
            try {
                deleteChatSessionUseCase(session)
            } catch (e: Exception) {
                // Silent
            }
        }
    }

    fun loadAiSettings() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() ?: return@launch
            _aiSettings.value = manageAiSettingsUseCase.getSettings(user.id)
        }
    }

    fun saveApiKey(provider: AiProviderType, rawKey: String) {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() ?: return@launch
            manageAiSettingsUseCase.saveApiKey(user.id, provider, rawKey)
            loadAiSettings()
        }
    }

    fun saveSettings(settings: AiSettingsEntity) {
        viewModelScope.launch {
            manageAiSettingsUseCase.saveSettings(settings)
        }
    }

    fun clearNewSessionId() { _newSessionId.value = null }
    fun clearSendError() { _sendError.value = null }
}
