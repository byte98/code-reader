package cz.skodaj.codereader.view.components

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.skodaj.codereader.R
import cz.skodaj.codereader.viewmodel.data.FolderItem
import cz.skodaj.codereader.viewmodel.data.IconInfo

/**
 * Class which holds information about folder item in recycler view.
 */
class FolderItemHolder(view: View): RecyclerView.ViewHolder(view) {

    /**
     * Text view with the icon of the item.
     */
    private val icon: TextView

    /**
     * Text view with the name of the item.
     */
    private val name: TextView

    /**
     * Text view with the description of the item.
     */
    private val description: TextView

    init{
        this.icon = view.findViewById(R.id.folderRowIcon)
        this.name = view.findViewById(R.id.folderRowName)
        this.description = view.findViewById(R.id.folderRowDescription)
    }

    /**
     * Views data in the row.
     * @param icon Icon which will be displayed.
     * @param name Name which will be displayed.
     * @param description Description which will be displayed.
     */
    public fun viewData(icon: IconInfo, name: String, description: String){
        this.icon.setText(icon.char.toString())
        this.icon.setTypeface(icon.font)
        this.name.setText(name)
        this.description.setText(description)
    }

    /**
     * Views data in the row.
     * @param data Data which will be displayed.
     */
    public fun viewData(data: FolderItem){
        this.viewData(data.icon, data.name, data.description)
    }

}