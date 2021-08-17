package com.example.mymapandroidapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mymapandroidapp.adapter.MyPointItemRecyclerViewAdapter
import com.example.mymapandroidapp.adapter.OnInteractionListener
import com.example.mymapandroidapp.databinding.FragmentAllPointsListBinding
import com.example.mymapandroidapp.dto.MyPoint
import com.example.mymapandroidapp.viewModels.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.model.cameraPosition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            }

            override fun onEdit(point: MyPoint) {
                super.onEdit(point)
            }
        })
        binding.allPointsList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.points) {
            }
        }

        return binding.root
    }

}