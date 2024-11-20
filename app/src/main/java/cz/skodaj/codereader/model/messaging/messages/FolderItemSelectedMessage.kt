package cz.skodaj.codereader.model.messaging.messages

import cz.skodaj.codereader.viewmodel.data.FolderItem

/**
 * Class which represents message which informs about selected folder item when
 * using recycle view with folder items.
 * @param item Item which has been selected.
 */
data class FolderItemSelectedMessage(
    val item: FolderItem
)
