package com.example.aitester

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aitester.data.model.GitHubIssue
import com.example.aitester.data.network.GitHubService
import com.example.aitester.data.preferences.PreferencesManager
import com.example.aitester.databinding.ActivityMainBinding
import com.example.aitester.ui.adapter.IssuesAdapter
import com.example.aitester.ui.detail.IssueDetailActivity
import com.example.aitester.ui.settings.SettingsActivity
import com.example.aitester.worker.IssueCheckWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gitHubService: GitHubService
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var issuesAdapter: IssuesAdapter

    private var allIssues: List<GitHubIssue> = emptyList()
    private var currentFilter: String = "open" // "open", "closed"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesManager = PreferencesManager(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupToolbar()
        setupBottomNavigation()
        setupRecyclerView()
        setupRetryButton()
        scheduleIssueCheck()

        // Initialize GitHubService with stored repo
        lifecycleScope.launch {
            val (owner, repo) = preferencesManager.getFullRepo()
            gitHubService = GitHubService(owner, repo)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload repo settings in case they changed
        lifecycleScope.launch {
            val (owner, repo) = preferencesManager.getFullRepo()
            gitHubService = GitHubService(owner, repo)
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            binding.toolbar.updatePadding(top = insets.top, left = insets.left, right = insets.right)
            binding.contentContainer.updatePadding(bottom = insets.bottom)
            binding.issuesRecycler.updatePadding(top = insets.top)
            binding.bottomNavigation.updatePadding(bottom = insets.bottom)

            windowInsets
        }
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_debug -> {
                startActivity(Intent(this, com.example.aitester.ui.debug.DebugActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun scheduleIssueCheck() {
        val workRequest = PeriodicWorkRequestBuilder<IssueCheckWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            IssueCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun setupBottomNavigation() {
        val bottomNav = binding.bottomNavigation

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    binding.toolbar.title = getString(R.string.app_name)
                    binding.homeContent.visibility = View.VISIBLE
                    binding.newsContent.visibility = View.GONE
                    true
                }
                R.id.nav_open -> {
                    binding.toolbar.title = getString(R.string.open_issues_filter)
                    binding.homeContent.visibility = View.GONE
                    binding.newsContent.visibility = View.VISIBLE
                    currentFilter = "open"
                    if (allIssues.isEmpty()) {
                        loadIssues()
                    } else {
                        filterAndDisplayIssues()
                    }
                    true
                }
                R.id.nav_closed -> {
                    binding.toolbar.title = getString(R.string.closed_title)
                    binding.homeContent.visibility = View.GONE
                    binding.newsContent.visibility = View.VISIBLE
                    currentFilter = "closed"
                    if (allIssues.isEmpty()) {
                        loadIssues()
                    } else {
                        filterAndDisplayIssues()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun filterAndDisplayIssues() {
        val filtered = when (currentFilter) {
            "open" -> allIssues.filter { it.state == "open" }
            "closed" -> allIssues.filter { it.state == "closed" }
            else -> allIssues
        }

        if (filtered.isEmpty()) {
            binding.errorLayout.visibility = View.VISIBLE
            binding.errorMessage.text = getString(R.string.no_issues)
            binding.issuesRecycler.visibility = View.GONE
        } else {
            binding.errorLayout.visibility = View.GONE
            binding.issuesRecycler.visibility = View.VISIBLE
            issuesAdapter.submitList(filtered)
            binding.issuesRecycler.postDelayed({
                binding.issuesRecycler.scheduleLayoutAnimation()
            }, 100)
        }
    }

    private fun setupRecyclerView() {
        issuesAdapter = IssuesAdapter { issue ->
            val intent = Intent(this, IssueDetailActivity::class.java)
            intent.putExtra("issue", issue)
            startActivity(intent)
            overridePendingTransitionCompat(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.issuesRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = issuesAdapter
            isNestedScrollingEnabled = true
        }
    }

    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            loadIssues()
        }
    }

    private fun loadIssues() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        binding.issuesRecycler.visibility = View.GONE

        lifecycleScope.launch {
            val result = gitHubService.getIssues("all")

            binding.loadingIndicator.visibility = View.GONE

            result.onSuccess { issues ->
                allIssues = issues

                val openCount = issues.count { it.state == "open" }
                val closedCount = issues.count { it.state == "closed" }
                binding.issuesCountText.text = getString(R.string.open_issues, openCount) + " • " + getString(R.string.closed_issues, closedCount)

                filterAndDisplayIssues()
            }.onFailure { error ->
                binding.errorLayout.visibility = View.VISIBLE
                binding.errorMessage.text = error.message ?: getString(R.string.unknown_error)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransitionCompat(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    @Suppress("DEPRECATION")
    private fun overridePendingTransitionCompat(enterAnim: Int, exitAnim: Int) {
        overridePendingTransition(enterAnim, exitAnim)
    }
}
