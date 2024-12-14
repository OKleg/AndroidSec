package com.example.imagetagseditor.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagetagseditor.R
import com.example.imagetagseditor.main.adapter.ViewAdapter
import kotlinx.coroutines.launch


class MainFragment : Fragment() {


    private val viewModel = MainViewModel.getInstance()
    private lateinit var adapter: ViewAdapter
    private lateinit var editButton: Button
    private lateinit var recyclerView: RecyclerView
    private val permissions =
        setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class)
        )
    private val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()
    private val requestPermissions = registerForActivityResult(requestPermissionActivityContract) { granted ->
        if (granted.containsAll(permissions)) {
            viewModel.loadHealthData { adapter.update() }
        }
    }
    private fun initHealthConnectClient(context: Context): Boolean {
        val providerPackageName = "com.google.android.apps.healthdata"
        val availabilityStatus = HealthConnectClient.getSdkStatus(context, providerPackageName)
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            return false
        }
        viewModel.healthConnectClient = HealthConnectClient.getOrCreate(context)
        return true
    }
    private suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (!granted.containsAll(permissions)) {
            requestPermissions.launch(permissions)
        } else {
            viewModel.loadHealthData { adapter.update() }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.healthConnectClient == null && !initHealthConnectClient(requireContext()))
            return
        viewModel.healthData.stepsRecords.clear()
        adapter = ViewAdapter(viewModel.healthData.stepsRecords)
        lifecycleScope.launch {
            checkPermissionsAndRun(viewModel.healthConnectClient!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        editButton = view.findViewById(R.id.edit)
        editButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_mainFragment_to_editFragment)
        }
        recyclerView = view.findViewById(R.id.tags_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }
}