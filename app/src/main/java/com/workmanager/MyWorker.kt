package com.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(val context: Context, val workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private val TAG : String = MyWorker::class.java.simpleName

    companion object{
        val TASK : String = "TASK"
    }

    override fun doWork(): Result {
        displayNotification("My Worker", "Hey I finished my work")
        val task : String? = inputData.getString(TASK)
        if (task != null) {
            showLogs(task)
        }
        val data = Data.Builder().putString(TASK,task).build()
        return Result.success(data)
    }

    private fun showLogs(task : String) {
        Log.i(TAG, "showLogs: $task")
    }

    private fun displayNotification(title: String, message: String) {

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("workManager", "workManager", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(applicationContext, "workManager")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        notificationManager.notify(1, notification)
    }
}