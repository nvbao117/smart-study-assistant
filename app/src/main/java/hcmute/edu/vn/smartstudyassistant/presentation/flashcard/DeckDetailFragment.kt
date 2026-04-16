package hcmute.edu.vn.smartstudyassistant.presentation.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentDeckDetailBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeckDetailFragment : Fragment() {

    private var _binding: FragmentDeckDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FlashcardViewModel by viewModels()

    private val deckId: Long by lazy {
        arguments?.getLong("deckId") ?: -1L
    }

    private val deckName: String by lazy {
        arguments?.getString("deckName") ?: "Deck"
    }

    private val cardAdapter = CardAdapter { card ->
        // Card click - could show edit
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeckDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = deckName
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.rvCards.adapter = cardAdapter

        binding.btnStudyNow.setOnClickListener {
            findNavController().navigate(
                R.id.action_deckDetail_to_review,
                bundleOf("deckId" to deckId)
            )
        }

        binding.fabAddCard.setOnClickListener {
            findNavController().navigate(
                R.id.action_deckDetail_to_createCard,
                bundleOf("deckId" to deckId)
            )
        }

        viewModel.loadDueCards(deckId)
        viewModel.loadDeckStats(deckId)
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dueCardsState.collect { state ->
                if (state is Resource.Success) {
                    cardAdapter.submitList(state.data)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deckStats.collect { stats ->
                stats?.let {
                    binding.tvTotalCards.text = it.totalCards.toString()
                    binding.tvDueCards.text = it.dueToday.toString()
                    binding.tvEaseFactor.text = String.format("%.1f", it.averageEaseFactor)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
