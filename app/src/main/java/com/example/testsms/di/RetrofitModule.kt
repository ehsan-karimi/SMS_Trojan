package com.example.testsms.di

import com.example.testsms.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {


    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

            }
        )



        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        val client = OkHttpClient.Builder()

        client.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        client.hostnameVerifier { _, _ -> true }
        client.readTimeout(60, TimeUnit.SECONDS)
        client.connectTimeout(60, TimeUnit.SECONDS)
        client.addInterceptor(interceptor)
        client.addInterceptor(Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "AAAARr6Jcbk:APA91bFLrJ5ESAyRQ4H8EXyFppafI7KESFcRdOt1--9X1-F93tEBW87JA-KWqwcigXym2OnX8dtxHpSsvOHRjw0q2bHhx3fsf04F1S65aNmJmRTQHGn0PwnujPxrSoi3E_Ej4bXSZQAX")
                .build()
            chain.proceed(newRequest)
        })
        return client.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://fcm.googleapis.com/fcm/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    @Singleton
    @Provides
    fun provideBlogService(retrofit: Retrofit.Builder): ApiService {
        return retrofit
            .build()
            .create(ApiService::class.java)
    }


}