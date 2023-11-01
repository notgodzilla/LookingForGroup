package com.notgodzilla.lookingforgroup.networking

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class XIVPFRepository {

    suspend fun getListingsByWorld(world: String): Elements? {
        return getAllListings()?.getElementsByAttributeValueMatching("data-centre", world)
    }

    private suspend fun getAllListings(): Element? {
        val doc: Document = Jsoup.connect("https://xivpf.com/listings").get()
        return doc.getElementById("listings")
    }

    suspend fun getPFByAuthor(author: String): Element? {
        val listElements = getAllListings()?.allElements
        listElements?.forEach {
            if (it.getElementsByClass("item creator").text() == author) {
                return it
            }
        }
        return null
    }


}