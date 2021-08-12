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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import com.example.mymapandroidapp.R
import com.example.mymapandroidapp.databinding.FragmentMapsBinding
import com.example.mymapandroidapp.dto.MyPoint
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

    private lateinit var selectedMarker: Marker
    private var selectedPosition: LatLng? = null
    private var pointMarkerMap: MutableMap<MyPoint, Marker> = mutableMapOf()

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

                var pointsToAdd = points.filter { x ->
                    !pointMarkerMap.keys.map { y -> y.id }.contains(x.id)
                }

                // add points
                pointsToAdd.forEach { x ->
                    markerCollection.addMarker {
                        this.position(LatLng(x.latitude, x.longitude))
                        this.title(x.title)
                        this.icon(
                            getDrawable(
                                requireContext(),
                                R.drawable.ic_baseline_location_on_24
                            )!!
                        )
                    }
                        .also {
                            pointMarkerMap.put(x, it)
                        }
                }

                // delete points
                var pointsToDelete = pointMarkerMap.keys.filter { x ->
                    !points.map { y -> y.id }.contains(x.id)
                }
                pointsToDelete.forEach { x ->
                    markerCollection.remove(pointMarkerMap[x])
                        .also {
                            pointMarkerMap.remove(x)
                        }
                }

                // TODO: update points

            }
        }

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight = 0
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.buttonDelete.setOnClickListener {
            var point = pointMarkerMap.filter { x -> x.value == selectedMarker }.keys.first()
            viewModel.deletePoint(point.id)
            BottomSheetBehavior.from(binding.bottomSheet).apply {
                peekHeight = 0
                this.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        binding.editTextTitle.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                AndroidUtils.hideKeyboard(editTextTitle)
                var txt = editTextTitle.text.toString()
                if (txt.isNotBlank())
                    viewModel.addPoint(selectedPosition!!, txt)
                selectedPosition = null
                editTextTitle.visibility = View.INVISIBLE
                editTextTitle.text.clear()
                return@OnKeyListener true
            }
            false
        })

        bottomSheet = binding.bottomSheet
        textViewMarker = binding.textViewMarker
        editTextTitle = binding.editTextTitle

        return binding.root

//        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        lifecycle.coroutineScope.launchWhenCreated {
            setMap(mapFragment)
            setupGeoPosition()

            markerManager = MarkerManager(googleMap)
            markerCollection = markerManager.newCollection("markCollection")
            markerCollection.setOnMarkerClickListener {
                BottomSheetBehavior.from(bottomSheet).state =
                    BottomSheetBehavior.STATE_EXPANDED
                textViewMarker.text = it.title
                selectedMarker = it
                return@setOnMarkerClickListener true
            }

            val target = LatLng(55.751999, 37.617734)
            googleMap.awaitAnimateCamera(
                CameraUpdateFactory.newCameraPosition(
                    cameraPosition {
                        target(target)
                        zoom(15F)
                    }
                ))
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

                googleMap.setOnMapLongClickListener {
                    selectedPosition = it
                    editTextTitle.visibility = View.VISIBLE
                    editTextTitle.requestFocus()
                    AndroidUtils.showKeyboard(editTextTitle, InputMethodManager.SHOW_IMPLICIT)
                }

                googleMap.setOnMapClickListener {
                    editTextTitle.visibility = View.INVISIBLE
                    editTextTitle.text.clear()
                    AndroidUtils.hideKeyboard(requireView())
                    BottomSheetBehavior.from(bottomSheet).apply {
                        peekHeight = 0
                        this.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
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

}