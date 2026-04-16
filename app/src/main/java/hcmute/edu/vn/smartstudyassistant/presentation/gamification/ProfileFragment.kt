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
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentProfileBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GamificationViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardAchievements.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_achievements)
        }
        binding.cardAnalytics.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_analytics)
        }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        val data = state.data
                        val user = data.user ?: return@collect

                        binding.tvDisplayName.text = user.displayName
                        binding.tvUsername.text = "@${user.username}"
                        binding.tvAvatarLetter.text = user.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
                        binding.tvLevel.text = data.level.toString()
                        binding.tvStreak.text = "🔥 ${data.streak}"
                        binding.tvTotalXp.text = data.currentXp.toString()

                        // XP progress bar
                        val xpProgress = if (data.xpToNextLevel > 0)
                            (data.currentXp * 100) / data.xpToNextLevel else 0
                        binding.progressXp.setProgressCompat(xpProgress.coerceAtMost(100), true)
                        binding.tvXpInfo.text = "${data.currentXp} / ${data.xpToNextLevel} XP"

                        // Achievement count
                        val unlocked = data.unlockedAchievementIds.size
                        val total = data.achievements.size
                        binding.tvAchievementCount.text = "$unlocked / $total unlocked"
                    }
                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
