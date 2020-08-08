package com.efficom.efid.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.efficom.efid.R
import com.efficom.efid.adapter.RoomAdapter
import com.efficom.efid.data.model.sealedClass.ErrorRoomApi
import com.efficom.efid.data.model.sealedClass.RoomApiReturn
import com.efficom.efid.viewmodel.LoginViewModel
import com.efficom.efid.viewmodel.RoomViewModel
import kotlinx.android.synthetic.main.fragment_room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RoomFragment: BaseFragment() {

    lateinit var viewModel: RoomViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(RoomViewModel::class.java)
        return inflater.inflate(R.layout.fragment_room, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.roomList.observe(viewLifecycleOwner, Observer {
            setupRecyclerView(it)
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {

        })
    }

    private fun setupRecyclerView(data: List<String>) {

        val list = listOf<String>("", "Salle 001", "Salle 002", "Salle 003")

        room_recyclerview.apply {
            layoutManager =LinearLayoutManager(context)
            val roomAdapter = RoomAdapter(list)
            adapter = roomAdapter
            roomAdapter.notifyDataSetChanged()
        }
    }

    private fun displayError(error: RoomApiReturn) {
        when(error){
            is ErrorRoomApi -> displayToast("", context)
        }
    }
}