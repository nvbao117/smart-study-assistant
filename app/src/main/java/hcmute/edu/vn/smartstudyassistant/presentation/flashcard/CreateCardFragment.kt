package hcmute.edu.vn.smartstudyassistant.presentation.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentCreateCardBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateCardFragment : Fragment() {

    private var _binding: FragmentCreateCardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FlashcardViewModel by viewModels()
    private val deckId: Long by lazy { arguments?.getLong("deckId") ?: -1L }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.btnSave.setOnClickListener {
            saveCard(andNew = false)
        }

        binding.btnSaveAndNew.setOnClickListener {
            saveCard(andNew = true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actionResult.collect { result ->
                result ?: return@collect
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Card saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), result.exceptionOrNull()?.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
                viewModel.clearActionResult()
            }
        }
    }

    private fun saveCard(andNew: Boolean) {
        val front = binding.etFront.text.toString().trim()
        val back = binding.etBack.text.toString().trim()

        if (front.isEmpty()) { binding.tilFront.error = "Required"; return }
        if (back.isEmpty()) { binding.tilBack.error = "Required"; return }

        binding.tilFront.error = null
        binding.tilBack.error = null

        viewModel.createCard(deckId, front, back)

        if (andNew) {
            binding.etFront.text?.clear()
            binding.etBack.text?.clear()
            binding.etFront.requestFocus()
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
