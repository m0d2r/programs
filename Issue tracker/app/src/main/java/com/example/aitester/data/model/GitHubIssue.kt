package com.example.aitester.data.model

import android.os.Parcel
import android.os.Parcelable

data class GitHubIssue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String,
    val state: String,
    val createdAt: String,
    val updatedAt: String,
    val user: GitHubUser,
    val comments: Int,
    val labels: List<GitHubLabel>,
    val htmlUrl: String
) : Parcelable {
    @Suppress("DEPRECATION")
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        number = parcel.readInt(),
        title = parcel.readString() ?: "",
        body = parcel.readString() ?: "",
        state = parcel.readString() ?: "",
        createdAt = parcel.readString() ?: "",
        updatedAt = parcel.readString() ?: "",
        user = parcel.readParcelable(GitHubUser::class.java.classLoader) ?: GitHubUser("", ""),
        comments = parcel.readInt(),
        labels = parcel.createTypedArrayList(GitHubLabel) ?: emptyList(),
        htmlUrl = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeInt(number)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeString(state)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeParcelable(user, flags)
        parcel.writeInt(comments)
        parcel.writeTypedList(labels)
        parcel.writeString(htmlUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GitHubIssue> {
        override fun createFromParcel(parcel: Parcel): GitHubIssue = GitHubIssue(parcel)
        override fun newArray(size: Int): Array<GitHubIssue?> = arrayOfNulls(size)
    }
}

data class GitHubUser(
    val login: String,
    val avatarUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        login = parcel.readString() ?: "",
        avatarUrl = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(login)
        parcel.writeString(avatarUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GitHubUser> {
        override fun createFromParcel(parcel: Parcel): GitHubUser = GitHubUser(parcel)
        override fun newArray(size: Int): Array<GitHubUser?> = arrayOfNulls(size)
    }
}

data class GitHubLabel(
    val id: Long,
    val name: String,
    val color: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        name = parcel.readString() ?: "",
        color = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(color)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GitHubLabel> {
        override fun createFromParcel(parcel: Parcel): GitHubLabel = GitHubLabel(parcel)
        override fun newArray(size: Int): Array<GitHubLabel?> = arrayOfNulls(size)
    }
}
