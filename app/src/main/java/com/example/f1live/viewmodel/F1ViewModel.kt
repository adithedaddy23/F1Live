package com.example.f1live.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f1live.api.Circuits
import com.example.f1live.api.ConstructorStandings
import com.example.f1live.api.Constructors
import com.example.f1live.api.DriverStandings
import com.example.f1live.api.Drivers
import com.example.f1live.api.LapData
import com.example.f1live.api.Qualifying
import com.example.f1live.api.RaceList
import com.example.f1live.api.ResultX
import com.example.f1live.api.SprintX
import com.example.f1live.api.UiState
import com.example.f1live.news.NewsArticle
import com.example.f1live.news.RssFeed
import com.example.f1live.repository.F1Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class F1ViewModel : ViewModel() {
    private val repository = F1Repository()

    // State flows for each endpoint
    private val _circuitsState = MutableStateFlow<UiState<Circuits>>(UiState.Loading)
    val circuitsState: StateFlow<UiState<Circuits>> = _circuitsState.asStateFlow()

    private val _constructorsState = MutableStateFlow<UiState<Constructors>>(UiState.Loading)
    val constructorsState: StateFlow<UiState<Constructors>> = _constructorsState.asStateFlow()

    private val _constructorStandingsState = MutableStateFlow<UiState<ConstructorStandings>>(UiState.Loading)
    val constructorStandingsState: StateFlow<UiState<ConstructorStandings>> = _constructorStandingsState.asStateFlow()

    private val _driversState = MutableStateFlow<UiState<Drivers>>(UiState.Loading)
    val driversState: StateFlow<UiState<Drivers>> = _driversState.asStateFlow()

    private val _driverStandingsState = MutableStateFlow<UiState<DriverStandings>>(UiState.Loading)
    val driverStandingsState: StateFlow<UiState<DriverStandings>> = _driverStandingsState.asStateFlow()

    private val _qualifyingState = MutableStateFlow<UiState<Qualifying>>(UiState.Loading)
    val qualifyingState: StateFlow<UiState<Qualifying>> = _qualifyingState.asStateFlow()

    private val _racesState = MutableStateFlow<UiState<RaceList>>(UiState.Loading)
    val racesState: StateFlow<UiState<RaceList>> = _racesState.asStateFlow()

    private val _racesRoundState = MutableStateFlow<UiState<RaceList>>(UiState.Loading)
    val racesRoundState: StateFlow<UiState<RaceList>> = _racesRoundState.asStateFlow()

    private val _resultsState = MutableStateFlow<UiState<com.example.f1live.api.Result>>(UiState.Loading)
    val resultsState: StateFlow<UiState<com.example.f1live.api.Result>> = _resultsState.asStateFlow()

    private val _allResultsState = MutableStateFlow<UiState<com.example.f1live.api.Result>>(UiState.Loading)
    val allResultsState: StateFlow<UiState<com.example.f1live.api.Result>> = _allResultsState

    private val _sprintState = MutableStateFlow<UiState<SprintX>>(UiState.Loading)
    val sprintState: StateFlow<UiState<SprintX>> = _sprintState.asStateFlow()

    private val _raceResultsMap = MutableStateFlow<Map<String, List<ResultX>>>(emptyMap())
    val raceResultsMap: StateFlow<Map<String, List<ResultX>>> = _raceResultsMap.asStateFlow()

    private val _driverDetailsState = MutableStateFlow<UiState<com.example.f1live.api.DriverDetails>>(UiState.Loading)
    val driverDetailsState: StateFlow<UiState<com.example.f1live.api.DriverDetails>> = _driverDetailsState

    private val _newsState = MutableStateFlow<UiState<List<NewsArticle>>>(UiState.Loading)
    val newsState: StateFlow<UiState<List<NewsArticle>>> = _newsState.asStateFlow()

    private val _lapDataState = MutableStateFlow<UiState<LapData>>(UiState.Loading)
    val lapDataState: StateFlow<UiState<LapData>> = _lapDataState.asStateFlow()

    // Current year and round tracking
    @RequiresApi(Build.VERSION_CODES.O)
    private val year = LocalDate.now().year
    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentYear = MutableStateFlow(year.toString())
    @RequiresApi(Build.VERSION_CODES.O)
    val currentYear: StateFlow<String> = _currentYear.asStateFlow()

    private val _currentRound = MutableStateFlow("1")
    val currentRound: StateFlow<String> = _currentRound.asStateFlow()

    // CACHING - Track what's been fetched
    private var cachedYear: String? = null
    private var lastFetchTime: Long = 0
    private val CACHE_DURATION = 3600000L // 1 hour in milliseconds

    // Track which data has been fetched for the cached year
    private val fetchedDataFlags = mutableSetOf<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedYear = MutableStateFlow(LocalDate.now().year - 1)
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedYear(year: Int) {
        _selectedYear.value = year
    }

    private fun isCacheValid(year: String): Boolean {
        return cachedYear == year &&
                System.currentTimeMillis() - lastFetchTime < CACHE_DURATION
    }

    private fun markDataFetched(dataType: String, year: String) {
        if (cachedYear != year) {
            // New year, reset cache
            cachedYear = year
            fetchedDataFlags.clear()
            lastFetchTime = System.currentTimeMillis()
        }
        fetchedDataFlags.add(dataType)
    }

    private fun isDataFetched(dataType: String, year: String): Boolean {
        return isCacheValid(year) && fetchedDataFlags.contains(dataType)
    }

    fun fetchNews(forceRefresh: Boolean = false) {
        if (!forceRefresh && _newsState.value is UiState.Success) {
            Log.d("F1ViewModel", "Using cached news data")
            return
        }

        viewModelScope.launch {
            _newsState.value = UiState.Loading
            Log.d("F1ViewModel", "Fetching news...")

            try {
                val response = repository.getF1News()
                Log.d("F1ViewModel", "Raw response: $response")

                if (response == null) {
                    Log.e("F1ViewModel", "Response is null")
                    _newsState.value = UiState.Error(Exception("No data received"))
                    return@launch
                }

                val articles = response
                Log.d("F1ViewModel", "Fetched ${articles.size} articles")

                if (articles.isEmpty()) {
                    Log.w("F1ViewModel", "No articles found in response")
                } else {
                    articles.forEachIndexed { index, article ->
                        Log.d("F1ViewModel", "Article $index: ${article.title}")
                        Log.d("F1ViewModel", "  Image: ${article.enclosure?.url}")
                        Log.d("F1ViewModel", "  Link: ${article.link}")
                    }
                }

                _newsState.value = UiState.Success(articles)

            } catch (e: Exception) {
                Log.e("F1ViewModel", "Error fetching news", e)
                Log.e("F1ViewModel", "Error message: ${e.message}")
                Log.e("F1ViewModel", "Error cause: ${e.cause}")
                _newsState.value = UiState.Error(e)
            }
        }
    }

    fun fetchCircuits(year: String = _currentYear.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("circuits_$year", year)) {
            return // Use cached data
        }

        viewModelScope.launch {
            _circuitsState.value = UiState.Loading
            try {
                val response = repository.getCircuits(year)
                _circuitsState.value = UiState.Success(response)
                markDataFetched("circuits_$year", year)
            } catch (e: Exception) {
                _circuitsState.value = UiState.Error(e)
            }
        }
    }

    fun fetchConstructors(year: String = _currentYear.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("constructors_$year", year)) {
            return
        }

        viewModelScope.launch {
            _constructorsState.value = UiState.Loading
            try {
                val response = repository.getConstructors(year)
                _constructorsState.value = UiState.Success(response)
                markDataFetched("constructors_$year", year)
            } catch (e: Exception) {
                _constructorsState.value = UiState.Error(e)
            }
        }
    }

    fun fetchConstructorStandings(year: String = _currentYear.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("constructor_standings_$year", year)) {
            return
        }

        viewModelScope.launch {
            _constructorStandingsState.value = UiState.Loading
            try {
                val response = repository.getConstructorStandings(year)
                _constructorStandingsState.value = UiState.Success(response)
                markDataFetched("constructor_standings_$year", year)
            } catch (e: Exception) {
                _constructorStandingsState.value = UiState.Error(e)
            }
        }
    }

    fun fetchDrivers(year: String = _currentYear.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("drivers_$year", year)) {
            return
        }

        viewModelScope.launch {
            _driversState.value = UiState.Loading
            try {
                val response = repository.getDrivers(year)
                _driversState.value = UiState.Success(response)
                markDataFetched("drivers_$year", year)
            } catch (e: Exception) {
                _driversState.value = UiState.Error(e)
            }
        }
    }

    fun fetchDriverStandings(year: String = _currentYear.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("driver_standings_$year", year)) {
            return
        }

        viewModelScope.launch {
            _driverStandingsState.value = UiState.Loading
            try {
                val response = repository.getDriverStandings(year)
                _driverStandingsState.value = UiState.Success(response)
                markDataFetched("driver_standings_$year", year)
            } catch (e: Exception) {
                _driverStandingsState.value = UiState.Error(e)
            }
        }
    }

    fun fetchQualifying(year: String = _currentYear.value, round: String = _currentRound.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("qualifying_${year}_$round", year)) {
            return
        }

        viewModelScope.launch {
            _qualifyingState.value = UiState.Loading
            try {
                val response = repository.getQualifying(year, round)
                _qualifyingState.value = UiState.Success(response)
                markDataFetched("qualifying_${year}_$round", year)
            } catch (e: Exception) {
                _qualifyingState.value = UiState.Error(e)
            }
        }
    }

    fun fetchRacesByRound(year: String = _currentYear.value, round: String = _currentRound.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("races_${year}_$round", year)) {
            return
        }

        viewModelScope.launch {
            // vvv THIS IS THE FIX vvv
            _racesRoundState.value = UiState.Loading
            // ^^^ THIS IS THE FIX ^^^
            try {
                val response = repository.getRacesByRound(year,round)
                _racesRoundState.value = UiState.Success(response)
                markDataFetched("races_${year}_$round", year)
            } catch (e: Exception) {
                _racesRoundState.value = UiState.Error(e)
            }
        }
    }

    fun fetchRaces(year: String = _currentYear.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("races_$year", year)) {
            return
        }

        viewModelScope.launch {
            _racesState.value = UiState.Loading
            try {
                val response = repository.getRaces(year)
                _racesState.value = UiState.Success(response)
                markDataFetched("races_$year", year)
            } catch (e: Exception) {
                _racesState.value = UiState.Error(e)
            }
        }
    }

    fun fetchResults(year: String = _currentYear.value, round: String = _currentRound.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("results_${year}_$round", year)) {
            return
        }

        viewModelScope.launch {
            _resultsState.value = UiState.Loading
            try {
                val response = repository.getResults(year, round)
                _resultsState.value = UiState.Success(response)
                markDataFetched("results_${year}_$round", year)
            } catch (e: Exception) {
                _resultsState.value = UiState.Error(e)
            }
        }
    }

    fun fetchResultsForRound(year: String, round: String) {
        // Check if this round's results are already in the map
        if (_raceResultsMap.value.containsKey(round)) {
            return // Already have results for this round
        }

        viewModelScope.launch {
            try {
                val response = repository.getResults(year, round)
                val results = response.MRData.RaceTable.Races.firstOrNull()?.Results ?: emptyList()

                // Update the map with new results
                _raceResultsMap.value = _raceResultsMap.value + (round to results)
            } catch (e: Exception) {
                // Handle error silently for individual races or log it
                Log.e("F1ViewModel", "Error fetching results for round $round: ${e.message}")
            }
        }
    }

    // Function to fetch results for multiple past races
    fun fetchResultsForPastRaces(year: String, rounds: List<String>) {
        viewModelScope.launch {
            // Only fetch rounds that aren't already in the map
            val roundsToFetch = rounds.filter { !_raceResultsMap.value.containsKey(it) }
            roundsToFetch.forEach { round ->
                fetchResultsForRound(year, round)
            }
        }
    }

    fun fetchAllResults(year: String, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("all_results_$year", year)) {
            return
        }

        viewModelScope.launch {
            _allResultsState.value = UiState.Loading
            try{
                val response = repository.getAllResults(year)
                _allResultsState.value = UiState.Success(response)
                markDataFetched("all_results_$year", year)
            } catch (e: Exception) {
                _allResultsState.value = UiState.Error(e)
            }
        }
    }

    fun fetchSprint(year: String = _currentYear.value, round: String = _currentRound.value, forceRefresh: Boolean = false) {
        if (!forceRefresh && isDataFetched("sprint_${year}_$round", year)) {
            return
        }

        viewModelScope.launch {
            _sprintState.value = UiState.Loading
            try {
                val response = repository.getSprint(year, round)
                _sprintState.value = UiState.Success(response)
                markDataFetched("sprint_${year}_$round", year)
            } catch (e: Exception) {
                _sprintState.value = UiState.Error(e)
            }
        }
    }

    fun fetchDriverDetails(year: String,driver_id: String, forceRefresh: Boolean = false) {
        if(!forceRefresh && isDataFetched("driver_details_$year", year)) {
            return
        }

        viewModelScope.launch {
            _driverDetailsState.value = UiState.Loading
            try {
                val response = repository.getDriverDetails(year,driver_id)
                _driverDetailsState.value = UiState.Success(response)

            } catch (e: Exception) {
                _driverDetailsState.value = UiState.Error(e)
            }
        }
    }

    fun fetchLapData(year: String = _currentYear.value, round: String = _currentRound.value, forceRefresh: Boolean = false) {
        // Leverage your existing caching layer
        if (!forceRefresh && isDataFetched("laps_${year}_$round", year)) {
            Log.d("F1ViewModel", "Using cached lap data for $year Round $round")
            return
        }

        viewModelScope.launch {
            _lapDataState.value = UiState.Loading
            try {
                val response = repository.getLapData(year, round)
                _lapDataState.value = UiState.Success(response)
                markDataFetched("laps_${year}_$round", year)
                Log.d("F1ViewModel", "Successfully fetched lap data for $year Round $round")
            } catch (e: Exception) {
                Log.e("F1ViewModel", "Error fetching lap data", e)
                _lapDataState.value = UiState.Error(e)
            }
        }
    }


    // Update current year and round
    fun setYear(year: String) {
        _currentYear.value = year
    }

    fun setRound(round: String) {
        _currentRound.value = round
    }

    // Convenience function to fetch all data for a specific year
    fun fetchAllDataForYear(year: String, forceRefresh: Boolean = false) {
        setYear(year)
        fetchCircuits(year, forceRefresh)
        fetchConstructors(year, forceRefresh)
        fetchConstructorStandings(year, forceRefresh)
        fetchDrivers(year, forceRefresh)
        fetchDriverStandings(year, forceRefresh)
        fetchRaces(year, forceRefresh)

    }

    // Convenience function to fetch all data for a specific race
    fun fetchAllDataForRace(year: String, round: String, forceRefresh: Boolean = false) {
        setYear(year)
        setRound(round)
        fetchQualifying(year, round, forceRefresh)
        fetchResults(year, round, forceRefresh)
        fetchSprint(year, round, forceRefresh)
        fetchLapData(year, round, forceRefresh)
    }

    // Optional: Function to manually clear cache
    fun clearCache() {
        cachedYear = null
        fetchedDataFlags.clear()
        lastFetchTime = 0
        _raceResultsMap.value = emptyMap()
    }

    // Optional: Function to force refresh all data
    fun refreshAllData(year: String) {
        clearCache()
        fetchAllDataForYear(year, forceRefresh = true)
    }
}