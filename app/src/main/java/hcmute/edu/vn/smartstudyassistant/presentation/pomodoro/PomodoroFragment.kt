package hcmute.edu.vn.smartstudyassistant.presentation.pomodoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSessionType
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentPomodoroBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PomodoroFragment : Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PomodoroViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPomodoroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHistory.setOnClickListener {
            findNavController().navigate(R.id.action_pomodoro_to_history)
        }

        binding.btnStartPause.setOnClickListener { viewModel.startPause() }
        binding.btnReset.setOnClickListener { viewModel.reset() }
        binding.btnSkip.setOnClickListener { viewModel.skip() }

        binding.tabSessionType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val type = when (tab.position) {
                    0 -> PomodoroSessionType.FOCUS
                    1 -> PomodoroSessionType.SHORT_BREAK
                    else -> PomodoroSessionType.LONG_BREAK
                }
                viewModel.selectSessionType(type)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })

        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                // Timer display
                val minutes = state.timeLeftSeconds / 60
                val seconds = state.timeLeftSeconds % 60
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)

                // Progress ring
                val progress = if (state.totalSeconds > 0) {
                    (state.timeLeftSeconds * 100) / state.totalSeconds
                } else 0
                binding.progressTimer.setProgressCompat(progress, true)

                // Start/Pause button text
                binding.btnStartPause.text = when (state.timerState) {
                    TimerState.RUNNING -> "Pause"
                    TimerState.PAUSED -> "Resume"
                    TimerState.IDLE -> "Start"
                }

                // Session label
                binding.tvSessionLabel.text = when (state.sessionType) {
                    PomodoroSessionType.FOCUS -> "FOCUS SESSION"
                    PomodoroSessionType.SHORT_BREAK -> "SHORT BREAK"
                    PomodoroSessionType.LONG_BREAK -> "LONG BREAK"
                }

                // Session counter
                binding.tvSessionCount.text = "Session ${state.completedSessions + 1}"

                // Stats
                state.stats?.let { stats ->
                    binding.tvFocusToday.text = "${stats.focusToday}m"
                    val weekHours = stats.focusWeek / 60.0
                    binding.tvFocusWeek.text = String.format("%.1fh", weekHours)
                    binding.tvSessionsToday.text = stats.totalSessions.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
