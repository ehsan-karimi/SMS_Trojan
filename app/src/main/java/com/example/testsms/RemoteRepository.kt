package com.example.testsms

import com.example.testsms.model.SendNotificationModel
import io.reactivex.Completable
import javax.inject.Inject

class RemoteRepository @Inject constructor(val apiService: ApiService) {
    fun sendNotification(token: String,sendNotificationModel: SendNotificationModel): Completable {
        return apiService.sendNotification(token, sendNotificationModel)
    }
}