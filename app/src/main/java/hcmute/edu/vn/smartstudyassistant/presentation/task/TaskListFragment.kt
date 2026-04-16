package hcmute.edu.vn.smartstudyassistant.presentation.task

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
import hcmute.edu.vn.smartstudyassistant.databinding.FragmentTaskListBinding
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupUI() {
        val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(Date())

        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskList_to_taskDetail)
        }
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskClick = { task ->
                findNavController().navigate(R.id.action_taskList_to_taskDetail)
            },
            onTaskComplete = { task ->
                viewModel.completeTask(task)
            }
        )
        binding.rvTasks.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasksState.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        // Optional: Show loading
                    }
                    is Resource.Success -> {
                        val tasks = state.data ?: emptyList()
                        adapter.submitList(tasks)
                        
                        binding.layoutEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
                        binding.rvTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
                    }
                    is Resource.Error -> {
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
