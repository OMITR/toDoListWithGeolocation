package com.example.todo_list_with_geolocation.ui.task

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_list_with_geolocation.R
import com.example.todo_list_with_geolocation.databinding.FragmentTaskBinding
import com.example.todo_list_with_geolocation.util.*
import com.example.todo_list_with_geolocation.viewModel.TaskViewModel


class TaskFragment : Fragment() {
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTaskBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        adapter = TaskAdapter(TaskClickListener { taskEntity ->
            findNavController().navigate(TaskFragmentDirections.actionTaskFragmentToUpdateFragment(taskEntity))
        })

        viewModel.getAllTasks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.apply {
            binding.recyclerView.adapter = adapter

            floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.action_taskFragment_to_addFragment)
            }
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskEntity = adapter.currentList[position]
                notificationId = taskEntity.notificationId
                viewModel.delete(taskEntity)
                cancelNotification(notificationId)
            }
        }).attachToRecyclerView(binding.recyclerView)

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun cancelNotification(notificationId: Int) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(activity?.applicationContext, NotificationsReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            activity?.applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)
        notificationManager.cancel(notificationId)
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.task_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    runQuery(newText)
                }
                return true
            }
        })
    }

    fun runQuery(query: String) {
        val searchQuery = "%$query%"
        viewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_priority -> viewModel.getAllPriorityTasks.observe(viewLifecycleOwner
            ) { tasks ->
                adapter.submitList(tasks)
            }
//            R.id.action_delete_all -> deleteAllItem()
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun deleteAllItem() {
//        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        AlertDialog.Builder(requireContext())
//            .setTitle("Delete All")
//            .setMessage("Are you sure?")
//            .setPositiveButton("Yes") {dialog, _ ->
//                viewModel.deleteAll()
//                notificationManager.cancelAll()
//                dialog.dismiss()
//            }.setNegativeButton("No") {dialog, _ ->
//                dialog.dismiss()
//            }.create().show()
//    }
}