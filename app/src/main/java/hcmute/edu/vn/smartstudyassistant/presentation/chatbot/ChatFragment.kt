package hcmute.edu.vn.smartstudyassistant.presentation.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentChatBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()

    private val messageAdapter = MessageAdapter()
    private var sessionId: Long = -1L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionId = arguments?.getLong("sessionId", -1L) ?: -1L
        val title = arguments?.getString("sessionTitle") ?: "Chat"

        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.rvMessages.layoutManager = layoutManager
        binding.rvMessages.adapter = messageAdapter

        binding.btnSend.setOnClickListener { sendMessage() }
        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else false
        }

        if (sessionId != -1L) {
            viewModel.loadMessages(sessionId)
        }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messagesState.collect { state ->
                if (state is Resource.Success) {
                    messageAdapter.submitList(state.data) {
                        if (state.data.isNotEmpty()) {
                            binding.rvMessages.scrollToPosition(state.data.size - 1)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSending.collect { sending ->
                binding.btnSend.isEnabled = !sending
                binding.layoutTyping.visibility = if (sending) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sendError.collect { error ->
                if (error != null) {
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                    viewModel.clearSendError()
                }
            }
        }
    }

    private fun sendMessage() {
        val text = binding.etMessage.text?.toString()?.trim() ?: return
        if (text.isBlank()) return
        binding.etMessage.setText("")
        viewModel.sendMessage(sessionId, text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
