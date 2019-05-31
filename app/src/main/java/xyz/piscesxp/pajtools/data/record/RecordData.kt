package xyz.piscesxp.pajtools.data.record

import com.google.gson.annotations.SerializedName

data class RecordData(
    @SerializedName("match_id")
    val matchID: Int,

    @SerializedName("player_grid")
    val playerGrid: String,

    val url: String,

    @SerializedName("game_time")
    val gameTime: Int,

    val pos: Int,

    val result: Boolean,

    @SerializedName("hero_data")
    val heroData: Map<String, HeroDataDetail>,

    @SerializedName("video_version")
    val videoVersion: String,

    val guid: String,

    @SerializedName("end_time")
    val endTime: Int
) {
}

data class HeroDataDetail(
    @SerializedName("hero_id")
    val heroID: Int,

    val name: String,

    val faction: Int
) {
}