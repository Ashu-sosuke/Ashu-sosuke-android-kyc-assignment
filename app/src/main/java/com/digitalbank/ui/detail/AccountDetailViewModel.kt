package com.digitalbank.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.digitalbank.data.repository.CustomerRepository
import com.digitalbank.data.repository.IfscRepository
import com.digitalbank.model.BankInfo
import com.digitalbank.model.Customer
import com.digitalbank.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val customerRepository = CustomerRepository(application)
    private val ifscRepository = IfscRepository(application)

    private val _customerState = MutableStateFlow<UiState<Customer>>(UiState.Loading)
    val customerState: StateFlow<UiState<Customer>> = _customerState.asStateFlow()

    private val _bankInfoState = MutableStateFlow<UiState<BankInfo>>(UiState.Loading)
    val bankInfoState: StateFlow<UiState<BankInfo>> = _bankInfoState.asStateFlow()

    fun loadCustomer(id: Int) {
        _customerState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val customer = customerRepository.getCustomer(id)
                _customerState.value = UiState.Success(customer)
                resolveIfsc(customer.ifsc)
            } catch (e: Exception) {
                _customerState.value = UiState.Error(e.message ?: "Failed to load customer")
            }
        }
    }

    fun resolveIfsc(ifsc: String) {
        _bankInfoState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val bankInfo = ifscRepository.resolve(ifsc)
                _bankInfoState.value = UiState.Success(bankInfo)
            } catch (e: Exception) {
                _bankInfoState.value = UiState.Error(e.message ?: "Failed to resolve bank details")
            }
        }
    }

    fun completeKyc(customerId: Int, selfiePath: String) {
        viewModelScope.launch {
            try {
                customerRepository.completeKyc(customerId, selfiePath)
                // Reload customer details to update state in the UI
                loadCustomer(customerId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    companion object {
        fun factory(application: Application) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AccountDetailViewModel(application) as T
            }
        }
    }
}
