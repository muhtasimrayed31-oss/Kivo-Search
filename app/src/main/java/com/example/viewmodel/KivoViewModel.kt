package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SearchHistoryEntry
import com.example.data.SearchHistoryRepository
import com.example.data.SearchRepository
import com.example.network.KivoNetwork
import com.example.network.SearchItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KivoViewModel(
    private val historyRepository: SearchHistoryRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    // --- State Properties ---

    private val _currentNavTab = MutableStateFlow("home") // "home", "search", "settings"
    val currentNavTab: StateFlow<String> = _currentNavTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeSearchSubTab = MutableStateFlow("all") // "all", "image", "video", "news"
    val activeSearchSubTab: StateFlow<String> = _activeSearchSubTab.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchItem>>(emptyList())
    val searchResults: StateFlow<List<SearchItem>> = _searchResults.asStateFlow()

    private val _aiOverview = MutableStateFlow<String?>(null)
    val aiOverview: StateFlow<String?> = _aiOverview.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchStart = MutableStateFlow(1)
    val searchStart: StateFlow<Int> = _searchStart.asStateFlow()

    // Weather & Location Area
    private val _localCity = MutableStateFlow("London")
    val localCity: StateFlow<String> = _localCity.asStateFlow()

    private val _localCountry = MutableStateFlow("United Kingdom")
    val localCountry: StateFlow<String> = _localCountry.asStateFlow()

    private val _localTemp = MutableStateFlow("28°C")
    val localTemp: StateFlow<String> = _localTemp.asStateFlow()

    private val _languageNotification = MutableStateFlow<String?>(null)
    val languageNotification: StateFlow<String?> = _languageNotification.asStateFlow()

    // Auth Simulation
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _isShowingAuthDialog = MutableStateFlow(false)
    val isShowingAuthDialog: StateFlow<Boolean> = _isShowingAuthDialog.asStateFlow()

    // Dark Mode settings
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Search History Room integration
    val searchHistory: StateFlow<List<SearchHistoryEntry>> = historyRepository.recentHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        fetchLocationAndWeather()
    }

    // --- Actions ---

    fun setNavTab(tab: String) {
        _currentNavTab.value = tab
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSubTab(subTab: String) {
        _activeSearchSubTab.value = subTab
        if (_searchQuery.value.isNotBlank()) {
            performSearch(_searchQuery.value, resetPagination = true)
        }
    }

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    fun toggleAuthDialog(show: Boolean) {
        _isShowingAuthDialog.value = show
    }

    fun signInUser(email: String) {
        _currentUserEmail.value = if (email.isBlank()) "Guest" else email
        _isShowingAuthDialog.value = false
    }

    fun signOutUser() {
        _currentUserEmail.value = null
    }

    fun clearLanguageNotification() {
        _languageNotification.value = null
    }

    fun deleteHistoryItem(query: String) {
        viewModelScope.launch {
            historyRepository.removeQuery(query)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearAll()
        }
    }

    fun performSearch(query: String, resetPagination: Boolean = true) {
        if (query.isBlank()) return
        _searchQuery.value = query

        viewModelScope.launch {
            _isLoading.value = true
            _currentNavTab.value = "search" // ensure we view the results layout

            if (resetPagination) {
                _searchStart.value = 1
                _searchResults.value = emptyList()
                _aiOverview.value = null
                // Save query in room database asynchronously
                historyRepository.addQuery(query, _activeSearchSubTab.value)
            }

            // Fetch AI SGE Overview only on the first page of "All" results
            if (resetPagination && _activeSearchSubTab.value == "all") {
                launch {
                    try {
                        _aiOverview.value = searchRepository.fetchAiOverview(query)
                    } catch (e: Exception) {
                        _aiOverview.value = "Unable to load intelligent SGE overview at this time."
                    }
                }
            }

            // Fetch actual Web / Image results from Google API or fallback index
            try {
                val googleType = if (_activeSearchSubTab.value == "image") "image" else null
                var modifiedQuery = query
                if (_activeSearchSubTab.value == "video") modifiedQuery += " video"
                if (_activeSearchSubTab.value == "news") modifiedQuery += " news"

                val results = searchRepository.fetchSearchResults(
                    query = modifiedQuery,
                    start = _searchStart.value,
                    searchType = googleType
                )

                if (resetPagination) {
                    _searchResults.value = results
                } else {
                    _searchResults.value = _searchResults.value + results
                }
            } catch (e: Exception) {
                // Handled in repository via fallback mock results
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreResults() {
        _searchStart.value = _searchStart.value + 10
        performSearch(_searchQuery.value, resetPagination = false)
    }

    fun fetchLocationAndWeather() {
        viewModelScope.launch {
            try {
                val loc = KivoNetwork.ipService.getIpLocation()
                if (loc.status == "success" && loc.lat != null && loc.lon != null) {
                    _localCity.value = loc.city ?: "London"
                    _localCountry.value = loc.country ?: "United Kingdom"

                    // Notify localized language eligibility
                    _languageNotification.value = "KIVO is available in your local language (Location: ${loc.country})"

                    // Now load weather coordinates
                    val weather = KivoNetwork.weatherService.getWeather(loc.lat, loc.lon)
                    weather.current_weather?.temperature?.let { temp ->
                        _localTemp.value = "${Math.round(temp)}°C"
                    }
                }
            } catch (e: Exception) {
                // SILENT FALLBACK TO STANDARD COLD DEFAULTS
            }
        }
    }
}

class KivoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KivoViewModel::class.java)) {
            val db = AppDatabase.getDatabase(context)
            val historyRepository = SearchHistoryRepository(db.searchHistoryDao())
            val searchRepository = SearchRepository()
            @Suppress("UNCHECKED_CAST")
            return KivoViewModel(historyRepository, searchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
