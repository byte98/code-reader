package cz.skodaj.codereader.utils

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Class which represents utility class to work with data sizes.
 */
class DataSize {

// <editor-fold defaultstate="collapsed" desc="Output string constants">

    /**
     * Character marking byte.
     */
    private val byteChar: String = "B"

    /**
     * Array with prefixes for decimal representation.
     */
    private val decPrefixes: Array<String> = arrayOf(
        "", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q"
    )

    /**
     * Array with prefixes for binary representation.
     */
    private val binaryPrefixes: Array<String> = arrayOf(
        "", "Ki", "Mi", "Gi", "Ti", "Pi", "Ei", "Zi", "Yi", "Ri", "Qi"
    )

// </editor-fold>

    /**
     * Size of data in bytes.
     */
    private val size: Double

    /**
     * Size of data as string with decimal number.
     */
    private lateinit var decSize: String

    /**
     * Size of data as string with binary number.
     */
    private lateinit var binSize: String

    /**
     * Size of data as string with combined decimal and binary size.
     */
    private lateinit var combSize: String

    /**
     * Creates new size of data.
     * @param size Size of data in bytes.
     */
    public constructor(size: Double){
        this.size = size
        this.initDecimal()
        this.initBinary()
        this.combSize = "${this.decSize} (${this.binSize})"
    }

    /**
     * Initializes decimal representation of size.
     */
    private fun initDecimal(){
        val coeff: Int = this.findCoeff(10.0, 3.0, this.decPrefixes.size)
        val value: Double = ((this.size / 10.0.pow(coeff.toDouble() * 3.0)) * 100).roundToLong().toDouble() / 100.0
        this.decSize = "${value} ${this.decPrefixes[coeff]}${this.byteChar}"
    }

    /**
     * Initializes binary representation of size.
     */
    private fun initBinary(){
        val coeff: Int = this.findCoeff(2.0, 10.0, this.binaryPrefixes.size)
        val value: Double = ((this.size / 2.0.pow(coeff.toDouble() * 10.0)) * 100).roundToLong().toDouble() / 100.0
        this.binSize = "${value} ${this.binaryPrefixes[coeff]}${this.byteChar}"
    }

    /**
     * Finds coefficient for unit of data size.
     * @param base Base used to determine unit of data size (usually 2.0 or 10.0).
     * @param exp  Step of exponent used to find unit of data size.
     * @param max  Maximum of expected output.
     * @return Integer representing coefficient for unit of data size.
     */
    private fun findCoeff(base: Double, exp: Double, max: Int): Int{
        var reti: Int = 0
        if (this.size >= 1){
            while (reti < max){
                val lower: Double = base.pow(reti.toDouble() * exp)
                val upper: Double = base.pow((reti.toDouble() + 1) * exp)
                if (this.size >= lower && this.size < upper){
                    break
                }
                reti++
            }
        }
        return reti
    }

    /**
     * Gets string with decimal representation of data size.
     * @return String with decimal representation of data size.
     */
    public fun getDecimal(): String{
        return this.decSize
    }

    /**
     * Gets string with binary representation of data size.
     * @return String with binary representation of data size.
     */
    public fun getBinary(): String{
        return this.binSize
    }

    /**
     * Gets string with combined representation of data size (both decimal and binary).
     * @return String with combined representation of data size.
     */
    public fun getCombined(): String{
        return this.combSize
    }

    /**
     * Gets size of data in bytes.
     * @return Size of data in bytes.
     */
    public fun getSize(): Double{
        return this.size
    }
}