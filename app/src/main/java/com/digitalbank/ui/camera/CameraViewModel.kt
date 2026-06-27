package com.digitalbank.ui.camera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val _isFrontLens = MutableStateFlow(true)
    val isFrontLens: StateFlow<Boolean> = _isFrontLens.asStateFlow()

    fun toggleLens() {
        _isFrontLens.value = !_isFrontLens.value
    }

    companion object {
        fun factory(application: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CameraViewModel(application) as T
            }
        }
    }
}
