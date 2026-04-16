package hcmute.edu.vn.smartstudyassistant.presentation.gamification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentAchievementsBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AchievementListFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GamificationViewModel by viewModels()
    private val adapter = AchievementAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.rvAchievements.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                if (state is Resource.Success) {
                    adapter.submitList(state.data.achievements)
                    adapter.setUnlocked(state.data.unlockedAchievementIds)

                    val unlocked = state.data.unlockedAchievementIds.size
                    val total = state.data.achievements.size
                    binding.tvUnlocked.text = "$unlocked / $total Unlocked"
                    val progress = if (total > 0) (unlocked * 100) / total else 0
                    binding.progressAchievements.setProgressCompat(progress, true)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
