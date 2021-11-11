package m1.pmob.veget_eau
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "PLANTE",
)
data class Eplante(
        @PrimaryKey(autoGenerate = true)val id:Int=0,
        @NonNull @ColumnInfo(defaultValue = "") val nomverna:String,
        @NonNull @ColumnInfo(defaultValue = "") val nomscient:String,
        @NonNull @ColumnInfo(defaultValue = "'@drawable/tokenplant.jpg'") val uri: String?
    ){
}
