package com.notgodzilla.lookingforgroup.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.*
import com.notgodzilla.lookingforgroup.R
import com.notgodzilla.lookingforgroup.databinding.PfListingTrackerFragmentBinding
import com.notgodzilla.lookingforgroup.model.PFListing
import com.notgodzilla.lookingforgroup.polling.PFTracker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PFTrackerFragment : Fragment() {

    private var _binding: PfListingTrackerFragmentBinding? = null
    private val binding
        get() = checkNotNull(_binding)

    private val viewModel: XIVPFViewModel by viewModels()
    private var trackingMenuItem: MenuItem? = null

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
                viewModel.listingState.collect {
                    updateTrackingState(it.isTracking)
                    it.listing?.let { PF ->
                        createPFTracker(PF)
                    }
                }
            }
        }
    }

    private fun createPFTracker(listing: PFListing) {
        binding.dutyTitleText.text = listing.duty
        binding.pfAuthorText.text = listing.author
        binding.description.text = listing.description
        binding.worldText.text = listing.world
        binding.slotsFilledText.text = listing.slotsFilledText
        binding.timeRemainingText.text = listing.timeRemaining

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.pf_listing_tracker_fragment, menu)
        trackingMenuItem = menu.findItem(R.id.menu_item_toggle_tracking)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_toggle_tracking -> {
               viewModel.toggleIsTracking()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateTrackingState(isTracking: Boolean) {
        val toggleItemTitle = if (isTracking) {
            R.string.stop_tracking
        } else {
            R.string.start_tracking
        }

        trackingMenuItem?.setTitle(toggleItemTitle)

        if (isTracking) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
            val periodicRequest =
                PeriodicWorkRequestBuilder<PFTracker>(3, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                TRACK_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        } else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork(TRACK_WORK)
        }

    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        trackingMenuItem = null
    }

    companion object {
        private const val TRACK_WORK = "POLL_WORK"
    }

}