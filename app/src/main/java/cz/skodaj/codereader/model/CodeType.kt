package cz.skodaj.codereader.model

import com.google.mlkit.vision.barcode.common.Barcode

/**
 * Enumeration of all known types of code.
 */
enum class CodeType {
    UNKNOWN,
    CODE_128,
    CODE_39,
    CODE_93,
    CODABAR,
    DATA_MATRIX,
    EAN_13,
    EAN_8,
    ITF,
    QR_CODE,
    UPC_A,
    UPC_E,
    PDF417,
    AZTEC;

    public override fun toString(): String {
        var reti: String = "UNKNOWN"
        when(this){
            CodeType.UNKNOWN -> reti = "UNKNOWN"
            CodeType.CODE_128 -> reti = "CODE128"
            CodeType.CODE_39 -> reti = "CODE39"
            CodeType.CODE_93 -> reti = "CODE93"
            CodeType.CODABAR -> reti = "CODABAR"
            CodeType.DATA_MATRIX -> reti = "DM"
            CodeType.EAN_13 -> reti = "EAN13"
            CodeType.EAN_8 -> reti = "EAN8"
            CodeType.ITF -> reti = "ITF"
            CodeType.QR_CODE -> reti = "QR"
            CodeType.UPC_A -> reti = "UPCA"
            CodeType.UPC_E -> reti = "UPCE"
            CodeType.PDF417 -> reti = "PDF417"
            CodeType.AZTEC -> reti = "AZTEC"
            else -> reti = "UNKNOWN"
        }
        return reti
    }

    /**
     * Gets string which can be translated.
     * @return String which should be translatable according to the actual context.
     */
    public fun toTranslatableString(): String{
        return "CT_${this.toString()}"
    }

    companion object{

        /**
         * Gets type of code from barcode data.
         * @param barcode Barcode data from which type of code will be get.
         * @return Type of code gathered from barcode data.
         */
        public fun fromBarcode(barcode: Barcode): CodeType{
            var reti: CodeType = CodeType.UNKNOWN
            val data: Int = barcode.format
            when (data){
                Barcode.FORMAT_UNKNOWN -> reti = CodeType.UNKNOWN
                Barcode.FORMAT_CODE_128 -> reti = CodeType.CODE_128
                Barcode.FORMAT_CODE_39 -> reti = CodeType.CODE_39
                Barcode.FORMAT_CODE_93 -> reti = CodeType.CODE_93
                Barcode.FORMAT_CODABAR -> reti = CodeType.CODABAR
                Barcode.FORMAT_DATA_MATRIX -> reti = CodeType.DATA_MATRIX
                Barcode.FORMAT_EAN_13 -> reti = CodeType.EAN_13
                Barcode.FORMAT_EAN_8 -> reti = CodeType.EAN_8
                Barcode.FORMAT_ITF -> reti = CodeType.ITF
                Barcode.FORMAT_QR_CODE -> reti = CodeType.QR_CODE
                Barcode.FORMAT_UPC_A -> reti = CodeType.UPC_A
                Barcode.FORMAT_UPC_E -> reti = CodeType.UPC_E
                Barcode.FORMAT_PDF417 -> reti = CodeType.PDF417
                Barcode.FORMAT_AZTEC -> reti = CodeType.AZTEC
                else -> reti = CodeType.UNKNOWN
            }
            return reti
        }

        /**
         * Parses code type from its string representation.
         * @param string String representation of code type.
         * @return Code type parsed from its string representation.
         */
        public fun fromString(string: String): CodeType{
            val input: String = string.trim().uppercase()
            var reti: CodeType = CodeType.UNKNOWN
            when(input){
                "UNKNOWN" -> reti = CodeType.UNKNOWN
                "CODE128" -> reti = CodeType.CODE_128
                "CODE39" -> reti = CodeType.CODE_39
                "CODE93" -> reti = CodeType.CODE_93
                "CODABAR" -> reti = CodeType.CODABAR
                "DM" -> reti = CodeType.DATA_MATRIX
                "EAN13" -> reti = CodeType.EAN_13
                "EAN8" -> reti = CodeType.EAN_8
                "ITF" -> reti = CodeType.ITF
                "QR" -> reti = CodeType.QR_CODE
                "UPCA" -> reti = CodeType.UPC_A
                "UPCE" -> reti = CodeType.UPC_E
                "PDF417" -> reti = CodeType.PDF417
                "AZTEC" -> reti = CodeType.AZTEC
                else -> reti = CodeType.UNKNOWN
            }
            return reti
        }
    }
}