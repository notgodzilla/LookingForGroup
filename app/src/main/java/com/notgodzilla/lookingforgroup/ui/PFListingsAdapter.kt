package com.notgodzilla.lookingforgroup.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.notgodzilla.lookingforgroup.databinding.PfListingItemBinding

class PFListingsAdapter(private val listings: List<String>) :
    RecyclerView.Adapter<PFListingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PFListingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PfListingItemBinding.inflate(inflater, parent, false)
        return PFListingViewHolder(binding)
    }

    override fun getItemCount(): Int = listings.size

    override fun onBindViewHolder(holder: PFListingViewHolder, position: Int) {
        val item = listings[position]
        holder.bind(item)
    }
}

class PFListingViewHolder(private val binding: PfListingItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(text: String) {
        binding.pfListingText.text = text

    }

}