package edu.gwu.zhihongliang.findacat.model.schema.adapter

import com.squareup.moshi.*
import java.lang.reflect.Type


class ObjectAsListJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? {
        if (!List::class.java.isAssignableFrom(Types.getRawType(type))) {
            return null
        }
        val listDelegate: JsonAdapter<List<Any>> = moshi.nextAdapter(this, type, annotations)
        val innerType = Types.collectionElementType(type, List::class.java)
        val objectDelegate: JsonAdapter<Any> = moshi.adapter(innerType, annotations)
        return ListJsonAdapter(listDelegate, objectDelegate)
    }

    inner class ListJsonAdapter<T>(private val listDelegate: JsonAdapter<List<T>>,
                                   private val objectDelegate: JsonAdapter<T>) : JsonAdapter<List<T>>() {

        override fun fromJson(reader: JsonReader): List<T>? {
            if (reader.peek() == JsonReader.Token.BEGIN_OBJECT) {
                objectDelegate.fromJson(reader)?.let { return arrayListOf(it) } ?: return null
            } else {
                return listDelegate.fromJson(reader)
            }
        }

        override fun toJson(writer: JsonWriter, value: List<T>?) {
            listDelegate.toJson(writer, value)
        }
    }
}