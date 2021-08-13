package com.example.mymapandroidapp.viewModels

import androidx.lifecycle.*
import com.example.mymapandroidapp.dto.MyPoint
import com.example.mymapandroidapp.model.FeedModel
import com.example.mymapandroidapp.model.FeedModelState
import com.example.mymapandroidapp.repository.MyPointRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val repository: MyPointRepository
) : ViewModel() {

    val data: LiveData<FeedModel> = repository.data
        .map { points ->
            FeedModel(
                points,
                points.isEmpty()
            )
        }.asLiveData()


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private var nextId: Long = 1;

    init {
        loadPoints()
    }

    fun loadPoints() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            var maxId = repository.getMaxId()
            nextId = ++maxId
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun addPoint(position: LatLng, title: String) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(refreshing = true)
                val point = MyPoint(nextId++, position.latitude, position.longitude, title)
                repository.insert(point)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun updatePoint(id: Long, title: String){
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(refreshing = true)
                repository.update(id, title)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun deletePoint(id: Long){
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(refreshing = true)
                repository.delete(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}