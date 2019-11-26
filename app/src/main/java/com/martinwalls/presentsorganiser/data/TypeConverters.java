package com.martinwalls.presentsorganiser.data;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class TypeConverters {

    @TypeConverter
    public static String fromDate(LocalDate dateTime) {
        return dateTime == null ? null : dateTime.toString();
    }

    @TypeConverter
    public static LocalDate toDate(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    // see https://developer.android.com/training/data-storage/room/referencing-data
}
