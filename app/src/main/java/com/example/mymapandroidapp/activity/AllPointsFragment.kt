package com.example.mymapandroidapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mymapandroidapp.adapter.MyPointItemRecyclerViewAdapter
import com.example.mymapandroidapp.adapter.MyPointViewHolder
import com.example.mymapandroidapp.adapter.OnInteractionListener
import com.example.mymapandroidapp.databinding.FragmentAllPointsListBinding
import com.example.mymapandroidapp.dto.MyPoint
import com.example.mymapandroidapp.extensions.SwipeToDeleteCallback
import com.example.mymapandroidapp.viewModels.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllPointsFragment : Fragment() {

    private val viewModel: MapsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAllPointsListBinding.inflate(inflater, container, false)

        val adapter = MyPointItemRecyclerViewAdapter(object : OnInteractionListener {
            override fun onSelect(point: MyPoint) {
                viewModel.selectedPoint = point
                findNavController().navigateUp()
            }

            override fun onDelete(point: MyPoint) {
                super.onDelete(point)
                viewModel.deletePoint(point.id)
            }

            override fun onEdit(point: MyPoint) {
                super.onEdit(point)
            }
        })
        binding.allPointsList.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(this.requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pointId = (viewHolder as MyPointViewHolder).pointId
                if (pointId != -1L)
                    viewModel.deletePoint(pointId)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.allPointsList)

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.points) {
            }
        }

        return binding.root
    }

}