package com.oytask.app.ui.dialog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oytask.app.R
import com.oytask.app.data.model.Priority
import com.oytask.app.data.model.Task
import com.oytask.app.databinding.DialogTaskBinding
import com.oytask.app.util.DateUtils
import java.util.*

class TaskDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogTaskBinding? = null
    private val binding get() = _binding!!

    private var selectedDueDate: Long? = null
    private var selectedPriority: Priority = Priority.MEDIUM
    private var existingTask: Task? = null
    private var onSave: ((Task) -> Unit)? = null

    companion object {
        fun newInstance(task: Task? = null, onSave: (Task) -> Unit): TaskDialogFragment {
            return TaskDialogFragment().apply {
                this.existingTask = task
                this.onSave = onSave
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPrioritySpinner()
        setupDatePicker()
        setupButtons()
        populateExistingTask()
    }

    private fun setupPrioritySpinner() {
        val priorities = arrayOf("Baja", "Media", "Alta", "Urgente")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, priorities)
        binding.spinnerPriority.adapter = adapter
        binding.spinnerPriority.setSelection(1)
    }

    private fun setupDatePicker() {
        binding.btnSelectDate.setOnClickListener {
            val cal = Calendar.getInstance()
            selectedDueDate?.let { cal.timeInMillis = it }

            DatePickerDialog(
                requireContext(),
                R.style.DatePickerTheme,
                { _, year, month, day ->
                    val selected = Calendar.getInstance().apply {
                        set(year, month, day, 23, 59, 59)
                    }
                    selectedDueDate = selected.timeInMillis
                    binding.tvSelectedDate.text = DateUtils.formatFull(selectedDueDate)
                    binding.btnClearDate.visibility = View.VISIBLE
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnClearDate.setOnClickListener {
            selectedDueDate = null
            binding.tvSelectedDate.text = getString(R.string.no_date)
            binding.btnClearDate.visibility = View.GONE
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            if (title.isEmpty()) {
                binding.tilTitle.error = getString(R.string.error_empty_title)
                return@setOnClickListener
            }

            selectedPriority = when (binding.spinnerPriority.selectedItemPosition) {
                0 -> Priority.LOW
                1 -> Priority.MEDIUM
                2 -> Priority.HIGH
                3 -> Priority.URGENT
                else -> Priority.MEDIUM
            }

            val task = (existingTask ?: Task(title = title)).copy(
                title = title,
                description = binding.etDescription.text.toString().trim(),
                dueDate = selectedDueDate,
                priority = selectedPriority
            )

            onSave?.invoke(task)
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun populateExistingTask() {
        existingTask?.let { task ->
            binding.dialogTitle.text = getString(R.string.edit_task)
            binding.etTitle.setText(task.title)
            binding.etDescription.setText(task.description)
            selectedDueDate = task.dueDate
            selectedPriority = task.priority

            if (task.dueDate != null) {
                binding.tvSelectedDate.text = DateUtils.formatFull(task.dueDate)
                binding.btnClearDate.visibility = View.VISIBLE
            }

            val priorityIndex = when (task.priority) {
                Priority.LOW -> 0
                Priority.MEDIUM -> 1
                Priority.HIGH -> 2
                Priority.URGENT -> 3
            }
            binding.spinnerPriority.setSelection(priorityIndex)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
