package tech.diarmaid.koohiiaite.database.roomdao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.Primitive

@Dao
interface PrimitiveDao {
    @Query("SELECT * FROM primitive")
    fun allPrimitives(): LiveData<List<Primitive>>
}