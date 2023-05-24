package cn.jailedbird.arouter.ksp

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.SerializationService
import com.alibaba.fastjson.JSON
import java.lang.reflect.Type


// If you need to pass a custom object, Create a new class(Not the custom object class),implement the SerializationService, And use the @Route annotation annotation, E.g:
@Route(path = "/yourservicegroupname/json")
class JsonServiceImpl : SerializationService {
    override fun init(context: Context?) {}
    override fun <T> json2Object(text: String?, clazz: Class<T>?): T {
        return JSON.parseObject(text, clazz)
    }

    override fun object2Json(instance: Any?): String {
        return JSON.toJSONString(instance)
    }

    override fun <T : Any?> parseObject(input: String?, clazz: Type?): T {
        return JSON.parseObject(input, clazz)
    }
}