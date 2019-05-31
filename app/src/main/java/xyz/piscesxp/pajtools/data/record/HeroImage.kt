package xyz.piscesxp.pajtools.data

import android.content.res.AssetManager
import android.util.JsonReader
import java.io.InputStreamReader
import java.util.HashMap

class HeroImage() {
    companion object {
        fun getImageUrlByHeroID(heroID: Int?, assetManager: AssetManager): String? {
            if (heroID == null) return null
            val url: String? = null
            val heroImageMap = HashMap<Int, String>()
            try {
                val inputStream = assetManager.open("picture_url.json")
                val jsonReader = JsonReader(InputStreamReader(inputStream))
                jsonReader.beginObject()
                jsonReader.nextName()
                jsonReader.beginArray()
                while (jsonReader.hasNext()) {
                    jsonReader.beginObject()
                    var id = 0
                    var src = ""
                    while (jsonReader.hasNext()) {
                        val key = jsonReader.nextName()
                        when (key) {
                            "id" -> id = jsonReader.nextInt()
                            "src" -> src = jsonReader.nextString()
                        }
                    }
                    heroImageMap[id] = src
                    jsonReader.endObject()
                }
                jsonReader.endArray()
                jsonReader.endObject()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return heroImageMap[heroID]
        }
    }
}

