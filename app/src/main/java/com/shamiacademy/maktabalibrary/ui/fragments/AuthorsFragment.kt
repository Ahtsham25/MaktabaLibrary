package com.shamiacademy.maktabalibrary.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.adapter.AuthorAdapter
import com.shamiacademy.maktabalibrary.data.LibraryRepository
import com.shamiacademy.maktabalibrary.data.Maktaba
import com.shamiacademy.maktabalibrary.ui.AuthorBooksActivity
import kotlinx.coroutines.launch

class AuthorsFragment : Fragment(R.layout.fragment_authors) {

    private var allAuthors: List<Maktaba> = emptyList()
    private lateinit var adapter: AuthorAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_authors)
        val search = view.findViewById<android.widget.EditText>(R.id.edit_search)
        val progress = view.findViewById<android.widget.ProgressBar>(R.id.progress)

        adapter = AuthorAdapter(emptyList()) { maktaba ->
            AuthorBooksActivity.start(requireContext(), maktaba.id)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            allAuthors = LibraryRepository.getMaktabas(requireContext())
            adapter.updateData(allAuthors)
            progress.visibility = View.GONE
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s?.toString()?.trim().orEmpty()
                val filtered = if (q.isEmpty()) allAuthors else allAuthors.filter {
                    it.nameUr.contains(q, ignoreCase = true)
                }
                adapter.updateData(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
