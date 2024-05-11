import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.movies.model.Movie
import com.example.movies.workers.GetMoviesWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface GetMoviesRepository {
    val outputWorkInfo: Flow<WorkInfo?>
    fun getMovies(movieType: String)
    fun cancelWork()
}

class WorkManagerRepository(context: Context): GetMoviesRepository {
    private val workManager = WorkManager.getInstance(context)

    override val outputWorkInfo: Flow<WorkInfo?> = MutableStateFlow(null)
    override fun getMovies(movieType: String) {
        // Add WorkRequest to blur the image
        val getMoviesBuilder = OneTimeWorkRequestBuilder<GetMoviesWorker>()

        // Input the Uri for the blur operation along with the blur level
        getMoviesBuilder.setInputData(createInputDataForWorkRequest(movieType))

        workManager.enqueue(getMoviesBuilder.build())
    }


    override fun cancelWork() {
        workManager.cancelAllWork()
    }

    private fun createInputDataForWorkRequest(movieType: String): Data {
        val builder = Data.Builder()
        builder.putString("MOVIE_TYPE", movieType.toString())
        return builder.build()
    }

    private fun createInputDataForDetailedMovieRequest(movie: Movie): Data {
        val builder = Data.Builder()
        builder.putString("MOVIE_ID", movie.id.toString())
        return builder.build()
    }
}