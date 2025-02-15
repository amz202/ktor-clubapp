package com.example.data.model.Requests

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ClubEventsRequest(val clubId: String)