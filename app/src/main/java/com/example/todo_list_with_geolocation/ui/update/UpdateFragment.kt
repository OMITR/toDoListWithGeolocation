package com.example.todo_list_with_geolocation.ui.update

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo_list_with_geolocation.R
import com.example.todo_list_with_geolocation.database.TaskEntity
import com.example.todo_list_with_geolocation.databinding.FragmentUpdateBinding
import com.example.todo_list_with_geolocation.notification.*
import com.example.todo_list_with_geolocation.ui.MainActivity
import com.example.todo_list_with_geolocation.viewmodel.TaskViewModel
import java.util.*

class UpdateFragment : Fragment() {
    private val viewModel: TaskViewModel by viewModels()

    private var _binding : FragmentUpdateBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(inflater)
        val args = UpdateFragmentArgs.fromBundle(requireArguments())

        binding.apply {
            createNotificationChannel()
            updTask.setText(args.taskEntry.task)
            updSpinner.setSelection(args.taskEntry.priority)
            btnUpd.setOnClickListener {
                if(TextUtils.isEmpty(updTask.text)) {
                    Toast.makeText(requireContext(), "It's empty!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val titleString = updTask.text
                val priority = updSpinner.selectedItemPosition

                val taskEntity = TaskEntity(
                    args.taskEntry.id,
                    titleString.toString(),
                    priority,
                    getDate()
                )

                viewModel.update(taskEntity)
                scheduleNotification()
                Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_updateFragment_to_taskFragment)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDate(): Long {
        val minute = binding.updTimePicker.minute
        val hour = binding.updTimePicker.hour
        val day = binding.updDatePicker.dayOfMonth
        val month = binding.updDatePicker.month
        val year = binding.updDatePicker.year
        val date = Calendar.getInstance()
        date.set(year, month, day, hour, minute).toString()

        return date.timeInMillis
    }

    private fun scheduleNotification() {
        val intent = Intent(activity?.applicationContext, Notifications::class.java)
        intent.putExtra(TITLE_EXTRA, "Напоминание")
        intent.putExtra(TASK_EXTRA, binding.updTask.text.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            activity?.applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}