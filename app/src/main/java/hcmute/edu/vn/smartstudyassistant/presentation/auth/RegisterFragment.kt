package hcmute.edu.vn.smartstudyassistant.presentation.auth

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
import hcmute.edu.vn.smartstudyassistant.R
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentRegisterBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    // We can share the AuthViewModel if scoped to activity, or create a new one scoped to fragment.
    // For registering, a new instance is fine.
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Backend assumes username, email, password
            viewModel.register(username, password, email)
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerState.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.btnRegister.isEnabled = false
                        binding.btnRegister.text = "Đang xử lý..."
                    }
                    is Resource.Success -> {
                        binding.btnRegister.isEnabled = true
                        binding.btnRegister.text = "Create Account"
                        if (state.data != null) {
                            Toast.makeText(requireContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp() // Back to login
                        }
                    }
                    is Resource.Error -> {
                        binding.btnRegister.isEnabled = true
                        binding.btnRegister.text = "Create Account"
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
