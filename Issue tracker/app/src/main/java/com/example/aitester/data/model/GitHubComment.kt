package com.example.aitester.data.model

data class GitHubComment(
    val id: Long,
    val body: String,
    val user: GitHubUser,
    val createdAt: String,
    val updatedAt: String
)
