package com.example.movies.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.movies.R
import com.example.movies.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



private const val TAG = "GetDetailedMovieWorker"

class GetDetailedMovieWorker(ctx : Context, params : WorkerParameters) : CoroutineWorker(ctx, params) {

    //get ViewModel

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                //get movie id from input data
                val movieId = inputData.getString("MOVIE_ID")
                //get movie details from the API

                Result.success()
            } catch (throwable: Throwable) {
                Log.e(TAG, applicationContext.resources.getString(R.string.error_getting_movies), throwable)
                Result.failure()
            }
        }
    }


}