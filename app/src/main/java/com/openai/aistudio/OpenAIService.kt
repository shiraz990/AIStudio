package com.openai.aistudio

import OpenAIRequest
import OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIService {
    @POST("v1/chat/completions")
    suspend fun getAnalysis(@Body request: OpenAIRequest): OpenAIResponse
}
