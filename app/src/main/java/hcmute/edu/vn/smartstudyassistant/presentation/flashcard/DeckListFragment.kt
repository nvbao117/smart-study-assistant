package hcmute.edu.vn.smartstudyassistant.presentation.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentDeckListBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeckListFragment : Fragment() {

    private var _binding: FragmentDeckListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FlashcardViewModel by viewModels()
    private val adapter = DeckAdapter { deck ->
        findNavController().navigate(
            R.id.action_deckList_to_deckDetail,
            bundleOf("deckId" to deck.id, "deckName" to deck.name)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeckListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvDecks.adapter = adapter
        binding.fabCreateDeck.setOnClickListener { showCreateDeckDialog() }
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.decksState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.layoutEmpty.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        val decks = state.data
                        adapter.submitList(decks)
                        val totalCards = decks.sumOf { it.totalCards }
                        val totalDue = decks.sumOf { it.dueCards }
                        binding.tvDeckCount.text = "${decks.size} decks"
                        binding.tvTotalCards.text = totalCards.toString()
                        binding.tvTotalDue.text = totalDue.toString()
                        binding.layoutEmpty.visibility = if (decks.isEmpty()) View.VISIBLE else View.GONE
                    }
                    is Resource.Error -> {
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showCreateDeckDialog() {
        val layout = layoutInflater.inflate(
            android.R.layout.simple_list_item_2, null
        )
        // Simple approach: use MaterialAlertDialog with a text input
        val input = EditText(requireContext()).apply {
            hint = "Deck name"
            setPadding(48, 32, 48, 16)
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Deck")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    viewModel.createDeck(name, "")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
