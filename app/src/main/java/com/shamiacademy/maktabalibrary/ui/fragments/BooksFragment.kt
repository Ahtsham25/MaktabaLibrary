package com.shamiacademy.maktabalibrary.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.adapter.BookGridAdapter
import com.shamiacademy.maktabalibrary.data.AppDatabase
import com.shamiacademy.maktabalibrary.data.FlatBook
import com.shamiacademy.maktabalibrary.data.LibraryRepository
import com.shamiacademy.maktabalibrary.ui.BookOptionsActivity
import kotlinx.coroutines.launch

class BooksFragment : Fragment(R.layout.fragment_books) {

    private var allBooks: List<FlatBook> = emptyList()
    private lateinit var adapter: BookGridAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_books)
        val search = view.findViewById<android.widget.EditText>(R.id.edit_search)
        val progress = view.findViewById<android.widget.ProgressBar>(R.id.progress)

        adapter = BookGridAdapter(emptyList(), emptyMap(), viewLifecycleOwner.lifecycleScope) { flat ->
            BookOptionsActivity.start(requireContext(), flat)
        }
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        recycler.adapter = adapter

        progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            allBooks = LibraryRepository.getAllBooksFlat(requireContext())
            adapter.updateData(allBooks)
            progress.visibility = View.GONE
            refreshDownloadedState()
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim().orEmpty()
                val filtered = if (q.isEmpty()) allBooks else allBooks.filter {
                    it.book.title.contains(q, ignoreCase = true) ||
                        it.maktabaNameUr.contains(q, ignoreCase = true)
                }
                adapter.updateData(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        refreshDownloadedState()
    }

    private fun refreshDownloadedState() {
        viewLifecycleOwner.lifecycleScope.launch {
            val downloaded = AppDatabase.get(requireContext()).downloadedBookDao().getAllOnce()
            adapter.updateDownloadedMap(downloaded.associate { it.uid to it.localFilePath })
        }
    }
}
