package com.example.data

import kotlinx.coroutines.flow.Flow

class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {
    val recentHistory: Flow<List<SearchHistoryEntry>> = searchHistoryDao.getRecentHistory()

    suspend fun addQuery(query: String, tab: String = "all") {
        // First delete double queries to prevent duplicate search list display
        searchHistoryDao.deleteByQuery(query)
        searchHistoryDao.insert(SearchHistoryEntry(query = query, tab = tab))
    }

    suspend fun removeQuery(query: String) {
        searchHistoryDao.deleteByQuery(query)
    }

    suspend fun clearAll() {
        searchHistoryDao.clearAll()
    }
}
