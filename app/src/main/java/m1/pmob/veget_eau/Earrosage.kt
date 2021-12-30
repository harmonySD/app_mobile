package m1.pmob.veget_eau

import android.icu.util.DateInterval
import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.util.Date

@Entity(tableName = "ARROSAGE",
    foreignKeys =[
        ForeignKey( entity = Eplante::class,
                parentColumns = ["id"],
                childColumns = ["idp"],onDelete = CASCADE
        )
    ]
)

data class Earrosage(@PrimaryKey(autoGenerate = true)val id:Long =0 // id = 0 comme ça SQL lite fait le travail
                     ,val idp:Long ,
                     @NonNull val type:Typearros,
                     @NonNull val interval : Int,
                     @NonNull val deb : Date,
                     @NonNull  val fin : Date,
                     @NonNull val isSpecial : Boolean = false // pour savoir si c'est un arrosage particulier ou non
)
