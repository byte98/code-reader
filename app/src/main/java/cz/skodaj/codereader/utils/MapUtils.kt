package cz.skodaj.codereader.utils

import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import cz.skodaj.codereader.configuration.Globals

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
                        reti.put(data.get(0), data.get(1))
                    }
                }
            }
        }
        return reti.toMap()
    }

    /**
     * Gets barcode data fields to map.
     * @param barcode Barcode which data fields will be returned.
     * @return Map with barcode data fields.
     */
    public fun barcodeToMap(barcode: Barcode): Map<String, String>{
        val reti: MutableMap<String, String> = mutableMapOf()
        if (barcode.valueType == Barcode.TYPE_CALENDAR_EVENT){
            val info: Barcode.CalendarEvent? = barcode.calendarEvent
            reti.put("description", info?.description ?: " ")
            reti.put("location", info?.location ?: " ")
            reti.put("organizer", info?.organizer ?: " ")
            reti.put("status", info?.status ?: " ")
            reti.put("summary", info?.summary ?: " ")
            reti.put("start", info?.start?.rawValue ?: " ")
            reti.put("end", info?.end?.rawValue ?: " ")
        }
        else if (barcode.valueType == Barcode.TYPE_CONTACT_INFO){
            val info: Barcode.ContactInfo? = barcode.contactInfo
            reti.put("name", info?.name?.formattedName ?: " ")
            reti.put("organization", info?.organization ?: " ")
            reti.put("title", info?.title ?: " ")
            reti.put("address", info?.addresses?.joinToString("${Globals.NEWLINE}${Globals.NEWLINE}") {
                it.addressLines.joinToString("\"${Globals.NEWLINE}")
            } ?: " ")
            reti.put("email", info?.emails?.joinToString("\"${Globals.NEWLINE}") ?: " ")
            reti.put("phone", info?.phones?.joinToString("\"${Globals.NEWLINE}") ?: " ")
            reti.put("url", info?.urls?.joinToString("\"${Globals.NEWLINE}") ?: " ")
        }
        else if (barcode.valueType == Barcode.TYPE_DRIVER_LICENSE){
            val info: Barcode.DriverLicense? = barcode.driverLicense
            reti.put("addressCity", info?.addressCity ?: " ")
            reti.put("addressState", info?.addressState ?: " ")
            reti.put("addressStreet", info?.addressStreet ?: " ")
            reti.put("addressZip", info?.addressZip ?: " ")
            reti.put("birthDate", info?.birthDate ?: " ")
            reti.put("documentType", info?.documentType ?: " ")
            reti.put("expiryDate", info?.expiryDate ?: " ")
            reti.put("firstName", info?.firstName ?: " ")
            reti.put("gender", info?.gender ?: " ")
            reti.put("issueDate", info?.issueDate ?: " ")
            reti.put("issuingCountry", info?.issuingCountry ?: " ")
            reti.put("lastName", info?.lastName ?: " ")
            reti.put("licenseNumber", info?.licenseNumber ?: " ")
            reti.put("middleName", info?.middleName ?: " ")
        }
        else if (barcode.valueType == Barcode.TYPE_EMAIL){
            val info: Barcode.Email? = barcode.email
            reti.put("address", info?.address ?: " ")
            reti.put("body", info?.body ?: " ")
            reti.put("subject", info?.subject ?: " ")
            reti.put("typeID", info?.type?.toString() ?: " ")
            when(info?.type ?: Int.MIN_VALUE){
                Barcode.Email.TYPE_HOME -> reti.put("typeName", "home")
                Barcode.Email.TYPE_WORK -> reti.put("typeName", "work")
                else -> reti.put("typeName", "unknown")
            }
        }
        else if (barcode.valueType == Barcode.TYPE_GEO){
            val info: Barcode.GeoPoint? = barcode.geoPoint
            reti.put("latitude", info?.lat?.toString() ?: " ")
            reti.put("longitude", info?.lng?.toString() ?: " ")
        }
        else if (barcode.valueType == Barcode.TYPE_PHONE){
            val info: Barcode.Phone? = barcode.phone
            reti.put("phoneNumber", info?.number ?: " ")
            reti.put("typeID", info?.type?.toString() ?: " ")
            when(info?.type ?: Int.MIN_VALUE){
                Barcode.Phone.TYPE_HOME -> reti.put("typeName", "home")
                Barcode.Phone.TYPE_WORK -> reti.put("typeName", "work")
                Barcode.Phone.TYPE_FAX -> reti.put("typeName", "fax")
                Barcode.Phone.TYPE_MOBILE -> reti.put("typeName", "mobile")
                else -> reti.put("typeName", "unknown")
            }
        }
        else if (barcode.valueType == Barcode.TYPE_SMS){
            val info: Barcode.Sms? = barcode.sms
            reti.put("phoneNumber", info?.phoneNumber ?: " ")
            reti.put("message", info?.message ?: " ")
        }
        else if (barcode.valueType == Barcode.TYPE_URL){
            val info: Barcode.UrlBookmark? = barcode.url
            reti.put("url", info?.url ?: " ")
            reti.put("title", info?.title ?: " ")
        }
        else if (barcode.valueType == Barcode.TYPE_WIFI){
            val info: Barcode.WiFi? = barcode.wifi
            reti.put("password", info?.password ?: " ")
            reti.put("ssid", info?.ssid ?: " ")
            reti.put("encryptionTypeID", info?.encryptionType?.toString() ?: " ")
            when(info?.encryptionType ?: Int.MIN_VALUE){
                Barcode.WiFi.TYPE_OPEN -> reti.put("encryptionTypeName", "open")
                Barcode.WiFi.TYPE_WEP -> reti.put("encryptionTypeName", "WEP")
                Barcode.WiFi.TYPE_WPA -> reti.put("encryptionTypeName", "WPA")
                else -> reti.put("encryptionTypeName", "unknown")
            }
        }
        return reti
    }
}