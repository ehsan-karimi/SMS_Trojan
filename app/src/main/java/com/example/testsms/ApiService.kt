package com.example.testsms

import com.example.testsms.model.SendNotificationModel
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiService {
    @POST("send")
    fun sendNotification(@Header("Authorization") accessToken: String, @Body sendNotificationModel: SendNotificationModel): Completable


}