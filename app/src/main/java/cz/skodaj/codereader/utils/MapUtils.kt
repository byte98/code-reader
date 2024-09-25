package cz.skodaj.codereader.utils

/**
 * Object which contains some utility functions to work with maps.
 */
object MapUtils {

    /**
     * Separator of data in string representing map.
     */
    private const val DataSeparator: Char = '\u001E'

    /**
     * Separator of keys in string representing map.
     */
    private const val KeySeparator: Char = '\u001F'


    /**
     * Converts map to string.
     * @param map Map which will be converted to string.
     * @return String representing data of map.
     */
    public fun toString(map: Map<String, String>): String{
        val reti: StringBuilder = StringBuilder()
        for(key: String in map.keys){
            reti.append(key)
            reti.append(this.KeySeparator)
            reti.append(map.get(key))
            reti.append(this.DataSeparator)
        }
        return reti.toString()
    }

    /**
     * Creates new map from data stored in string.
     * @param string String containing map data.
     * @return Map with data from string.
     */
    public fun fromString(string: String): Map<String, String>{
        val reti: MutableMap<String, String> = mutableMapOf<String, String>()
        if (string.length > 1){
            val records: List<String> = string.split(this.DataSeparator)
            for(record in records){
                if (record.length > 1) {
                    val data: List<String> = record.split(this.KeySeparator)
                    if (data.size == 2){
                        reti.put(data.get(1), data.get(2))
                    }
                }
            }
        }
        return reti.toMap()
    }
}