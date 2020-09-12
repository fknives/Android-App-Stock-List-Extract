package org.fknives.rstocklist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BindingViewHolder<Binding : ViewBinding>(
    val binding: Binding
) : RecyclerView.ViewHolder(binding.root) {

    constructor(
        parent: ViewGroup,
        howToBind: (LayoutInflater, ViewGroup, Boolean) -> Binding
    ) : this(howToBind(LayoutInflater.from(parent.context), parent, false))
}