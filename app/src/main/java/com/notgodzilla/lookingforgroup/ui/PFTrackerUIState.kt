package com.notgodzilla.lookingforgroup.ui

import com.notgodzilla.lookingforgroup.model.PFListing

data class PFTrackerUIState(
    val listing: PFListing? = null,
    val slotsFilled: String = "",
    val isTracking: Boolean = false,
    val isRefreshing: Boolean = false
)