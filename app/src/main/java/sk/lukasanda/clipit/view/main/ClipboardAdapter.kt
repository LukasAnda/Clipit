package sk.lukasanda.clipit.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_recycler.view.clipboard
import kotlinx.android.synthetic.main.item_recycler.view.date
import kotlinx.android.synthetic.main.item_recycler.view.group
import kotlinx.android.synthetic.main.item_recycler.view.order
import sk.lukasanda.clipit.R
import sk.lukasanda.clipit.data.db.entity.ClipboardEntry
import sk.lukasanda.clipit.utils.createChipFromCategory
import sk.lukasanda.clipit.view.main.ClipboardAdapter.ViewHolder

class ClipboardAdapter(
    private val items: MutableList<ClipboardEntry> = mutableListOf(),
    private val onRemoveListener: (ClipboardEntry) -> Unit,
    private val listener: (ClipboardEntry) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, items[position], listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, clipboardEntry: ClipboardEntry, listener: (ClipboardEntry) -> Unit) {
            itemView.order.text = "#${position + 1}"
            itemView.clipboard.text = clipboardEntry.clipboard
            itemView.date.text = clipboardEntry.createdAt.toString("dd.MM.yyyy")
            itemView.setOnClickListener {
                listener(clipboardEntry)
            }
            itemView.group.removeAllViews()

            clipboardEntry.categories.filterNotNull().forEach {
                if (it.name != "Unfiled") {
                    itemView.group.addView(
                        createChipFromCategory(
                            itemView.context,
                            it,
                            false, false
                        )
                    )
                }
            }
        }
    }

    fun setData(list: List<ClipboardEntry>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        onRemoveListener(items.removeAt(position))
    }
}