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

data class Earrosage(@ColumnInfo(index= true)val id:Long =0 // id = 0 comme ça SQL lite fait le travail
                     ,@ColumnInfo(index= true)val idp:Long ,
                     @NonNull val type:Typearros,
                     @NonNull val interval : Int,
                     @NonNull val deb : Date,
                     @NonNull  val fin : Date
)
