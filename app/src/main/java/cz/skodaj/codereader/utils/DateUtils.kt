package cz.skodaj.codereader.utils

import android.content.Context
import cz.skodaj.codereader.model.preferences.DateFormat
import cz.skodaj.codereader.model.preferences.PreferencesSet
import cz.skodaj.codereader.model.preferences.TimeFormat
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

/**
 * Object which holds utility functions to works with dates and times.
 */
object DateUtils {

    /**
     * Offset of hours in numerical representation.
     */
    private const val HourOffset: Double = 0.01

    /**
     * Offset of minutes in numerical representation.
     */
    private const val MinuteOffset: Double = 0.0001

    /**
     * Offset of seconds in numerical representation.
     */
    private const val SecondOffset: Double = 0.000001

    /**
     * Offset of years in numerical representation.
     */
    private const val YearOffset: Double = 10000.0

    /**
     * Offset of months in numerical representation.
     */
    private const val MonthOffset: Double = 100.0

    /**
     * Offset of days in numerical representation.
     */
    private const val DayOffset: Double = 1.0

    /**
     * Function which takes only integer part of the double precision number.
     * @return Double precision number with only integer part.
     */
    private fun Double.int(): Double{
        return this.toInt().toDouble()
    }

    /**
     * Function which takes only part of the double precision number after decimal point.
     * @return Double precision number with removed integer part.
     */
    private fun Double.dec(): Double{
        return this - this.int()
    }

    /**
     * Moves decimal point of the number to the left side.
     * @param distance Distance by which decimal point will be moved.
     * @return Double precision number with moved decimal point.
     */
    private fun Double.left(distance: Double): Double{
        return this / (10.0).pow(distance)
    }

    /**
     * Moves decimal point of the number to the right side.
     * @param distance Distance by which decimal point will be moved.
     * @return Double precision number with moved decimal point.
     */
    private fun Double.right(distance: Double): Double{
        return this * (10.0).pow(distance)
    }

    /**
     * Converts time to numerical value.
     * @param time Time which will be converted.
     * @return Double precision number representing time.
     */
    public fun timeToDouble(time: LocalTime): Double{
        val hrs: Double = time.hour.toDouble() * this.HourOffset
        val min: Double = time.minute.toDouble() * this.MinuteOffset
        val sec: Double = time.second.toDouble() * this.SecondOffset
        return (hrs + min + sec)
    }

    /**
     * Parses time from its numerical value.
     * @param double Double precision number representing time.
     * @return Time parsed from its numerical value.
     */
    public fun timeFromDouble(double: Double): LocalTime{
        val hrs = double.dec().right(abs(log10(this.HourOffset))).int().left(2.0).dec().right(2.0).int().toInt()
        val min = double.dec().right(abs(log10(this.MinuteOffset))).int().left(2.0).dec().right(2.0).int().toInt()
        val sec = double.dec().right(abs(log10(this.SecondOffset))).int().left(2.0).dec().right(2.0).int().toInt()
        return LocalTime.of(hrs, min, sec)
    }

    /**
     * Converts date to numerical value.
     * @param date Date which will be converted.
     * @return Double precision number representing date.
     */
    public fun dateToDouble(date: LocalDate): Double{
        val day: Double = date.dayOfMonth.toDouble() * this.DayOffset
        val mon: Double = date.monthValue.toDouble() * this.MonthOffset
        val yer: Double = date.year * this.YearOffset
        return (yer + mon + day)
    }

    /**
     * Parses date from its numerical value.
     * @param double Double precision number representing date.
     * @return Date parsed from its numerical value.
     */
    public fun dateFromDouble(double: Double): LocalDate{
        val day: Int = double.right(2.0 + log10(this.DayOffset)).dec().left(2.0).int().toInt()
        val mon: Int = double.right(2.0 + log10(this.MonthOffset)).dec().left(2.0).int().toInt()
        val yer: Int = double.right(2.0 + log10(this.YearOffset)).dec().left(2.0).int().toInt()
        return LocalDate.of(yer, mon, day)
    }

    /**
     * Converts date time to numerical value.
     * @param dateTime Date time which will be converted.
     * @return Double precision number representing numerical value of passed date time.
     */
    public fun datetimeToDouble(dateTime: LocalDateTime): Double{
        return this.dateToDouble(dateTime.toLocalDate()) + this.timeToDouble(dateTime.toLocalTime())
    }

    /**
     * Parses date time from its numerical value.
     * @param double Double precision number representing date time.
     * @return Date time parsed from its numerical value.
     */
    public fun datetimeFromDouble(double: Double): LocalDateTime{
        return LocalDateTime.of(this.dateFromDouble(double), this.timeFromDouble(double))
    }

    /**
     * Formats date according to the format.
     * @param date Date which will be formatted.
     * @param format Format of output.
     * @return String representing date formatted according to the given format.
     */
    public fun formatDate(date: LocalDate, format: DateFormat): String{
        return format.format(date)
    }

    /**
     * Formats date according to the format.
     * @param date Date which will be formatted.
     * @param format Format of output.
     * @return String representing date formatted according to the given format.
     */
    public fun formatDate(date: LocalDateTime, format: DateFormat): String{
        return this.formatDate(date.toLocalDate(), format)
    }

    /**
     * Formats date according to the format.
     * @param date Date which will be formatted.
     * @param prefs Preferences which includes format of date which will be used.
     * @return String representing date formatted according to the given format.
     */
    public fun formatDate(date: LocalDateTime, prefs: PreferencesSet): String{
        return this.formatDate(date, prefs.dateFormat)
    }

    /**
     * Formats time according to the format.
     * @param time Time which will be formatted.
     * @param format Format of output.
     * @return String representing time formatted according to the given format.
     */
    public fun formatTime(time: LocalTime, format: TimeFormat): String{
        return format.format(time)
    }

    /**
     * Formats time according to the format.
     * @param time Time which will be formatted.
     * @param format Format of output.
     * @return String representing time formatted according to the given format.
     */
    public fun formatTime(time: LocalDateTime, format: TimeFormat): String{
        return this.formatTime(time.toLocalTime(), format)
    }

    /**
     * Formats time according to the format.
     * @param time Time which will be formatted.
     * @param prefs Preferences which includes format of time which will be used.
     * @return String representing time formatted according to the given format.
     */
    public fun formatTime(time: LocalDateTime, prefs: PreferencesSet): String{
        return this.formatTime(time, prefs.timeFormat)
    }

    /**
     * Formats date and time according to the format.
     * @param data Date and time which will be formatted.
     * @param date Date format which will be used to format date.
     * @param time Time format which will be used to format time.
     * @return String representing date and time formatted according to the given format.
     */
    public fun format(data: LocalDateTime, date: DateFormat, time: TimeFormat): String{
        return "${this.formatDate(data, date)} ${this.formatTime(data, time)}"
    }

    /**
     * Formats date and time according to the format.
     * @param data Date and time which will be formatted.
     * @param prefs Preferences which includes date and time format which will be used.
     * @return String representing date and time formatted according to the given preferences.
     */
    public fun format(data: LocalDateTime, prefs: PreferencesSet): String{
        return this.format(data, prefs.dateFormat, prefs.timeFormat)
    }
}