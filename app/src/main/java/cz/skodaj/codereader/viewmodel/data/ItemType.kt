package cz.skodaj.codereader.viewmodel.data

/**
 * Enumeration of all types of items which can be saved in the folder.
 */
enum class ItemType {

    /**
     * Item is another folder.
     */
    FOLDER,

    /**
     * Item is saved code.
     */
    CODE,

    /**
     * Item is saved script.
     */
    SCRIPT
}