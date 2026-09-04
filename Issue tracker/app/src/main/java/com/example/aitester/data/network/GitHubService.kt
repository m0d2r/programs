package com.example.aitester.data.network

import com.example.aitester.data.model.GitHubComment
import com.example.aitester.data.model.GitHubIssue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class GitHubService(
    private val owner: String,
    private val repo: String
) {

    companion object {
        private const val BASE_URL = "https://api.github.com"
    }

    suspend fun getIssues(state: String = "all"): Result<List<GitHubIssue>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/repos/$owner/$repo/issues?state=$state&sort=updated&per_page=50")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            connection.setRequestProperty("User-Agent", "AITESTER-App")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val responseCode = connection.responseCode
            when (responseCode) {
                403 -> return@withContext Result.failure(Exception("API rate limit - zkuste později"))
                404 -> return@withContext Result.failure(Exception("Repo nenalezeno"))
                else -> if (responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext Result.failure(Exception("GitHub API chyba: $responseCode"))
                }
            }

            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            val issues = parseIssues(response)
            Result.success(issues)
        } catch (e: Exception) {
            Result.failure(Exception("Chyba připojení: ${e.message}"))
        }
    }

    suspend fun getIssueComments(issueNumber: Int): Result<List<GitHubComment>> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL/repos/$owner/$repo/issues/$issueNumber/comments?per_page=100")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            connection.setRequestProperty("User-Agent", "AITESTER-App")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            val responseCode = connection.responseCode
            if (responseCode == 403) {
                return@withContext Result.failure(Exception("API rate limit - zkuste později"))
            }
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(Exception("GitHub API chyba: $responseCode"))
            }

            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            val comments = parseComments(response)
            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(Exception("Chyba připojení: ${e.message}"))
        }
    }

    private fun parseIssues(json: String): List<GitHubIssue> {
        val issues = mutableListOf<GitHubIssue>()
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            if (obj.has("pull_request") && !obj.isNull("pull_request")) continue

            val labels = mutableListOf<com.example.aitester.data.model.GitHubLabel>()
            val labelsArray = obj.optJSONArray("labels") ?: JSONArray()
            for (j in 0 until labelsArray.length()) {
                val labelObj = labelsArray.getJSONObject(j)
                labels.add(
                    com.example.aitester.data.model.GitHubLabel(
                        id = labelObj.getLong("id"),
                        name = labelObj.getString("name"),
                        color = labelObj.getString("color")
                    )
                )
            }

            val userObj = obj.getJSONObject("user")
            val user = com.example.aitester.data.model.GitHubUser(
                login = userObj.getString("login"),
                avatarUrl = userObj.getString("avatar_url")
            )

            issues.add(
                GitHubIssue(
                    id = obj.getLong("id"),
                    number = obj.getInt("number"),
                    title = obj.getString("title"),
                    body = obj.optString("body", ""),
                    state = obj.getString("state"),
                    createdAt = obj.getString("created_at"),
                    updatedAt = obj.getString("updated_at"),
                    user = user,
                    comments = obj.getInt("comments"),
                    labels = labels,
                    htmlUrl = obj.getString("html_url")
                )
            )
        }
        return issues
    }

    private fun parseComments(json: String): List<GitHubComment> {
        val comments = mutableListOf<GitHubComment>()
        val jsonArray = JSONArray(json)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val userObj = obj.getJSONObject("user")
            val user = com.example.aitester.data.model.GitHubUser(
                login = userObj.getString("login"),
                avatarUrl = userObj.getString("avatar_url")
            )

            comments.add(
                GitHubComment(
                    id = obj.getLong("id"),
                    body = obj.getString("body"),
                    user = user,
                    createdAt = obj.getString("created_at"),
                    updatedAt = obj.getString("updated_at")
                )
            )
        }
        return comments
    }
}
