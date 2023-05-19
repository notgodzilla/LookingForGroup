package com.notgodzilla.lookingforgroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class XIVPFViewModel : ViewModel() {
    var listings: MutableStateFlow<MutableList<String>> = MutableStateFlow(
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
        }
    }

}