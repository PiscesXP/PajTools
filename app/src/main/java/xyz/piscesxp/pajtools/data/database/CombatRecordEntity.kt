package xyz.piscesxp.pajtools.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import xyz.piscesxp.pajtools.data.record.RecordData

@Entity
data class CombatRecordEntity(
    @PrimaryKey val id: Int,
    val isBackupLocal: Boolean,
    val isBackupRemote:Boolean,
    val recordData: RecordData
) {}