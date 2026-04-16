package hcmute.edu.vn.smartstudyassistant.presentation.flashcard

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentFlashcardReviewBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlashcardReviewFragment : Fragment() {

    private var _binding: FragmentFlashcardReviewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FlashcardViewModel by viewModels()

    private val deckId: Long by lazy { arguments?.getLong("deckId") ?: -1L }

    private var cards: List<FlashcardEntity> = emptyList()
    private var currentIndex = 0
    private var isShowingBack = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFlashcardReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.cardContainer.setOnClickListener { flipCard() }
        binding.cardFront.setOnClickListener { flipCard() }
        binding.cardBack.setOnClickListener { flipCard() }

        binding.btnAgain.setOnClickListener { submitReview(1) }
        binding.btnHard.setOnClickListener { submitReview(2) }
        binding.btnGood.setOnClickListener { submitReview(4) }
        binding.btnEasy.setOnClickListener { submitReview(5) }
        binding.btnFinish.setOnClickListener { findNavController().navigateUp() }

        viewModel.loadDueCards(deckId)
        observeCards()
    }

    private fun observeCards() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dueCardsState.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        cards = state.data
                        if (cards.isEmpty()) showDoneState() else showCard(0)
                        binding.progressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        showDoneState()
                    }
                }
            }
        }
    }

    private fun showCard(index: Int) {
        if (index >= cards.size) { showDoneState(); return }
        currentIndex = index
        isShowingBack = false
        val card = cards[index]
        binding.tvFront.text = card.front
        binding.tvBack.text = card.back
        binding.cardFront.visibility = View.VISIBLE
        binding.cardBack.visibility = View.GONE
        binding.layoutDifficultyButtons.visibility = View.GONE
        binding.tvTapHint.visibility = View.VISIBLE

        // Progress
        binding.progressReview.max = cards.size
        binding.progressReview.progress = index
        binding.tvProgressCount.text = "${index + 1} / ${cards.size}"

        binding.layoutDone.visibility = View.GONE
        binding.cardContainer.visibility = View.VISIBLE
        binding.layoutProgress.visibility = View.VISIBLE
        binding.tvTapHint.visibility = View.VISIBLE
    }

    private fun flipCard() {
        if (isShowingBack) return
        isShowingBack = true

        val flipOut = ObjectAnimator.ofFloat(binding.cardFront, "rotationY", 0f, 90f).apply { duration = 150 }
        val flipIn = ObjectAnimator.ofFloat(binding.cardBack, "rotationY", -90f, 0f).apply { duration = 150 }

        flipOut.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                binding.cardFront.visibility = View.GONE
                binding.cardBack.visibility = View.VISIBLE
                flipIn.start()
            }
        })
        flipOut.start()

        binding.layoutDifficultyButtons.visibility = View.VISIBLE
        binding.tvTapHint.visibility = View.GONE
    }

    private fun submitReview(quality: Int) {
        val card = cards.getOrNull(currentIndex) ?: return
        viewModel.reviewCard(card.id, quality)
        val next = currentIndex + 1
        if (next >= cards.size) showDoneState() else showCard(next)
    }

    private fun showDoneState() {
        binding.cardContainer.visibility = View.GONE
        binding.layoutProgress.visibility = View.GONE
        binding.layoutDifficultyButtons.visibility = View.GONE
        binding.tvTapHint.visibility = View.GONE
        binding.layoutDone.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
