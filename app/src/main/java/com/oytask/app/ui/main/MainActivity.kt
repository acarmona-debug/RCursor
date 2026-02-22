package com.oytask.app.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.oytask.app.R
import com.oytask.app.data.model.Task
import com.oytask.app.data.model.TaskFilter
import com.oytask.app.databinding.ActivityMainBinding
import com.oytask.app.ui.adapter.SwipeToDeleteCallback
import com.oytask.app.ui.adapter.TaskAdapter
import com.oytask.app.ui.dialog.TaskDialogFragment
import com.oytask.app.ui.viewmodel.TaskViewModel
import com.oytask.app.util.VoiceParser

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private var speechRecognizer: SpeechRecognizer? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceRecognition()
        } else {
            Toast.makeText(this, R.string.mic_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        setupRecyclerView()
        setupFab()
        setupVoiceButton()
        setupFilterChips()
        setupSearch()
        setupSwipeRefresh()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onTaskClick = { task -> showEditDialog(task) },
            onTaskLongClick = { task -> showTaskOptions(task) },
            onCheckChanged = { task -> viewModel.toggleComplete(task) }
        )

        binding.recyclerTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
        }

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = adapter.currentList[position]

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.deleteTask(task)
                        showUndoSnackbar(task)
                    }
                    ItemTouchHelper.RIGHT -> {
                        viewModel.toggleComplete(task)
                    }
                }
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.recyclerTasks)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val anim = AnimationUtils.loadAnimation(this, R.anim.fab_pulse)
            binding.fabAdd.startAnimation(anim)
            showAddDialog()
        }
    }

    private fun setupVoiceButton() {
        binding.fabVoice.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) {
                startVoiceRecognition()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startVoiceRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, R.string.voice_not_available, Toast.LENGTH_SHORT).show()
            return
        }

        showListeningUI()

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                binding.tvListeningStatus.text = getString(R.string.listening)
            }

            override fun onBeginningOfSpeech() {
                binding.tvListeningStatus.text = getString(R.string.listening_active)
                binding.voicePulse.visibility = View.VISIBLE
            }

            override fun onRmsChanged(rmsdB: Float) {
                val scale = 1f + (rmsdB / 10f).coerceIn(0f, 1f)
                binding.voicePulse.scaleX = scale
                binding.voicePulse.scaleY = scale
            }

            override fun onEndOfSpeech() {
                binding.tvListeningStatus.text = getString(R.string.processing)
            }

            override fun onError(error: Int) {
                hideListeningUI()
                val msg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> getString(R.string.voice_no_match)
                    SpeechRecognizer.ERROR_NETWORK -> getString(R.string.voice_network_error)
                    else -> getString(R.string.voice_error)
                }
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                hideListeningUI()
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    processVoiceInput(matches[0])
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    binding.tvListeningStatus.text = "\"${partial[0]}\"..."
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer?.startListening(intent)
    }

    private fun processVoiceInput(text: String) {
        val parsed = VoiceParser.parse(text)
        val task = VoiceParser.toTask(parsed)

        val dialog = TaskDialogFragment.newInstance(task) { editedTask ->
            viewModel.insertTask(editedTask)
            Snackbar.make(binding.root, R.string.task_created_voice, Snackbar.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "edit_voice_task")
    }

    private fun showListeningUI() {
        binding.listeningOverlay.visibility = View.VISIBLE
        binding.listeningOverlay.alpha = 0f
        binding.listeningOverlay.animate().alpha(1f).setDuration(300).start()

        binding.btnCancelListening.setOnClickListener {
            speechRecognizer?.stopListening()
            hideListeningUI()
        }
    }

    private fun hideListeningUI() {
        binding.listeningOverlay.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction { binding.listeningOverlay.visibility = View.GONE }
            .start()
        binding.voicePulse.scaleX = 1f
        binding.voicePulse.scaleY = 1f
    }

    private fun setupFilterChips() {
        val chipMap = mapOf(
            binding.chipAll to TaskFilter.ALL,
            binding.chipPending to TaskFilter.PENDING,
            binding.chipToday to TaskFilter.TODAY,
            binding.chipOverdue to TaskFilter.OVERDUE,
            binding.chipWeek to TaskFilter.THIS_WEEK,
            binding.chipCompleted to TaskFilter.COMPLETED
        )

        chipMap.forEach { (chip, filter) ->
            chip.setOnClickListener {
                viewModel.setFilter(filter)
                updateChipSelection(chip)
            }
        }
    }

    private fun updateChipSelection(selectedChip: Chip) {
        listOf(
            binding.chipAll, binding.chipPending, binding.chipToday,
            binding.chipOverdue, binding.chipWeek, binding.chipCompleted
        ).forEach { chip ->
            chip.isChecked = chip == selectedChip
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.search("")
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
            R.color.primary, R.color.accent, R.color.success
        )
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.setFilter(TaskFilter.ALL)
            updateChipSelection(binding.chipAll)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun observeData() {
        viewModel.tasks.observe(this) { tasks ->
            adapter.submitList(tasks)
            binding.emptyState.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE

            if (tasks.isEmpty()) {
                val anim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                binding.emptyState.startAnimation(anim)
            }
        }

        viewModel.pendingCount.observe(this) { count ->
            binding.tvPendingCount.text = resources.getQuantityString(
                R.plurals.pending_tasks_count, count, count
            )
        }

        viewModel.overdueCount.observe(this) { count ->
            binding.tvOverdueCount.visibility = if (count > 0) View.VISIBLE else View.GONE
            binding.tvOverdueCount.text = resources.getQuantityString(
                R.plurals.overdue_tasks_count, count, count
            )
        }
    }

    private fun showAddDialog() {
        val dialog = TaskDialogFragment.newInstance { task ->
            viewModel.insertTask(task)
            Snackbar.make(binding.root, R.string.task_created, Snackbar.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "add_task")
    }

    private fun showEditDialog(task: Task) {
        val dialog = TaskDialogFragment.newInstance(task) { editedTask ->
            viewModel.updateTask(editedTask)
            Snackbar.make(binding.root, R.string.task_updated, Snackbar.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "edit_task")
    }

    private fun showTaskOptions(task: Task) {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle(task.title)
            .setItems(arrayOf(
                getString(R.string.edit),
                getString(if (task.isCompleted) R.string.mark_pending else R.string.mark_complete),
                getString(R.string.delete)
            )) { _, which ->
                when (which) {
                    0 -> showEditDialog(task)
                    1 -> viewModel.toggleComplete(task)
                    2 -> {
                        viewModel.deleteTask(task)
                        showUndoSnackbar(task)
                    }
                }
            }
            .show()
    }

    private fun showUndoSnackbar(task: Task) {
        Snackbar.make(binding.root, R.string.task_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                viewModel.insertTask(task)
            }
            .setActionTextColor(ContextCompat.getColor(this, R.color.accent))
            .show()
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        super.onDestroy()
    }
}
