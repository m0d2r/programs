package com.example.aitester

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aitester.data.model.GitHubIssue
import com.example.aitester.data.network.GitHubService
import com.example.aitester.databinding.ActivityMainBinding
import com.example.aitester.ui.adapter.IssuesAdapter
import com.example.aitester.ui.detail.IssueDetailActivity
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gitHubService = GitHubService()
    private lateinit var issuesAdapter: IssuesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivityIfAvailable(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupToolbar()
        setupBottomNavigation()
        setupRecyclerView()
        setupRetryButton()
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

    private fun setupBottomNavigation() {
        val bottomNav = binding.bottomNavigation

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    binding.toolbar.title = getString(R.string.app_name)
                    if (binding.newsContent.visibility == View.VISIBLE) {
                        // Animate out issues, in home
                        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
                        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                        binding.newsContent.startAnimation(fadeOut)
                        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                            override fun onAnimationStart(p0: android.view.animation.Animation?) {}
                            override fun onAnimationRepeat(p0: android.view.animation.Animation?) {}
                            override fun onAnimationEnd(p0: android.view.animation.Animation?) {
                                binding.newsContent.visibility = View.GONE
                                binding.homeContent.visibility = View.VISIBLE
                                binding.homeContent.startAnimation(fadeIn)
                            }
                        })
                    }
                    true
                }
                R.id.nav_news -> {
                    binding.toolbar.title = "Issues"
                    if (binding.homeContent.visibility == View.VISIBLE) {
                        // Animate out home, in issues
                        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
                        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                        binding.homeContent.startAnimation(fadeOut)
                        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                            override fun onAnimationStart(p0: android.view.animation.Animation?) {}
                            override fun onAnimationRepeat(p0: android.view.animation.Animation?) {}
                            override fun onAnimationEnd(p0: android.view.animation.Animation?) {
                                binding.homeContent.visibility = View.GONE
                                binding.newsContent.visibility = View.VISIBLE
                                binding.newsContent.startAnimation(fadeIn)
                            }
                        })
                    } else if (binding.newsContent.visibility == View.GONE) {
                        binding.homeContent.visibility = View.GONE
                        binding.newsContent.visibility = View.VISIBLE
                        binding.newsContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
                    }

                    if (issuesAdapter.itemCount == 0) {
                        loadIssues()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        issuesAdapter = IssuesAdapter { issue ->
            // Animate card press
            val intent = Intent(this, IssueDetailActivity::class.java)
            intent.putExtra("issue", issue)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.issuesRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = issuesAdapter
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
                if (issues.isEmpty()) {
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.errorMessage.text = "Žádné issues v repozitáři"
                } else {
                    binding.issuesRecycler.visibility = View.VISIBLE

                    val openCount = issues.count { it.state == "open" }
                    val closedCount = issues.count { it.state == "closed" }
                    binding.issuesCountText.text = "○ $openCount otevřených • ✓ $closedCount uzavřených"

                    issuesAdapter.submitList(issues)
                    // Animate items sliding in
                    binding.issuesRecycler.postDelayed({
                        binding.issuesRecycler.scheduleLayoutAnimation()
                    }, 100)
                }
            }.onFailure { error ->
                binding.errorLayout.visibility = View.VISIBLE
                binding.errorMessage.text = error.message ?: "Neznámá chyba"
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
