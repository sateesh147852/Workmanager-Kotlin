package com.workmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.workmanager.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var data: Data
    private lateinit var data1: Data
    private lateinit var constraints: Constraints

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        data = Data.Builder().putString(MyWorker.TASK, "data passed from MainActivity")
            .build()

        data1 = Data.Builder().putString(MyWorker.TASK, "data passed from MainActivity2")
            .build()

        constraints = Constraints.Builder().setRequiresCharging(true).build()


        binding.btEnqueueWork.setOnClickListener() {
            //initializeOneTimeRequest()
            //initializeOnePeriodicRequest()
            initializeChainedRequest()
        }
    }


    private fun initializeOneTimeRequest() {

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance().enqueue(oneTimeWorkRequest)

        WorkManager.getInstance().getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this, Observer {
                binding.tvStatus.text = it.outputData.getString(MyWorker.TASK)
            })

    }

    private fun initializeOnePeriodicRequest() {

        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(MyWorker::class.java, 5, TimeUnit.MINUTES)
                .setInputData(data)
                //.setConstraints(constraints)
                .build()

        WorkManager.getInstance().enqueue(periodicWorkRequest)
    }

    private fun initializeChainedRequest() {

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .build()

        val oneTimeWorkRequest1 = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data1)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        val request = ArrayList<OneTimeWorkRequest>()
        request.add(oneTimeWorkRequest)
        request.add(oneTimeWorkRequest1)

        WorkManager.getInstance().enqueue(request)

        WorkManager.getInstance()
            .getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this, Observer {
                binding.tvStatus.text = it.outputData.getString(MyWorker.TASK)
            })

        WorkManager.getInstance()
            .getWorkInfoByIdLiveData(oneTimeWorkRequest1.id)
            .observe(this, Observer {
                binding.tvStatus2.text = it.outputData.getString(MyWorker.TASK)
            })
    }
}