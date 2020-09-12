package org.fknives.rstocklist

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.fknives.rstocklist.databinding.ItemTickerBinding

class TickerAdapter :
    ListAdapter<String, BindingViewHolder<ItemTickerBinding>>(StringDiffUtilItem()) {

    class StringDiffUtilItem : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            true

        override fun getChangePayload(oldItem: String, newItem: String): Any? =
            this

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder<ItemTickerBinding> =
        BindingViewHolder(parent, ItemTickerBinding::inflate)

    override fun onBindViewHolder(holder: BindingViewHolder<ItemTickerBinding>, position: Int) {
        holder.binding.ticker.text = getItem(position)
    }
}