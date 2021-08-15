package com.example.mymapandroidapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mymapandroidapp.adapter.MyPointItemRecyclerViewAdapter
import com.example.mymapandroidapp.adapter.OnInteractionListener
import com.example.mymapandroidapp.databinding.FragmentAllPointsListBinding
import com.example.mymapandroidapp.dto.MyPoint
import com.example.mymapandroidapp.viewModels.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllPointsFragment : Fragment() {

//    private var columnCount = 1
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        arguments?.let {
//            columnCount = it.getInt(ARG_COLUMN_COUNT)
//        }
//    }

    private val viewModel: MapsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_all_points_list, container, false)
        val binding = FragmentAllPointsListBinding.inflate(inflater, container, false)

        val adapter = MyPointItemRecyclerViewAdapter(object : OnInteractionListener{
            override fun onSelect(point: MyPoint) {
                super.onSelect(point)
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
//                if (newPostsWasPressed) {
//                    val lm = binding.recyclerView.layoutManager
//                    lm?.smoothScrollToPosition(binding.recyclerView, RecyclerView.State(), 0)
//                    newPostsWasPressed = false
//                }
            }
//            binding.emptyText.isVisible = state.empty
        }

        return binding.root

        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//                adapter = MyPointItemRecyclerViewAdapter(PlaceholderContent.ITEMS)
//            }
//        }
//        return view
    }

//    companion object {
//
//        // TODO: Customize parameter argument names
//        const val ARG_COLUMN_COUNT = "column-count"
//
//        // TODO: Customize parameter initialization
//        @JvmStatic
//        fun newInstance(columnCount: Int) =
//            AllPointsFragment().apply {
//                arguments = Bundle().apply {
//                    putInt(ARG_COLUMN_COUNT, columnCount)
//                }
//            }
//    }
}