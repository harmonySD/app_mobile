package m1.pmob.veget_eau

import android.icu.util.DateInterval
import java.util.Date
import androidx.room.TypeConverter

class DateConverter {
/*  LA CREATION DE CETTE CLASSE A ETE EN GRANDE PARTIE INFLUENCE PAR
* LA CONSULTATION DU  LIEN CI-APRES
* https://stackoverflow.com/questions/50313525/room-using-date-field
*
* */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        if(timestamp ==null) return null
        return Date(timestamp)
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return  date?.getTime()
    }

    @TypeConverter
    fun toTimestamp(dateinterval: DateInterval?):Long?{
        if(dateinterval == null) {return null }
        return dateinterval.toDate - dateinterval.fromDate
    }
    @TypeConverter
    fun toDateInterval(timestamp:Long?):DateInterval?{
        if (timestamp == null ){return null}
        return DateInterval(0,0+timestamp)
    }
}
