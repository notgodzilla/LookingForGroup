package com.notgodzilla.lookingforgroup.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notgodzilla.lookingforgroup.model.PFListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class XIVPFViewModel : ViewModel() {
    var listings: MutableStateFlow<MutableList<String>> = MutableStateFlow(
        mutableListOf()
    )

    //TODO Temporarily doing this to filter out my own PF
    var listingElements: MutableStateFlow<MutableList<Element>> = MutableStateFlow(
        mutableListOf()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val doc: Document = Jsoup.connect("https://xivpf.com/listings").get()
            val listElements = doc.getElementById("listings")
                ?.getElementsByAttributeValueMatching("data-centre", "Dynamis")
            val items = mutableListOf<String>()
            listElements?.forEach {
                items.add(it.text())
            }
            listings.update { items }

            //TODO Temporarily doing this to filter out my own PF
            listingElements.update { listElements?.toMutableList() ?: mutableListOf() }
        }
    }

    fun mapToPFListing(listing: Element): PFListing {
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