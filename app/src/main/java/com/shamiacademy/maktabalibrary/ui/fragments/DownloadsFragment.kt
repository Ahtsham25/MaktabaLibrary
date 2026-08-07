package com.shamiacademy.maktabalibrary.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shamiacademy.maktabalibrary.R
import com.shamiacademy.maktabalibrary.adapter.DownloadedAdapter
import com.shamiacademy.maktabalibrary.data.AppDatabase
import com.shamiacademy.maktabalibrary.ui.PdfReaderActivity
import kotlinx.coroutines.launch

class DownloadsFragment : Fragment(R.layout.fragment_downloads) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_downloads)
        val emptyText = view.findViewById<android.widget.TextView>(R.id.text_empty)

        val adapter = DownloadedAdapter(emptyList(), viewLifecycleOwner.lifecycleScope) { book ->
            PdfReaderActivity.start(requireContext(), book.localFilePath, book.title)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            AppDatabase.get(requireContext()).downloadedBookDao().observeAll().collect { list ->
                adapter.updateData(list)
                emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}
