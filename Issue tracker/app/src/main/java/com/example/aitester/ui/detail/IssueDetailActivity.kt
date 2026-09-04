package com.example.aitester.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.aitester.R
import com.example.aitester.data.model.GitHubIssue
import com.example.aitester.data.network.GitHubService
import com.example.aitester.databinding.ActivityIssueDetailBinding
import com.google.android.material.chip.Chip
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class IssueDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIssueDetailBinding
    private val gitHubService = GitHubService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivityIfAvailable(this)

        binding = ActivityIssueDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Slide in animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        setupWindowInsets()
        setupToolbar()

        val issue = intent.getParcelableExtra<GitHubIssue>("issue")
        if (issue != null) {
            displayIssue(issue)
            loadComments(issue.number)
        } else {
            finish()
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(top = insets.top)
            windowInsets
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun displayIssue(issue: GitHubIssue) {
        binding.toolbar.title = "#${issue.number}"

        // State badge
        if (issue.state == "open") {
            binding.issueState.text = "○ Open"
            binding.issueState.setBackgroundColor(getColor(R.color.md3_secondary))
        } else {
            binding.issueState.text = "✓ Closed"
            binding.issueState.setBackgroundColor(getColor(R.color.md3_error))
        }

        binding.issueTitle.text = issue.title
        binding.issueAuthor.text = issue.user.login
        binding.issueDate.text = formatDate(issue.createdAt)
        binding.issueCommentsCount.text = "${issue.comments} komentářů"

        if (issue.body.isNotBlank()) {
            binding.issueBody.visibility = View.VISIBLE
            binding.issueBody.text = issue.body
        } else {
            binding.issueBody.visibility = View.GONE
        }

        // Labels
        binding.issueLabels.removeAllViews()
        issue.labels.forEach { label ->
            val chip = Chip(this).apply {
                text = label.name
                isClickable = false
                try {
                    val color = Color.parseColor("#${label.color}")
                    chipBackgroundColor = android.content.res.ColorStateList.valueOf(color)
                    setTextColor(getContrastColor(color))
                } catch (e: Exception) {
                    chipStrokeWidth = 1f
                }
                textSize = 10f
            }
            binding.issueLabels.addView(chip)
        }
    }

    private fun loadComments(issueNumber: Int) {
        binding.commentsLoading.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = gitHubService.getIssueComments(issueNumber)

            binding.commentsLoading.visibility = View.GONE

            result.onSuccess { comments ->
                if (comments.isEmpty()) {
                    binding.commentsError.visibility = View.VISIBLE
                    binding.commentsError.text = "Žádné komentáře"
                    binding.commentsError.setTextColor(getColor(R.color.md3_on_surface_variant))
                } else {
                    displayComments(comments)
                }
            }.onFailure { error ->
                binding.commentsError.visibility = View.VISIBLE
                binding.commentsError.text = error.message
            }
        }
    }

    private fun displayComments(comments: List<com.example.aitester.data.model.GitHubComment>) {
        binding.commentsContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)

        comments.forEach { comment ->
            val view = inflater.inflate(R.layout.item_comment, binding.commentsContainer, false)
            view.findViewById<TextView>(R.id.comment_author).text = comment.user.login
            view.findViewById<TextView>(R.id.comment_date).text = formatDate(comment.createdAt)
            view.findViewById<TextView>(R.id.comment_body).text = comment.body
            binding.commentsContainer.addView(view)
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val output = SimpleDateFormat("d. MMM yyyy, HH:mm", Locale.getDefault())
            val date = input.parse(dateStr)
            date?.let { output.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr.take(10)
        }
    }

    private fun getContrastColor(color: Int): Int {
        val luminance = (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (luminance > 0.5) Color.BLACK else Color.WHITE
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
