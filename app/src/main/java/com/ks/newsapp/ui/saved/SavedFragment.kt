package com.ks.newsapp.ui.saved

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ks.newsapp.databinding.FragmentSavedBinding

class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private val viewModel: SavedViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding = FragmentSavedBinding.inflate(inflater, container, false)
//        setHasOptionsMenu(true)
//        binding.viewModel = viewModel
//        binding.lifecycleOwner = this



        return binding.root
    }
}