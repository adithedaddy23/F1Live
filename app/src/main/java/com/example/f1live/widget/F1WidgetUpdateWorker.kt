package com.example.f1live.widget

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.f1live.api.RaceX
import com.example.f1live.repository.F1DataRepository
import com.example.f1live.repository.F1Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
class F1WidgetUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = F1Repository()

    override suspend fun doWork(): Result {
        return try {
            val year = LocalDate.now().year.toString()

            // Fetch races from API
            val racesResponse = repository.getRaces(year)
            val races = racesResponse.MRData.RaceTable.Races

            // Find current or upcoming race
            val raceInfo = findCurrentOrUpcomingRace(races)

            // Fetch track image if race found
            val trackImage = raceInfo?.let { info ->
                val race = races.find { it.round == info.round }
                race?.let {
                    F1DataRepository.findTrackPhotoByRace(
                        raceName = it.raceName,
                        circuitName = it.Circuit.circuitName
                    )
                }
            }

            // Save to DataStore
            F1WidgetDataManager.saveRaceData(applicationContext, raceInfo)
            F1WidgetDataManager.saveTrackImage(applicationContext, trackImage)

            // Update all widgets
            F1GrandPrixWidget().updateAll(applicationContext)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun findCurrentOrUpcomingRace(races: List<RaceX>): F1WidgetRaceInfo? {
        val today = LocalDate.now(ZoneId.systemDefault())

        // First check for ongoing race (Friday to Monday)
        for (race in races) {
            val raceDate = LocalDate.parse(race.date)
            val raceWeekendStart = raceDate.minusDays(2) // Friday
            val raceWeekendEnd = raceDate.plusDays(1)   // Monday

            if (today in raceWeekendStart..raceWeekendEnd) {
                return createRaceInfo(race, today, isOngoing = true)
            }
        }

        // Find next upcoming race
        val upcomingRace = races
            .filter { LocalDate.parse(it.date).isAfter(today) }
            .minByOrNull { LocalDate.parse(it.date) }

        return upcomingRace?.let { createRaceInfo(it, today, isOngoing = false) }
    }

    private fun createRaceInfo(race: RaceX, today: LocalDate, isOngoing: Boolean): F1WidgetRaceInfo {
        val raceDate = LocalDate.parse(race.date)
        val raceTime = race.time?.let { LocalTime.parse(it.take(5)) } ?: LocalTime.MIDNIGHT
        val utcDateTime = LocalDateTime.of(raceDate, raceTime)
        val zonedUtcDateTime = utcDateTime.atZone(ZoneId.of("UTC"))
        val localZoneId = ZoneId.systemDefault()
        val localZonedDateTime = zonedUtcDateTime.withZoneSameInstant(localZoneId)

        val formattedDate = localZonedDateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        val formattedTime = if (race.time != null) {
            localZonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else null

        val daysUntil = if (!isOngoing) {
            val days = ChronoUnit.DAYS.between(today, raceDate)
            when {
                days == 0L -> "Today!"
                days == 1L -> "Tomorrow"
                days < 7 -> "In $days days"
                else -> "In ${days / 7} weeks"
            }
        } else ""

        val ongoingStatus = if (isOngoing) {
            val dayOfWeek = today.dayOfWeek
            when (dayOfWeek) {
                java.time.DayOfWeek.FRIDAY -> "Practice Sessions Today"
                java.time.DayOfWeek.SATURDAY -> "Qualifying Today"
                java.time.DayOfWeek.SUNDAY -> "Race Day!"
                java.time.DayOfWeek.MONDAY -> "Results Available"
                else -> "Race Weekend"
            }
        } else ""

        return F1WidgetRaceInfo(
            raceName = race.raceName,
            circuitName = race.Circuit.circuitName,
            location = "${race.Circuit.Location.locality}, ${race.Circuit.Location.country}",
            formattedDate = formattedDate,
            formattedTime = formattedTime,
            isOngoing = isOngoing,
            ongoingStatus = ongoingStatus,
            daysUntil = daysUntil,
            round = race.round
        )
    }
}