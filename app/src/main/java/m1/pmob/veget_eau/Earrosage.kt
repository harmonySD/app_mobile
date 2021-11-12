package m1.pmob.veget_eau

import android.icu.util.DateInterval
import androidx.annotation.NonNull
import androidx.room.*
import java.util.Date

@Entity(tableName = "ARROSAGE",
    foreignKeys =[
        ForeignKey( entity = Eplante::class,
                parentColumns = ["id"],
                childColumns = ["idp"]
        )
    ],primaryKeys = ["id","idp"],
)

data class Earrosage(val id:Int
                     , @ColumnInfo(index = true)val idp:Int,
                     @NonNull val type:Typearros,
                     @NonNull val interval : Int,
                     @NonNull val deb : Date,
                     @NonNull  val fin : Date
)
