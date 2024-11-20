package cz.skodaj.codereader.viewmodel.data

/**
 * Class representing one single item which is saved in the folder.
 * @param id Identifier of item in database.
 * @param type Type of item.
 * @param name Name of item.
 * @param description Description of item.
 * @param icon Icon of item.
 */
data class FolderItem(

    val id: Long,
    val type: ItemType,
    val name: String,
    val description: String,
    val icon: IconInfo
)