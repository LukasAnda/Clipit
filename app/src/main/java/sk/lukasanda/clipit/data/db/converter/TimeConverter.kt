package sk.lukasanda.clipit.data.db.converter

import androidx.room.TypeConverter
import org.joda.time.DateTime

class TimeConverter {

    @TypeConverter
    fun toString(dateTime: DateTime): String {
        return dateTime.toString()
    }

    @TypeConverter
    fun toDateTime(time: String): DateTime {
        return DateTime(time)
    }
}