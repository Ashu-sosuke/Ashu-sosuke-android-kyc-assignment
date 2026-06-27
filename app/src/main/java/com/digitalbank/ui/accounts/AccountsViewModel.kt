package com.digitalbank.ui.accounts

  import android.app.Application
  import androidx.lifecycle.AndroidViewModel
  import androidx.lifecycle.ViewModel
  import androidx.lifecycle.ViewModelProvider
  import androidx.lifecycle.viewModelScope
  import com.digitalbank.data.repository.CustomerRepository
  import com.digitalbank.model.Customer
  import com.digitalbank.ui.UiState
  import kotlinx.coroutines.flow.*
  import kotlinx.coroutines.launch

  class AccountsViewModel(application: Application) : AndroidViewModel(application) {
      private val repository = CustomerRepository(application)

      private val _allCustomers = MutableStateFlow<List<Customer>>(emptyList())
      val allCustomers: StateFlow<List<Customer>> = _allCustomers.asStateFlow()

      private val _uiState = MutableStateFlow<UiState<List<Customer>>>(UiState.Loading)
      val uiState: StateFlow<UiState<List<Customer>>> = _uiState.asStateFlow()

      val searchQuery = MutableStateFlow("")
      val selectedTab = MutableStateFlow(0)
      val selectedChip = MutableStateFlow("All")

      private var currentPage = 0
      private var isLastPage = false
      private var isPageLoading = false

      val filteredCustomers: StateFlow<List<Customer>> = combine(
          _allCustomers,
          searchQuery,
          selectedTab,
          selectedChip
      ) { customers, query, tab, chip ->
          customers.filter {
              it.kycStatus.ordinal == tab &&
              (chip == "All" || it.accountType == chip) &&
              (query.isEmpty() || it.name.contains(query, ignoreCase = true) || it.maskedIban.contains(query))
          }
      }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

      init {
          loadCustomers()
      }

      fun loadCustomers() {
          if (isPageLoading) return
          isPageLoading = true
          if (currentPage == 0) {
              _uiState.value = UiState.Loading
          }
          viewModelScope.launch {
              try {
                  val result = repository.getCustomers(currentPage)
                  if (result.isEmpty()) {
                      isLastPage = true
                      if (currentPage == 0) {
                          _uiState.value = UiState.Empty
                      }
                  } else {
                      if (currentPage == 0) {
                          _allCustomers.value = result
                      } else {
                          _allCustomers.value = _allCustomers.value + result
                      }
                      _uiState.value = UiState.Success(_allCustomers.value)
                  }
              } catch (e: Exception) {
                  if (currentPage == 0) {
                      _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
                  }
              } finally {
                  isPageLoading = false
              }
          }
      }

      fun loadMore() {
          if (!isLastPage && !isPageLoading) {
              currentPage++
              loadCustomers()
          }
      }

      fun retry() {
          currentPage = 0
          isLastPage = false
          loadCustomers()
      }

      companion object {
          fun factory(application: Application) = object : ViewModelProvider.Factory {
              override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  @Suppress("UNCHECKED_CAST")
                  return AccountsViewModel(application) as T
              }
          }
      }
  }
