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
import com.notgodzilla.lookingforgroup.databinding.PfListingTrackerFragmentBinding
import kotlinx.coroutines.launch
import org.jsoup.nodes.Element

class PFTrackerFragment : Fragment() {

    private var _binding: PfListingTrackerFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private val viewModel: XIVPFViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PfListingTrackerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.listingElements.collect {
                    //TODO Temporarily doing this to filter out my own PF
                    it.forEach { e ->
                        if (e.getElementsByClass("item creator")
                                .text() == "Herja Avagnar @ Malboro"
                        ) {
                            createPFTracker(e)
                        }
                    }

                }
            }
        }
    }

    private fun createPFTracker(pf: Element) {

        val listing = viewModel.mapToPFListing(pf)
        binding.dutyTitleText.text = listing.duty
        binding.pfAuthorText.text = listing.author
        binding.description.text = listing.description
        binding.worldText.text = listing.world
        binding.slotsFilledText.text = listing.slotsFilledText
        binding.timeRemainingText.text = listing.timeRemaining

    }
}