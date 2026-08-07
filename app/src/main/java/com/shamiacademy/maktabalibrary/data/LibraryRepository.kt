package com.shamiacademy.maktabalibrary.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * Single source of truth for the library's data.
 *
 * IMPORTANT: replace RAW_JSON_URL below with the raw.githubusercontent.com
 * link to data/authors.json once this repo is pushed to GitHub, e.g.
 * https://raw.githubusercontent.com/<user>/<repo>/main/data/authors.json
 */
object LibraryRepository {

    private const val RAW_JSON_URL =
        "https://raw.githubusercontent.com/REPLACE_ME/REPLACE_ME/main/data/authors.json"

    private val client = OkHttpClient()
    private var cache: List<Maktaba>? = null

    private fun cacheFile(context: Context) = File(context.filesDir, "authors_cache.json")

    suspend fun getMaktabas(context: Context, forceRefresh: Boolean = false): List<Maktaba> {
        cache?.let { if (!forceRefresh) return it }

        return withContext(Dispatchers.IO) {
            val json = try {
                val request = Request.Builder().url(RAW_JSON_URL).build()
                client.newCall(request).execute().use { resp ->
                    if (resp.isSuccessful) {
                        val body = resp.body?.string()
                        if (!body.isNullOrBlank()) {
                            cacheFile(context).writeText(body)
                            body
                        } else null
                    } else null
                }
            } catch (e: Exception) {
                null
            } ?: run {
                // network failed — fall back to last cached copy on disk, if any
                val f = cacheFile(context)
                if (f.exists()) f.readText() else null
            }

            val list: List<Maktaba> = if (json != null) {
                val type = object : TypeToken<List<Maktaba>>() {}.type
                Gson().fromJson(json, type)
            } else {
                emptyList()
            }
            cache = list
            list
        }
    }

    suspend fun getAllBooksFlat(context: Context): List<FlatBook> {
        return getMaktabas(context).flatMap { m ->
            m.books.map { b -> FlatBook(m.nameUr, m.id, m.coverImageUrl, b) }
        }
    }
}
