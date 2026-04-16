package hcmute.edu.vn.smartstudyassistant.presentation.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hcmute.edu.vn.smartstudyassistant.data.local.dao.DeckWithCardCount
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.DeckEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.domain.model.DeckStats
import hcmute.edu.vn.smartstudyassistant.domain.usecase.auth.GetCurrentUserUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard.CreateCardUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard.CreateDeckUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard.DeleteDeckUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard.GetDeckStatsUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard.GetDueCardsUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard.ReviewCardUseCase
import hcmute.edu.vn.smartstudyassistant.domain.repository.DeckRepository
import hcmute.edu.vn.smartstudyassistant.util.Resource
import hcmute.edu.vn.smartstudyassistant.util.todayMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val deckRepository: DeckRepository,
    private val createDeckUseCase: CreateDeckUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    private val createCardUseCase: CreateCardUseCase,
    private val getDueCardsUseCase: GetDueCardsUseCase,
    private val getDeckStatsUseCase: GetDeckStatsUseCase,
    private val reviewCardUseCase: ReviewCardUseCase
) : ViewModel() {

    private val _decksState = MutableStateFlow<Resource<List<DeckWithCardCount>>>(Resource.Loading)
    val decksState: StateFlow<Resource<List<DeckWithCardCount>>> = _decksState

    private val _dueCardsState = MutableStateFlow<Resource<List<FlashcardEntity>>>(Resource.Loading)
    val dueCardsState: StateFlow<Resource<List<FlashcardEntity>>> = _dueCardsState

    private val _deckStats = MutableStateFlow<DeckStats?>(null)
    val deckStats: StateFlow<DeckStats?> = _deckStats

    private val _actionResult = MutableStateFlow<Result<Unit>?>(null)
    val actionResult: StateFlow<Result<Unit>?> = _actionResult

    init {
        loadDecks()
    }

    fun loadDecks() {
        viewModelScope.launch {
            _decksState.value = Resource.Loading
            val user = getCurrentUserUseCase() ?: run {
                _decksState.value = Resource.Error("Not logged in")
                return@launch
            }
            try {
                deckRepository.getDecksWithCardCount(user.id, todayMillis()).collect { decks ->
                    _decksState.value = Resource.Success(decks)
                }
            } catch (e: Exception) {
                _decksState.value = Resource.Error(e.message ?: "Failed to load decks")
            }
        }
    }

    fun loadDueCards(deckId: Long) {
        viewModelScope.launch {
            _dueCardsState.value = Resource.Loading
            try {
                getDueCardsUseCase(deckId).collect { cards ->
                    _dueCardsState.value = Resource.Success(cards)
                }
            } catch (e: Exception) {
                _dueCardsState.value = Resource.Error(e.message ?: "Failed to load cards")
            }
        }
    }

    fun loadDeckStats(deckId: Long) {
        viewModelScope.launch {
            try {
                _deckStats.value = getDeckStatsUseCase(deckId)
            } catch (e: Exception) {
                _deckStats.value = null
            }
        }
    }

    fun createDeck(name: String, description: String) {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() ?: return@launch
            val deck = DeckEntity(userId = user.id, name = name, description = description)
            createDeckUseCase(deck)
        }
    }

    fun deleteDeck(deck: DeckEntity) {
        viewModelScope.launch {
            try {
                deleteDeckUseCase(deck)
            } catch (e: Exception) {
                // Silent fail - UI can handle
            }
        }
    }

    fun createCard(deckId: Long, front: String, back: String) {
        viewModelScope.launch {
            val card = FlashcardEntity(deckId = deckId, front = front, back = back)
            val result = createCardUseCase(card)
            _actionResult.value = result.map { }
        }
    }

    fun reviewCard(cardId: Long, quality: Int) {
        viewModelScope.launch {
            reviewCardUseCase(cardId, quality)
        }
    }

    fun clearActionResult() {
        _actionResult.value = null
    }
}
