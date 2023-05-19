package com.notgodzilla.lookingforgroup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.notgodzilla.lookingforgroup.databinding.PfListingFragmentBinding
import kotlinx.coroutines.launch

class PFListingsFragment : Fragment() {

    private var _binding: PfListingFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private val viewModel: XIVPFViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PfListingFragmentBinding.inflate(inflater, container, false)
        binding.pfListingsRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.pfListingsRecycler
            .addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listings.collect {
                    binding.pfListingsRecycler.adapter = PFListingsAdapter(it)
                }


            }
        }
    }
}