package com.gloot.androidassignment.core.middlewares

import android.util.Log
import com.gloot.androidassignment.core.states.CoreState
import org.json.JSONException
import org.json.JSONObject
import org.rekotlin.Middleware
import java.lang.reflect.Modifier

//Extra butter on the pancake, pretty cool to show how the user moves in the app :)
internal val logMiddleware: Middleware<CoreState> = { dispatch, _ ->
    { next ->
        { action ->
            Log.d("Action Performed", LogUtil.jsonify(action, true).toString())
            next(action)
        }
    }
}

class LogUtil {
    companion object {
        @Throws(JSONException::class)
        fun jsonify(`object`: Any, addType: Boolean): JSONObject {
            val json = JSONObject()
            if (addType) {
                json.put("type", `object`.javaClass.name)
            }
            try {
                val fields = `object`.javaClass.declaredFields
                for (field in fields) {
                    if (field.isSynthetic
                            || Modifier.isStatic(field.modifiers)
                            || Modifier.isTransient(field.modifiers)) {
                        continue
                    }

                    field.isAccessible = true
                    val value = field.get(`object`)
                    if (value == null || isFramework(field.type)) {
                        json.put(field.name, value)
                    } else {
                        json.put(field.name, jsonify(value, false))
                    }
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            return json
        }

        private fun isFramework(objectClass: Class<*>): Boolean {
            if (objectClass.isPrimitive) {
                return true
            }
            val name = objectClass.name
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                return true
            }
            return false
        }

    }
}