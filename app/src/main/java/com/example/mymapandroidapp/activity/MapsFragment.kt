package com.example.mymapandroidapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.example.mymapandroidapp.R
import com.example.mymapandroidapp.databinding.FragmentMapsBinding
import com.example.mymapandroidapp.dto.MyPointMarker
import com.example.mymapandroidapp.extensions.icon
import com.example.mymapandroidapp.utils.AndroidUtils
import com.example.mymapandroidapp.viewModels.MapsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textview.MaterialTextView
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.cameraPosition
import com.google.maps.android.ktx.utils.collection.addMarker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap

    private val viewModel: MapsViewModel by viewModels(
        ownerProducer = ::requireParentFragment,
    )

    private lateinit var markerManager: MarkerManager
    private lateinit var markerCollection: MarkerManager.Collection

    private lateinit var bottomSheet: FrameLayout
    private lateinit var textViewMarker: MaterialTextView
    private lateinit var editTextTitle: EditText
    private lateinit var layoutAddPoint: ConstraintLayout

    private var selectedMarker: Marker? = null
    private var selectedPosition: LatLng? = null
    private var pointMarkerMap: MutableList<MyPointMarker> = mutableListOf()

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                // TODO: show sorry dialog
            }
        }

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMapsBinding.inflate(inflater, container, false)

        viewModel.data.observe(viewLifecycleOwner) { state ->

            if (state != null) {
                var points = state.points

//                var pmm = pointMarkerMap
                var pointsToAdd = points.filter { x ->
                    !pointMarkerMap.map { y -> y.point.id }.contains(x.id)
                }

                // add points
                pointsToAdd.forEach { x ->
                    markerCollection.addMarker {
                        this.position(LatLng(x.latitude, x.longitude))
                        this.title(x.title)
                        this.icon(
                            getDrawable(
                                requireContext(),
                                R.drawable.ic_baseline_location_on_32
                            )!!
                        )
                    }
                        .also {
                            pointMarkerMap.add(MyPointMarker(x, it))
                        }
                }

                // delete points
                var pointsToDelete = pointMarkerMap.filter { x ->
                    !points.map { y -> y.id }.contains(x.point.id)
                }
                pointsToDelete.forEach { x ->
                    markerCollection.remove(pointMarkerMap.first { y -> y == x }.marker)
                        .also {
                            pointMarkerMap.remove(x)
                        }
                }

                // update points
                points.forEach { p ->
                    var oldPointMarker = pointMarkerMap.first { x -> x.point.id == p.id }
                    if (oldPointMarker.point != p) {
                        oldPointMarker.marker.title = p.title
                        pointMarkerMap.add(MyPointMarker(p, oldPointMarker.marker))
                        pointMarkerMap.remove(oldPointMarker)
                    }
                }

//                val x1 = viewModel.selectedPoint
//                var pmm2 = pointMarkerMap
//                val x3 = if (x1 == null) null else pmm2.first { x -> x.point.id == x1!!.id }
                if ((viewModel.selectedPoint != null && selectedMarker == null)
                    || (viewModel.selectedPoint != null && pointMarkerMap.first { x -> x.marker == selectedMarker}.point != viewModel.selectedPoint))
                    selectPoint(pointMarkerMap.first { x -> x.point.id == viewModel.selectedPoint!!.id }.marker)
                if (selectedMarker != null)
                    showBottomSheet(selectedMarker!!)
            }
        }

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = 0
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // button delete
        binding.buttonDelete.setOnClickListener {
            var point = pointMarkerMap.first { x -> x.marker == selectedMarker }.point
            viewModel.deletePoint(point.id)
            hideBottomSheet()
        }

        // button edit
        binding.buttonEdit.setOnClickListener {
            hideBottomSheet()
            showEditText()
            selectedPosition = selectedMarker!!.position
            editTextTitle.setText(selectedMarker!!.title)
        }

        binding.editTextTitle.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {

                var sm = selectedMarker
                var a1 = pointMarkerMap
                var txt = editTextTitle.text.toString()
                if (txt.isNotBlank()) {
                    if (selectedMarker != null) {
                        viewModel.updatePoint(
                            pointMarkerMap.first { x -> x.marker == selectedMarker }.point.id,
                            txt
                        )
                    } else
                        viewModel.addPoint(selectedPosition!!, txt)
                } else {
                    if (selectedMarker != null) {
                        viewModel.deletePoint(pointMarkerMap.first { x -> x.marker == selectedMarker }.point.id)
                        selectPoint(null)
                    }
                }
                hideEditText()
                selectedPosition = null
                return@OnKeyListener true
            }
            false
        })

        bottomSheet = binding.bottomSheet
        textViewMarker = binding.textViewMarker
        editTextTitle = binding.editTextTitle
        layoutAddPoint = binding.layoutAddPoint

        return binding.root

//        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val markersInitialized = this::markerManager.isInitialized

        lifecycle.coroutineScope.launchWhenCreated {
            if (!markersInitialized) {
            setMap(mapFragment)
            setupGeoPosition()

                markerManager = MarkerManager(googleMap)
                markerCollection = markerManager.newCollection("markCollection")
                // show bottomsheet
                markerCollection.setOnMarkerClickListener {
                    showBottomSheet(it)
                    selectPoint(it)
                    return@setOnMarkerClickListener true
                }
            }

            if (viewModel.selectedPoint != null)
                selectPoint(pointMarkerMap.first { x -> x.point == viewModel.selectedPoint }.marker)
            else if (pointMarkerMap.any())
                selectPoint(pointMarkerMap.first().marker)
            else {
                moveCamera(55.751999, 37.617734)
            }
        }
    }

    private fun setupGeoPosition() {
        when {
            // 1. Проверяем есть ли уже права
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }

                val fusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(requireActivity())

                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    println(it)
                }

                // map longClick
                googleMap.setOnMapLongClickListener {
                    selectedPosition = it
                    showEditText()
                }

                // map click
                googleMap.setOnMapClickListener {
                    selectPoint(null)
                    hideEditText()
                    hideBottomSheet()
                }
            }
            // 2. Должны показать обоснование необходимости прав
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // TODO: show rationale dialog
            }
            // 3. Запрашиваем права
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private suspend fun setMap(mapFragment: SupportMapFragment) {
        googleMap = mapFragment.awaitMap().apply {
            isTrafficEnabled = true
            isBuildingsEnabled = true

            uiSettings.apply {
                isZoomControlsEnabled = true
                setAllGesturesEnabled(true)
            }
        }
    }

    private fun showBottomSheet(marker: Marker) {
        BottomSheetBehavior.from(bottomSheet).state =
            BottomSheetBehavior.STATE_EXPANDED
        textViewMarker.text = marker.title
    }

    private fun hideBottomSheet() {
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = 0
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun showEditText() {
        layoutAddPoint.visibility = View.VISIBLE
        editTextTitle.requestFocus()
        AndroidUtils.showKeyboard(editTextTitle, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideEditText() {
        layoutAddPoint.visibility = View.INVISIBLE
        editTextTitle.text.clear()
        AndroidUtils.hideKeyboard(requireView())
    }

    private fun selectPoint(marker: Marker?){
        selectedMarker = marker
        if (marker != null) {
            viewModel.selectedPoint = pointMarkerMap.first { x -> x.marker == selectedMarker }.point
            val point = viewModel.selectedPoint!!
            lifecycle.coroutineScope.launch {
                moveCamera(point.latitude, point.longitude)
            }
        }
        else
            viewModel.selectedPoint = null
    }

    private suspend fun moveCamera(latitude: Double, longitude: Double) {
        val target = LatLng(latitude, longitude)
        googleMap.awaitAnimateCamera(
            CameraUpdateFactory.newCameraPosition(
                cameraPosition {
                    target(target)
                    zoom(15F)
                }
            ))
    }
}