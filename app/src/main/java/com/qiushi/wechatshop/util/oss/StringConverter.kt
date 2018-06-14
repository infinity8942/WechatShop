package com.qiushi.wechatshop.util.oss

import com.google.gson.*
import java.lang.reflect.Type

class StringConverter : JsonSerializer<String>,
        JsonDeserializer<String> {
    override fun serialize(src: String?, typeOfSrc: Type,
                           context: JsonSerializationContext): JsonElement {
        return if (src == null) {
            JsonPrimitive("")
        } else {
            JsonPrimitive(src)
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type,
                             context: JsonDeserializationContext): String {
        return json.asJsonPrimitive.asString
    }
}