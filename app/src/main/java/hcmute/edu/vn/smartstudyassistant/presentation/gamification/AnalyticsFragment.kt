package hcmute.edu.vn.smartstudyassistant.presentation.gamification

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentAnalyticsBinding
import hcmute.edu.vn.smartstudyassistant.domain.model.Analytics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GamificationViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.analyticsState.collect { state ->
                if (state is Resource.Success) {
                    displayAnalytics(state.data)
                }
            }
        }
    }

    private fun displayAnalytics(analytics: Analytics) {
        val totalFocusHours = analytics.totalFocusMinutes / 60
        val totalFocusMins = analytics.totalFocusMinutes % 60
        binding.tvTotalFocus.text = if (totalFocusHours > 0) "${totalFocusHours}h ${totalFocusMins}m" else "${totalFocusMins}m"

        val dayCount = analytics.data.size.coerceAtLeast(1)
        val avgMins = analytics.totalFocusMinutes / dayCount
        binding.tvAvgSession.text = "${avgMins}m"

        // Peak hour: find the day with highest focus minutes and show the date
        val peakDay = analytics.data.maxByOrNull { it.focusMinutes }
        binding.tvPeakHour.text = peakDay?.let {
            SimpleDateFormat("EEE", Locale.getDefault()).format(Date(it.date))
        } ?: "--"

        binding.tvTasksCompleted.text = analytics.totalTasksCompleted.toString()

        // Build bar chart from daily data
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dailyData = analytics.data.map { entity ->
            dayFormat.format(Date(entity.date)) to entity.focusMinutes
        }
        buildBarChart(dailyData)
    }

    private fun buildBarChart(dailyData: List<Pair<String, Int>>) {
        binding.layoutBarChart.removeAllViews()
        binding.layoutDayLabels.removeAllViews()
        if (dailyData.isEmpty()) return

        val maxValue = dailyData.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
        val barHeight = 120 // dp container height

        dailyData.forEach { (day, minutes) ->
            // Bar
            val barHeightRatio = (minutes.toFloat() / maxValue)
            val barHeightPx = (barHeight * barHeightRatio).toInt().coerceAtLeast(4)
            val bar = View(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(0, barHeightPx).also {
                    (it as? ViewGroup.MarginLayoutParams)?.marginStart = 4
                }
                background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_pill)
                backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.accent_primary)
            }
            val params = android.widget.LinearLayout.LayoutParams(0, barHeightPx, 1f)
            params.marginStart = 4
            params.marginEnd = 4
            bar.layoutParams = params
            binding.layoutBarChart.addView(bar)

            // Label
            val label = TextView(requireContext()).apply {
                text = day
                textSize = 10f
                gravity = Gravity.CENTER
                layoutParams = android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary_dark))
            }
            binding.layoutDayLabels.addView(label)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
