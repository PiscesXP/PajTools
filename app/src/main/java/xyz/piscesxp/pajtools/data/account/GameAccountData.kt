package xyz.piscesxp.pajtools.data.account

import xyz.piscesxp.pajtools.data.record.RecordData
import xyz.piscesxp.pajtools.utility.GameSourceType

data class GameAccountData(
    val gameSourceType: GameSourceType,
    val accountName: String,
    val recordList: List<Record>
) {
    /**
     * @return 游戏中的录像个数
     * */
    fun getLocalRecordCount(): Int {
        var count = 0
        recordList.forEach { record -> if (record.isLocal) ++count }
        return count
    }

    /**
     * @return 已备份的录像个数
     * */
    fun getBackupRecordCount(): Int {
        var count = 0
        recordList.forEach { record -> if (record.isBackup) ++count }
        return count
    }

    /**
     * @return 原始数据
     * */
    fun getRecordDataList(): List<RecordData> {
        val recordDataList = mutableListOf<RecordData>()
        for (record in recordList) {
            recordDataList.add(record.recordData)
        }
        return recordDataList
    }
}

data class Record(
    val recordData: RecordData,
    val isLocal: Boolean,
    val isBackup: Boolean
) {
    /**
     * app内使用的id，与录像的guid相同
     * */
    fun getID(): String {
        return recordData.guid
    }

}