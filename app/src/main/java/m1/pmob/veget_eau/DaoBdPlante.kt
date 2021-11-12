package m1.pmob.veget_eau

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DaoBdPlante {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun ajoutPlante(vararg p :Eplante):List<Long>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun ajoutArros(vararg arros :Earrosage):List<Long>

    @Delete
    fun supprPlante(vararg p :Eplante):Int
    @Delete
    fun supprArros(vararg p :Earrosage):Int

    @Update
    fun modifPlante(vararg p :Eplante):Int

    @Update
    fun modifArros(vararg p :Earrosage):Int

    @Query("SELECT * FROM PLANTE")
    fun getAllPlants():LiveData<List<Eplante>>

    @Query("SELECT * FROM ARROSAGE WHERE idp = :idrch ")
    fun getPlantArros(idrch :Int):LiveData<Earrosage>

    @Query("SELECT * FROM PLANTE WHERE (nomscient like :nom || '%') OR (nomverna like :nom || '%')")
    fun loadPartialName(nom: String): List<Eplante>

}