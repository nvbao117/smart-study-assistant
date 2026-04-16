package hcmute.edu.vn.smartstudyassistant.presentation.pomodoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentPomodoroHistoryBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PomodoroHistoryFragment : Fragment() {

    private var _binding: FragmentPomodoroHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PomodoroViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPomodoroHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                state.stats?.let { stats ->
                    val weekHours = stats.focusWeek / 60
                    val weekMinutes = stats.focusWeek % 60
                    binding.tvWeekFocus.text = if (weekHours > 0) "${weekHours}h ${weekMinutes}m" else "${weekMinutes}m"
                    binding.tvTotalSessions.text = stats.totalSessions.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
