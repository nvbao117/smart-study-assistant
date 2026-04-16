package hcmute.edu.vn.smartstudyassistant.presentation.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiProviderType
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentAiSettingsBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AiSettingsFragment : Fragment() {

    private var _binding: FragmentAiSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.seekTemperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val temp = progress / 100.0
                binding.tvTemperatureValue.text = String.format("%.2f", temp)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.btnSave.setOnClickListener { saveSettings() }

        viewModel.loadAiSettings()
        observeSettings()
    }

    private fun observeSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.aiSettings.collect { settings ->
                if (settings != null) {
                    // Select provider radio button
                    val radioId = when (settings.selectedProvider) {
                        AiProviderType.GEMINI -> R.id.rbGemini
                        AiProviderType.OPENAI -> R.id.rbOpenAI
                        AiProviderType.ANTHROPIC -> R.id.rbAnthropic
                    }
                    binding.rgProvider.check(radioId)

                    // Temperature (0.0–1.0 mapped to 0–100)
                    val tempProgress = (settings.temperature * 100).toInt().coerceIn(0, 100)
                    binding.seekTemperature.progress = tempProgress
                    binding.tvTemperatureValue.text = String.format("%.2f", settings.temperature)
                }
            }
        }
    }

    private fun saveSettings() {
        val currentSettings = viewModel.aiSettings.value ?: return

        val provider = when (binding.rgProvider.checkedRadioButtonId) {
            R.id.rbOpenAI -> AiProviderType.OPENAI
            R.id.rbAnthropic -> AiProviderType.ANTHROPIC
            else -> AiProviderType.GEMINI
        }

        val apiKey = binding.tilApiKey.editText?.text?.toString()?.trim()
        val temperature = binding.seekTemperature.progress / 100.0

        val updated = currentSettings.copy(
            selectedProvider = provider,
            temperature = temperature
        )
        viewModel.saveSettings(updated)

        if (!apiKey.isNullOrBlank()) {
            viewModel.saveApiKey(provider, apiKey)
        }

        Snackbar.make(binding.root, "Settings saved", Snackbar.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
