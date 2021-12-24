package m1.pmob.veget_eau

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface DaoBdPlante {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun ajoutPlante(vararg p :Eplante):List<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun ajoutArros(vararg arros :Earrosage):List<Long>


    @Query("DELETE FROM PLANTE WHERE id = :id")
    fun supprPlante(id: Long):Int

    @Query("DELETE FROM ARROSAGE WHERE idp = :idp")
    fun supprAllArros(idp:Long)



    @Delete
    fun supprArros(vararg p :Earrosage):Int

    @Update
    fun modifPlante(vararg p :Eplante):Int

    @Update
    fun modifArros(vararg p :Earrosage):Int

    @Query("SELECT * FROM PLANTE")
    fun getAllPlants():LiveData<List<Eplante>>

    @Query("SELECT * FROM ARROSAGE WHERE idp = :idrch ")
    fun getPlantArros(idrch :Long):List<Earrosage>

    @Query("SELECT * FROM PLANTE WHERE (nomscient like :nom || '%') OR (nomverna like :nom || '%')")
    fun loadPartialName(nom: String): List<Eplante>

    @Query("SELECT * FROM PLANTE WHERE (id like :idp )")
    fun loadPlanteByID(idp:Long?):Eplante

    // REQUÊTES POUR METTRE A JOUR UNE PARTIE D'UNE PLANTE
    // INUTILES ??
    @Query("UPDATE PLANTE SET nomverna =:nv WHERE id = :id ")
    fun updatePlanteVerna(id: Long,nv:String){}

    @Query("UPDATE PLANTE SET nomscient =:ns WHERE id = :id ")
    fun updatePlanteScient(id: Long,ns:String){}

    @Query("UPDATE PLANTE SET uri =:uri WHERE id = :id ")
    fun updateArrosInterval(id: Long,uri:String){}

    // REQUÊTES POUR METTRE A JOUR UNE PARTIE D'UN ARROSAGE
    //INUTILES ????
/*
    @Query("UPDATE ARROSAGE SET interval =:interv WHERE id= :id AND idp = idp")
    fun updateArrosInterval(id: Long,idp:Long,interv:Int){}

    @Query("UPDATE ARROSAGE SET type =:type WHERE id= :id AND idp = idp")
    fun updateTypearros(id: Long,idp:Long,type:Typearros){}

    @Query("UPDATE ARROSAGE SET deb =:deb WHERE id= :id AND idp = idp")
    fun updateArrosDeb(id: Long,idp:Long,deb: Date)


    @Query("UPDATE ARROSAGE SET fin =:fin WHERE id= :id AND idp = idp")
    fun updateArrosFin(id: Long,idp:Long,fin: Date)

*/

}