package hcmute.edu.vn.smartstudyassistant.presentation.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatSessionEntity
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentChatListBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()

    private val adapter = ChatSessionAdapter(
        onSessionClick = { session -> openChat(session) },
        onDeleteClick = { session -> confirmDelete(session) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSessions.adapter = adapter

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_chatList_to_aiSettings)
        }

        binding.cardNewChat.setOnClickListener { startNewChat() }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sessionsState.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        val sessions = state.data
                        adapter.submitList(sessions)
                        binding.layoutEmpty.visibility = if (sessions.isEmpty()) View.VISIBLE else View.GONE
                        binding.rvSessions.visibility = if (sessions.isEmpty()) View.GONE else View.VISIBLE
                    }
                    is Resource.Loading -> Unit
                    is Resource.Error -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newSessionId.collect { id ->
                if (id != null) {
                    viewModel.clearNewSessionId()
                    findNavController().navigate(
                        R.id.action_chatList_to_chat,
                        bundleOf("sessionId" to id, "sessionTitle" to "New Chat")
                    )
                }
            }
        }
    }

    private fun startNewChat() {
        viewModel.createNewSession()
    }

    private fun openChat(session: ChatSessionEntity) {
        findNavController().navigate(
            R.id.action_chatList_to_chat,
            bundleOf("sessionId" to session.id, "sessionTitle" to session.title)
        )
    }

    private fun confirmDelete(session: ChatSessionEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Chat")
            .setMessage("Delete \"${session.title}\"? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteSession(session) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
