package com.example.f1live.repository

import com.example.f1live.api.RaceX
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Centralized repository for accessing F1 data from Firestore.
 * Can be used across all screens/composables.
 */
object F1DataRepository {

    private val db = FirebaseFirestore.getInstance()

    // Cache for frequently accessed data (optional but recommended)
    private var circuitMapsCache: List<Circuit>? = null
    private var trackPhotosCache: List<Track>? = null
    private var driverPortraitsCache: List<F1DriverImage>? = null
    private var driverDetailsCache: List<DriverDetailsImg>? = null
    private var teamLogosCache: List<F1logo>? = null
    private var carsCache: List<F1Car>? = null

    /**
     * Get circuit map by circuit name or GP name
     */
    suspend fun getCircuitMap(circuitName: String? = null, gpName: String? = null): Circuit? {
        return try {
            if (circuitName != null) {
                val doc = db.collection("circuit_maps")
                    .document(circuitName)
                    .get()
                    .await()

                if (doc.exists()) {
                    Circuit(
                        gpName = doc.getString("gpName") ?: "",
                        circuitName = doc.getString("circuitName") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                } else null
            } else if (gpName != null) {
                val querySnapshot = db.collection("circuit_maps")
                    .whereEqualTo("gpName", gpName)
                    .limit(1)
                    .get()
                    .await()

                querySnapshot.documents.firstOrNull()?.let { doc ->
                    Circuit(
                        gpName = doc.getString("gpName") ?: "",
                        circuitName = doc.getString("circuitName") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                }
            } else null
        } catch (e: Exception) {
            println("Error fetching circuit map: ${e.message}")
            null
        }
    }

    /**
     * Get all circuit maps (with caching)
     */
    suspend fun getAllCircuitMaps(forceRefresh: Boolean = false): List<Circuit> {
        if (!forceRefresh && circuitMapsCache != null) {
            return circuitMapsCache!!
        }

        return try {
            val querySnapshot = db.collection("circuit_maps").get().await()
            val circuits = querySnapshot.documents.mapNotNull { doc ->
                try {
                    Circuit(
                        gpName = doc.getString("gpName") ?: "",
                        circuitName = doc.getString("circuitName") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            circuitMapsCache = circuits
            circuits
        } catch (e: Exception) {
            println("Error fetching circuit maps: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get track photo by circuit name or GP name (similar to circuit maps)
     */
    suspend fun getTrackPhoto(circuitName: String? = null, gpName: String? = null): Track? {
        return try {
            if (circuitName != null) {
                val doc = db.collection("track_photos")
                    .document(circuitName)
                    .get()
                    .await()

                if (doc.exists()) {
                    Track(
                        gpName = doc.getString("gpName") ?: "",
                        circuitName = doc.getString("circuitName") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                } else null
            } else if (gpName != null) {
                val querySnapshot = db.collection("track_photos")
                    .whereEqualTo("gpName", gpName)
                    .limit(1)
                    .get()
                    .await()

                querySnapshot.documents.firstOrNull()?.let { doc ->
                    Track(
                        gpName = doc.getString("gpName") ?: "",
                        circuitName = doc.getString("circuitName") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                }
            } else null
        } catch (e: Exception) {
            println("Error fetching track photo: ${e.message}")
            null
        }
    }

    /**
     * Get all track photos (with caching)
     */
    suspend fun getAllTrackPhotos(forceRefresh: Boolean = false): List<Track> {
        if (!forceRefresh && trackPhotosCache != null) {
            return trackPhotosCache!!
        }

        return try {
            val querySnapshot = db.collection("track_photos").get().await()
            val tracks = querySnapshot.documents.mapNotNull { doc ->
                try {
                    Track(
                        gpName = doc.getString("gpName") ?: "",
                        circuitName = doc.getString("circuitName") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            trackPhotosCache = tracks
            tracks
        } catch (e: Exception) {
            println("Error fetching track photos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get driver portrait image by driver name
     */
    suspend fun getDriverPortrait(driverName: String): F1DriverImage? {
        return try {
            val doc = db.collection("driver_images_portrait")
                .document(driverName)
                .get()
                .await()

            if (doc.exists()) {
                F1DriverImage(
                    name = doc.getString("name") ?: "",
                    imgUrl = doc.getString("imgUrl") ?: ""
                )
            } else null
        } catch (e: Exception) {
            println("Error fetching driver portrait: ${e.message}")
            null
        }
    }

    /**
     * Get all driver portraits (with caching)
     */
    suspend fun getAllDriverPortraits(forceRefresh: Boolean = false): List<F1DriverImage> {
        if (!forceRefresh && driverPortraitsCache != null) {
            return driverPortraitsCache!!
        }

        return try {
            val querySnapshot = db.collection("driver_images_portrait").get().await()
            val drivers = querySnapshot.documents.mapNotNull { doc ->
                try {
                    F1DriverImage(
                        name = doc.getString("name") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            driverPortraitsCache = drivers
            drivers
        } catch (e: Exception) {
            println("Error fetching driver portraits: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get driver detail image by driver name
     */
    suspend fun getDriverDetailImage(driverName: String): DriverDetailsImg? {
        return try {
            val doc = db.collection("driver_images_details")
                .document(driverName)
                .get()
                .await()

            if (doc.exists()) {
                DriverDetailsImg(
                    name = doc.getString("name") ?: "",
                    imgUrl = doc.getString("imgUrl") ?: ""
                )
            } else null
        } catch (e: Exception) {
            println("Error fetching driver detail image: ${e.message}")
            null
        }
    }

    /**
     * Get all driver detail images (with caching)
     */
    suspend fun getAllDriverDetailImages(forceRefresh: Boolean = false): List<DriverDetailsImg> {
        if (!forceRefresh && driverDetailsCache != null) {
            return driverDetailsCache!!
        }

        return try {
            val querySnapshot = db.collection("driver_images_details").get().await()
            val drivers = querySnapshot.documents.mapNotNull { doc ->
                try {
                    DriverDetailsImg(
                        name = doc.getString("name") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            driverDetailsCache = drivers
            drivers
        } catch (e: Exception) {
            println("Error fetching driver detail images: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get team logo by team name
     */
    suspend fun getTeamLogo(teamName: String): F1logo? {
        return try {
            val doc = db.collection("team_logos")
                .document(teamName)
                .get()
                .await()

            if (doc.exists()) {
                F1logo(
                    name = doc.getString("name") ?: "",
                    logoUrl = doc.getString("logoUrl") ?: ""
                )
            } else null
        } catch (e: Exception) {
            println("Error fetching team logo: ${e.message}")
            null
        }
    }

    /**
     * Get all team logos (with caching)
     */
    suspend fun getAllTeamLogos(forceRefresh: Boolean = false): List<F1logo> {
        if (!forceRefresh && teamLogosCache != null) {
            return teamLogosCache!!
        }

        return try {
            val querySnapshot = db.collection("team_logos").get().await()
            val logos = querySnapshot.documents.mapNotNull { doc ->
                try {
                    F1logo(
                        name = doc.getString("name") ?: "",
                        logoUrl = doc.getString("logoUrl") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            teamLogosCache = logos
            logos
        } catch (e: Exception) {
            println("Error fetching team logos: ${e.message}")
            emptyList()
        }
    }

    /**
     * Get F1 car by team name
     */
    suspend fun getF1Car(teamName: String): F1Car? {
        return try {
            val doc = db.collection("f1_cars")
                .document(teamName)
                .get()
                .await()

            if (doc.exists()) {
                F1Car(
                    name = doc.getString("name") ?: "",
                    imgUrl = doc.getString("imgUrl") ?: ""
                )
            } else null
        } catch (e: Exception) {
            println("Error fetching F1 car: ${e.message}")
            null
        }
    }

    /**
     * Get all F1 cars (with caching)
     */
    suspend fun getAllF1Cars(forceRefresh: Boolean = false): List<F1Car> {
        if (!forceRefresh && carsCache != null) {
            return carsCache!!
        }

        return try {
            val querySnapshot = db.collection("f1_cars").get().await()
            val cars = querySnapshot.documents.mapNotNull { doc ->
                try {
                    F1Car(
                        name = doc.getString("name") ?: "",
                        imgUrl = doc.getString("imgUrl") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            carsCache = cars
            cars
        } catch (e: Exception) {
            println("Error fetching F1 cars: ${e.message}")
            emptyList()
        }
    }

    /**
     * Search for track photo by raceName or circuitName (fuzzy matching)
     * Useful for RaceCard component
     */
    suspend fun findTrackPhotoByRace(raceName: String, circuitName: String): String? {
        val tracks = getAllTrackPhotos()
        return tracks.firstOrNull { track ->
            raceName.contains(track.gpName, ignoreCase = true) ||
                    circuitName.contains(track.circuitName, ignoreCase = true)
        }?.imgUrl
    }

    /**
     * Clear all caches (useful when you update data in Firestore)
     */
    fun clearCache() {
        circuitMapsCache = null
        trackPhotosCache = null
        driverPortraitsCache = null
        driverDetailsCache = null
        teamLogosCache = null
        carsCache = null
    }

    /**
     * Preload all data at app startup (recommended for better UX)
     */
    suspend fun preloadAllData() {
        try {
            getAllCircuitMaps()
            getAllTrackPhotos()
            getAllDriverPortraits()
            getAllDriverDetailImages()
            getAllTeamLogos()
            getAllF1Cars()
            println("All F1 data preloaded successfully")
        } catch (e: Exception) {
            println("Error preloading data: ${e.message}")
        }
    }
}

/**
 * Extension function to get track image URL directly from race data
 */
suspend fun RaceX.getTrackImageUrl(): String? {
    return F1DataRepository.findTrackPhotoByRace(
        raceName = this.raceName,
        circuitName = this.Circuit.circuitName
    )
}