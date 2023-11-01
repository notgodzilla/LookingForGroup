package com.notgodzilla.lookingforgroup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notgodzilla.lookingforgroup.model.PFListing
import com.notgodzilla.lookingforgroup.networking.XIVPFRepository
import com.notgodzilla.lookingforgroup.preferences.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jsoup.nodes.Element

class XIVPFViewModel : ViewModel() {

    var listings: MutableStateFlow<MutableList<String>> = MutableStateFlow(
        mutableListOf()
    )

    //TODO Inject this
    private val preferencesRepository = PreferencesRepository.get()

    //TODO Inject this
    private val repo: XIVPFRepository = XIVPFRepository()

    private var _listingState: MutableStateFlow<PFTrackerUIState> =
        MutableStateFlow(PFTrackerUIState())
    val listingState: StateFlow<PFTrackerUIState>
        get() = _listingState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val listElements = repo.getListingsByWorld("Crystal")
            val items = mutableListOf<String>()
            listElements?.forEach {
                items.add(it.text())
            }
            listings.update { items }


            //TODO Temporarily doing this to filter out my own PF
            val rawAuthorListing = repo.getPFByAuthor("Herja Avagnar @ Malboro")
            val authorListing =
                if (rawAuthorListing != null) {
                    mapToPFListing(rawAuthorListing)
                } else {
                    null
                }
            _listingState.update {
                it.copy(
                    listing = authorListing,
                    slotsFilled = authorListing?.slotsFilledText ?: "",
                    isRefreshing = false
                )
            }

        }

        viewModelScope.launch {
            preferencesRepository.isTracking.collect { isTracking ->
                _listingState.update {
                    it.copy(
                        isTracking = isTracking
                    )
                }
            }
        }
    }

    fun toggleIsTracking() {
        viewModelScope.launch {
            preferencesRepository.setTracking(!listingState.value.isTracking)
        }
    }

    private fun mapToPFListing(listing: Element): PFListing {
        //Ex: How to get slots filled
        //listElements[5].getElementsByClass("party")[0].getElementsByClass("slot filled dps")
        return PFListing(
            listing.getElementsByClass("duty cross").text() ?: "",
            listing.getElementsByClass("description").text() ?: "",
            listing.getElementsByClass("item creator").text() ?: "",
            listing.getElementsByClass("item world").text() ?: "",
            listing.getElementsByClass("total").text() ?: "",
            listing.getElementsByClass("time expires").text() ?: ""
        )
    }

}