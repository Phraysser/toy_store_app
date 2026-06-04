package com.toystore.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToyDao {

    @Query("SELECT * FROM toys_cache ORDER BY name")
    fun getAllToys(): Flow<List<CachedToy>>

    @Query("SELECT * FROM toys_cache WHERE id = :toyId")
    suspend fun getToyById(toyId: Long): CachedToy?

    @Query("SELECT * FROM toys_cache WHERE LOWER(name) LIKE LOWER('%' || :query || '%') OR LOWER(category) LIKE LOWER('%' || :query || '%')")
    fun searchToys(query: String): Flow<List<CachedToy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToys(toys: List<CachedToy>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToy(toy: CachedToy)

    @Delete
    suspend fun deleteToy(toy: CachedToy)

    @Query("DELETE FROM toys_cache WHERE id = :toyId")
    suspend fun deleteToyById(toyId: Long)

    @Query("DELETE FROM toys_cache")
    suspend fun clearAllToys()

    @Query("SELECT COUNT(*) FROM toys_cache")
    suspend fun getCachedCount(): Int
}