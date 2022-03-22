package com.example.todo_list_with_geolocation.ui.update

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo_list_with_geolocation.R
import com.example.todo_list_with_geolocation.database.TaskEntity
import com.example.todo_list_with_geolocation.databinding.FragmentUpdateBinding
import com.example.todo_list_with_geolocation.util.*
import com.example.todo_list_with_geolocation.viewModel.TaskViewModel
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

        notificationId = args.taskEntity.notificationId

        binding.apply {
            createNotificationChannel()
            updTask.setText(args.taskEntity.task)
            updSpinner.setSelection(args.taskEntity.priority)
            btnUpd.setOnClickListener {
                if(TextUtils.isEmpty(updTask.text)) {
                    Toast.makeText(requireContext(), "It's empty!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val titleString = updTask.text
                val priority = updSpinner.selectedItemPosition
                val isRepeating = updCheckBox.isChecked

                val taskEntity = TaskEntity(
                    args.taskEntity.id,
                    titleString.toString(),
                    priority,
                    getDate(),
                    isRepeating,
                    notificationId
                )

                viewModel.update(taskEntity)
                updateNotification(notificationId)
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

    private fun updateNotification(notificationId: Int) {
        val intent = Intent(activity?.applicationContext, NotificationsReceiver::class.java)
        intent.putExtra(taskExtra, binding.updTask.text.toString())

        val pendingIntent = PendingIntent.getBroadcast(
            activity?.applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getDate()

        if (binding.updCheckBox.isChecked) {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
        else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        }
    }

    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val desc = "A Description of the channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = desc
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

