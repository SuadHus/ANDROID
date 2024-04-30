package com.example.movies.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieVideoListResponse (
    @SerialName("id")
    val id: Int,

    @SerialName("results")
    val results: List<MovieVideo>,
)