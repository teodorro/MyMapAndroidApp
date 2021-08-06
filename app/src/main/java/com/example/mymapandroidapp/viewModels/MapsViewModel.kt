package com.example.mymapandroidapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.mymapandroidapp.model.PointModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor() : ViewModel() {

    private val points: MutableList<PointModel> = emptyList<PointModel>().toMutableList()

    private var num: Int = 1;

    fun getName1(): String{
        return "point #$num";
    }

    fun savePoint(position: LatLng, title: String = ""){
        var t = if (title == "") getName1() else title
        points.add(PointModel(position, t))
    }
}