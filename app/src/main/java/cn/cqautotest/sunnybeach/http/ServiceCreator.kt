package cn.cqautotest.sunnybeach.http

import cn.cqautotest.sunnybeach.http.annotation.CaiYunClient
import cn.cqautotest.sunnybeach.http.annotation.SobClient
import cn.cqautotest.sunnybeach.ktx.unicodeToString
import cn.cqautotest.sunnybeach.manager.LocalCookieManager
import cn.cqautotest.sunnybeach.util.BASE_URL
import cn.cqautotest.sunnybeach.util.CAI_YUN_BASE_URL
import cn.cqautotest.sunnybeach.util.SUNNY_BEACH_API_BASE_URL
import com.hjq.gson.factory.GsonFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/02
 * desc   : 网络请求服务创建者
 */
object ServiceCreator {

    val sobRetrofit: Retrofit by lazy { createRetrofit { baseUrl(SUNNY_BEACH_API_BASE_URL) } }

    val caiYunRetrofit: Retrofit by lazy { createRetrofit { baseUrl(CAI_YUN_BASE_URL) } }

    val otherRetrofit: Retrofit by lazy { createRetrofit { baseUrl(BASE_URL) } }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor {
            Timber.d("===> result：${it.unicodeToString()}")
        }.also {
            it.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
    private val accountInterceptor by lazy { AccountInterceptor() }
    private val cookieManager = LocalCookieManager.get()

    val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(accountInterceptor)
            .addInterceptor(loggingInterceptor)
            .cookieJar(cookieManager)
            .build()
    }

    private fun createRetrofit(block: Retrofit.Builder.() -> Retrofit.Builder) = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
        .client(client)
        .run(block)
        .build()

    inline fun <reified T> create(): T {
        val clazz = T::class.java
        val api = when {
            clazz containsAnnotation SobClient::class.java -> sobRetrofit.create(clazz)
            clazz containsAnnotation CaiYunClient::class.java -> caiYunRetrofit.create(clazz)
            else -> otherRetrofit.create(clazz)
        }
        return api
    }

    infix fun <A : Class<*>, B : Annotation> A.containsAnnotation(that: Class<B>): Boolean = getAnnotation(that) != null
}