package com.qiushi.wechatshop.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class SPUtil {

    companion object {

        private val FILE_NAME = "wechat_shop_file"

        /**
         * 保存
         */
        fun put(context: Context, key: String, `object`: Any) {

            val sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE)
            val editor = sp.edit()

            when (`object`) {
                is String -> editor.putString(key, `object`)
                is Int -> editor.putInt(key, `object`)
                is Boolean -> editor.putBoolean(key, `object`)
                is Float -> editor.putFloat(key, `object`)
                is Long -> editor.putLong(key, `object`)
                else -> editor.putString(key, `object`.toString())
            }

            SharedPreferencesCompat.apply(editor)
        }

        operator fun get(context: Context, key: String, defaultObject: Any): Any? {
            val sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE)

            return when (defaultObject) {
                is String -> sp.getString(key, defaultObject)
                is Int -> sp.getInt(key, defaultObject)
                is Boolean -> sp.getBoolean(key, defaultObject)
                is Float -> sp.getFloat(key, defaultObject)
                is Long -> sp.getLong(key, defaultObject)
                else -> null
            }
        }

        fun remove(context: Context, key: String) {
            val sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove(key)
            SharedPreferencesCompat.apply(editor)
        }

        fun clear(context: Context) {
            val sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.clear()
            SharedPreferencesCompat.apply(editor)
        }

        fun contains(context: Context, key: String): Boolean {
            val sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE)
            return sp.contains(key)
        }

        fun getAll(context: Context): Map<String, *> {
            val sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE)
            return sp.all
        }

        private object SharedPreferencesCompat {
            private val sApplyMethod = findApplyMethod()

            private fun findApplyMethod(): Method? {
                try {
                    val clz = SharedPreferences.Editor::class.java
                    return clz.getMethod("apply")
                } catch (e: NoSuchMethodException) {
                }

                return null
            }

            fun apply(editor: SharedPreferences.Editor) {
                try {
                    if (sApplyMethod != null) {
                        sApplyMethod.invoke(editor)
                        return
                    }
                } catch (e: IllegalArgumentException) {
                } catch (e: IllegalAccessException) {
                } catch (e: InvocationTargetException) {
                }
                editor.commit()
            }
        }

        /**
         * 保存(文件名可变)
         */
        fun put(fileName: String, context: Context, key: String, `object`: Any) {

            val sp = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE)
            val editor = sp.edit()

            when (`object`) {
                is String -> editor.putString(key, `object`)
                is Int -> editor.putInt(key, `object`)
                is Boolean -> editor.putBoolean(key, `object`)
                is Float -> editor.putFloat(key, `object`)
                is Long -> editor.putLong(key, `object`)
                else -> editor.putString(key, `object`.toString())
            }

            SharedPreferencesCompat.apply(editor)
        }

        operator fun get(fileName: String, context: Context, key: String, defaultObject: Any): Any? {
            val sp = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE)

            return when (defaultObject) {
                is String -> sp.getString(key, defaultObject)
                is Int -> sp.getInt(key, defaultObject)
                is Boolean -> sp.getBoolean(key, defaultObject)
                is Float -> sp.getFloat(key, defaultObject)
                is Long -> sp.getLong(key, defaultObject)
                else -> null
            }

        }

        fun remove(fileName: String, context: Context, key: String) {
            val sp = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove(key)
            SharedPreferencesCompat.apply(editor)
        }

        fun clear(fileName: String, context: Context) {
            val sp = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.clear()
            SharedPreferencesCompat.apply(editor)
        }

        fun contains(fileName: String, context: Context, key: String): Boolean {
            val sp = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE)
            return sp.contains(key)
        }

        fun getAll(fileName: String, context: Context): Map<String, *> {
            val sp = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE)
            return sp.all
        }


        fun putObject(fileName: String, context: Context, `object`: Any?,
                      key: String): Boolean {
            val sp = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE)
            if (`object` == null) {
                val editor = sp.edit().remove(key)
                return editor.commit()
            }
            val baos = ByteArrayOutputStream()
            var oos: ObjectOutputStream?
            try {
                oos = ObjectOutputStream(baos)
                oos.writeObject(`object`)
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

            val objectStr = String(Base64.encode(baos.toByteArray(),
                    Base64.DEFAULT))
            try {
                baos.close()
                oos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val editor = sp.edit()
            editor.putString(key, objectStr)
            return editor.commit()
        }

        fun getObject(fileName: String, context: Context, key: String): Any? {
            val sharePre = context.getSharedPreferences(fileName,
                    Context.MODE_PRIVATE)
            try {
                val wordBase64 = sharePre.getString(key, "")
                // 将base64格式字符串还原成byte数组
                if (wordBase64 == null || wordBase64 == "") { // 不可少，否则在下面会报java.io.StreamCorruptedException
                    return null
                }
                val objBytes = Base64.decode(wordBase64.toByteArray(),
                        Base64.DEFAULT)
                val bais = ByteArrayInputStream(objBytes)
                val ois = ObjectInputStream(bais)
                // 将byte数组转换成product对象
                val obj = ois.readObject()
                bais.close()
                ois.close()
                return obj
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        @Throws(IOException::class)
        private fun <A> serialize(obj: A): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(
                    byteArrayOutputStream)
            objectOutputStream.writeObject(obj)
            var serStr = byteArrayOutputStream.toString("ISO-8859-1")
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
            objectOutputStream.close()
            byteArrayOutputStream.close()
            return serStr
        }

        @Suppress("UNCHECKED_CAST")
        @Throws(IOException::class, ClassNotFoundException::class)
        private fun <A> deSerialization(str: String): A {
            val redStr = java.net.URLDecoder.decode(str, "UTF-8")
            val byteArrayInputStream = ByteArrayInputStream(
                    redStr.toByteArray(charset("ISO-8859-1")))
            val objectInputStream = ObjectInputStream(
                    byteArrayInputStream)
            val obj = objectInputStream.readObject() as A
            objectInputStream.close()
            byteArrayInputStream.close()
            return obj
        }
    }
}