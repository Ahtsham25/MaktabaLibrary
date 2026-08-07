package com.shamiacademy.maktabalibrary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedBookDao {

    @Query("SELECT * FROM downloaded_books ORDER BY downloadedAt DESC")
    fun observeAll(): Flow<List<DownloadedBook>>

    @Query("SELECT * FROM downloaded_books")
    suspend fun getAllOnce(): List<DownloadedBook>

    @Query("SELECT * FROM downloaded_books WHERE uid = :uid LIMIT 1")
    suspend fun findByUid(uid: String): DownloadedBook?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: DownloadedBook)

    @Delete
    suspend fun delete(book: DownloadedBook)

    @Query("DELETE FROM downloaded_books WHERE uid = :uid")
    suspend fun deleteByUid(uid: String)
}
