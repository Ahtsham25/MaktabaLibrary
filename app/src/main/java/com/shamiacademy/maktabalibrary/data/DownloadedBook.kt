package com.shamiacademy.maktabalibrary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_books")
data class DownloadedBook(
    @PrimaryKey val uid: String,       // book.uid (online_view_url)
    val title: String,
    val maktabaNameUr: String,
    val localFilePath: String,
    val downloadedAt: Long = System.currentTimeMillis()
)
