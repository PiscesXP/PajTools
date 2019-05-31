package xyz.piscesxp.pajtools.utility

/**
 * 游戏来源：网易官方、渠道服...
 * 缺：OPPO
 * */
enum class GameSourceType(val packagePostfix: String, val sourceName: String) {
    OFFICIAL("", "网易官方"),
    XIAOMI(".mi", "小米"),
    BILIBILI(".huawei", "华为"),
    SAN_LIU_LING_360(".qihoo", "360"),
    YING_YONG_BAO(".zqb", "应用宝"),
    MEIZU(".mz", "魅族"),
    VIVO(".vivo", "vivo"),
    UC(".uc", "uc"),
    KAOPU(".kaopu", "靠谱助手"),
    UNKNOWN(".wtf", "未知渠道")
    ;

    fun toPackageName(): String {
        return "com.netease.moba${this.packagePostfix}"
    }

    override fun toString(): String {
        return toPackageName()
    }

    companion object {
        fun parse(packageName: String): GameSourceType? {
            for (type in GameSourceType.values()) {
                if (type.toPackageName() === packageName) {
                    return type
                }
            }
            return null
        }
    }

}