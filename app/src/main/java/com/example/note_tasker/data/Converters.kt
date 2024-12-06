package com.example.note_tasker.data

import androidx.room.TypeConverter
import java.util.Date
import java.sql.Time

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }


    @TypeConverter
    fun fromTime(value: Long?): Time? {
        return value?.let { Time(it) }
    }

    @TypeConverter
    fun timeToTimestamp(time: Time?): Long? {
        return time?.time
    }
}
