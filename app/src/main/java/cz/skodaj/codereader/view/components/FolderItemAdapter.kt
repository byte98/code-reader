package cz.skodaj.codereader.view.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.skodaj.codereader.R
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.messages.FolderItemSelectedMessage
import cz.skodaj.codereader.viewmodel.data.FolderItem

/**
 * Class which connects items in folder with its view.
 */
class FolderItemAdapter(
    /**
     * List of displayed items.
     */
    private val itemList: List<FolderItem>,

    /**
     * Index of selected position.
     */
    private var selectedPosition: Int = RecyclerView.NO_POSITION
): RecyclerView.Adapter<FolderItemHolder>() {

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderItemHolder {
        return FolderItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.widget_folder_row, parent, false))
    }

    public override fun getItemCount(): Int {
        return this.itemList.size
    }

    public override fun onBindViewHolder(holder: FolderItemHolder, position: Int) {
        holder.viewData(this.itemList.get(position))
        holder.itemView.setOnClickListener {
            val prev: Int = this.selectedPosition
            this.selectedPosition = holder.adapterPosition
            this.notifyItemChanged(prev)
            this.notifyItemChanged(this.selectedPosition)
            Messenger.default.send(FolderItemSelectedMessage(this.itemList.get(this.selectedPosition)))
        }
        holder.itemView.isSelected = (position == this.selectedPosition)
    }
}