package xyz.piscesxp.pajtools.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CombatRecordDao {
    @Query("SELECT * FROM combatrecordentity")
    fun getAll(): List<CombatRecordEntity>

    @Insert
    fun insert(combatRecordEntity: CombatRecordEntity)

    @Delete
    fun delete(combatRecordEntity: CombatRecordEntity)
}