package com.shamiacademy.maktabalibrary.data

import com.google.gson.annotations.SerializedName

data class Maktaba(
    @SerializedName("id") val id: String,
    @SerializedName("name_ur") val nameUr: String,
    @SerializedName("archive_identifier") val archiveIdentifier: String,
    @SerializedName("cover_image_url") val coverImageUrl: String,
    @SerializedName("book_count") val bookCount: Int,
    @SerializedName("books") val books: List<Book>
)

data class Book(
    @SerializedName("title") val title: String,
    @SerializedName("file_slug") val fileSlug: String,
    @SerializedName("online_view_url") val onlineViewUrl: String,
    @SerializedName("pdf_download_url") val pdfDownloadUrl: String
) {
    // Composite key: unique across the whole library
    val uid: String get() = "$onlineViewUrl"
}

/**
 * Flat wrapper used by the "All Books" tab, carrying the parent maktaba name
 * alongside each book so the grid can show author context if needed.
 */
data class FlatBook(
    val maktabaNameUr: String,
    val maktabaId: String,
    val maktabaCoverUrl: String,
    val book: Book
)
