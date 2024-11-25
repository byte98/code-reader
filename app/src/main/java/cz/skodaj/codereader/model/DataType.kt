package cz.skodaj.codereader.model

import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Enumeration of all available data types stored in codes.
 */
enum class DataType {
    UNKNOWN,
    CONTACT_INFO,
    EMAIL,
    ISBN,
    PHONE,
    PRODUCT,
    SMS,
    TEXT,
    URL,
    WIFI,
    GEO,
    CALENDAR_EVENT,
    DRIVER_LICENSE;

    public override fun toString(): String {
        var reti: String = "UNKNOWN"
        when(this){
            DataType.UNKNOWN -> reti = "UNKNOWN"
            DataType.CONTACT_INFO -> reti = "CONTACT"
            DataType.EMAIL -> reti = "EMAIL"
            DataType.ISBN -> reti = "ISBN"
            DataType.PHONE -> reti = "PHONE"
            DataType.PRODUCT -> reti = "PRODUCT"
            DataType.SMS -> reti = "SMS"
            DataType.TEXT -> reti = "TXT"
            DataType.URL -> reti = "URL"
            DataType.WIFI -> reti = "WIFI"
            DataType.GEO -> reti = "GEO"
            DataType.CALENDAR_EVENT -> reti = "CALENDAR"
            DataType.DRIVER_LICENSE -> reti = "DRIVER"
            else -> reti = "UNKNOWN"
        }
        return reti
    }

    /**
     * Gets string which can be translated.
     * @return String which should be translatable according to the actual context.
     */
    public fun toTranslatableString(): String{
        return "DT_${this.toString()}"
    }

    companion object{

        /**
         * Gets type fo data stored in code from barcode object.
         * @param barcode Object which contains code itself.
         * @return Type of data stored in code.
         */
        public fun fromBarcode(barcode: Barcode): DataType{
            var reti: DataType = DataType.UNKNOWN
            val data: Int = barcode.valueType
            when (data){
                Barcode.TYPE_UNKNOWN -> reti = DataType.UNKNOWN
                Barcode.TYPE_CONTACT_INFO -> reti = DataType.CONTACT_INFO
                Barcode.TYPE_EMAIL -> reti = DataType.EMAIL
                Barcode.TYPE_ISBN -> reti = DataType.ISBN
                Barcode.TYPE_PHONE -> reti = DataType.PHONE
                Barcode.TYPE_PRODUCT -> reti = DataType.PRODUCT
                Barcode.TYPE_SMS -> reti = DataType.SMS
                Barcode.TYPE_TEXT -> reti = DataType.TEXT
                Barcode.TYPE_URL -> reti = DataType.URL
                Barcode.TYPE_WIFI -> reti = DataType.WIFI
                Barcode.TYPE_GEO -> reti = DataType.GEO
                Barcode.TYPE_CALENDAR_EVENT -> reti = DataType.CALENDAR_EVENT
                Barcode.TYPE_DRIVER_LICENSE -> reti = DataType.DRIVER_LICENSE
                else -> reti = DataType.UNKNOWN
            }
            return reti
        }

        /**
         * Parses type of data stored in code from its string representation.
         * @param string String representation of type of data stored in code.
         * @return Type of data stored in code parsed from its string representation.
         */
        public fun fromString(string: String): DataType{
            val input: String = string.trim().uppercase()
            var reti: DataType = DataType.UNKNOWN
            when(input){
                "UNKNOWN" -> reti = DataType.UNKNOWN
                "CONTACT" -> reti = DataType.CONTACT_INFO
                "EMAIL" -> reti = DataType.EMAIL
                "ISBN" -> reti = DataType.ISBN
                "PHONE" -> reti = DataType.PHONE
                "PRODUCT" -> reti = DataType.PRODUCT
                "SMS" -> reti = DataType.SMS
                "TXT" -> reti = DataType.TEXT
                "URL" -> reti = DataType.URL
                "WIFI" -> reti = DataType.WIFI
                "GEO" -> reti = DataType.GEO
                "CALENDAR" -> reti = DataType.CALENDAR_EVENT
                "DRIVER" -> reti = DataType.DRIVER_LICENSE
                else -> reti = DataType.UNKNOWN
            }
            return reti
        }
    }

}