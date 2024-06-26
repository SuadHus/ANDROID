package com.example.movies.model

import Review
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewListResponse(

    @SerialName(value = "page")
    var page: Int = 0,

    @SerialName(value = "results")
    var results: List<Review> = listOf(),

    @SerialName(value = "total_pages")
    var totalPages: Int = 0,

    @SerialName(value = "total_results")
    var totalResults: Int = 0,

)
