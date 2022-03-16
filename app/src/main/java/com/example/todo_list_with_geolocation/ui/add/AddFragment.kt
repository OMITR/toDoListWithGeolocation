package com.example.todo_list_with_geolocation.ui.add

import android.app.*
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo_list_with_geolocation.R
import com.example.todo_list_with_geolocation.database.TaskEntity
import com.example.todo_list_with_geolocation.databinding.FragmentAddBinding
import com.example.todo_list_with_geolocation.notification.*
import com.example.todo_list_with_geolocation.viewmodel.TaskViewModel
import java.util.*
import android.app.AlarmManager as AlarmManager

class AddFragment : Fragment() {
    private val viewModel: TaskViewModel by viewModels()
    private var _binding : FragmentAddBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddBinding.inflate(inflater)

        val myAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.priorities)
        )

        binding.apply {
            createNotificationChannel()
            spinner.adapter = myAdapter
            btnAdd.setOnClickListener {
                if (TextUtils.isEmpty((addTask.text))) {
                    Toast.makeText(requireContext(), "It's empty!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val task = addTask.text.toString()
                val priority = spinner.selectedItemPosition


                val taskEntity = TaskEntity(
                    0,
                    task,
                    priority,
                    getDate()
                )

                viewModel.insert(taskEntity)
                scheduleNotification()
                Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addFragment_to_taskFragment)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDate(): Long {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year
        val date = Calendar.getInstance()
        date.set(year, month, day, hour, minute).toString()

        return date.timeInMillis
    }

    private fun scheduleNotification() {
        val intent = Intent(activity?.applicationContext, Notifications::class.java)
        intent.putExtra(TITLE_EXTRA, "Напоминание")
        intent.putExtra(TASK_EXTRA, binding.addTask.text.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            activity?.applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = activity?.getSystemService(ALARM_SERVICE) as AlarmManager
        val time = getDate()
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val desc = "A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = desc
        val notificationManager = activity?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}