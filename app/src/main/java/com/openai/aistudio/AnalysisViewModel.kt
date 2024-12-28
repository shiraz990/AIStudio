import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.openai.aistudio.BuildConfig
import com.openai.aistudio.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.openai.aistudio.RetrofitInstance.service
import kotlinx.coroutines.delay

// Define data model for API request
data class Message(val role: String, val content: String)

data class OpenAIRequest(val messages: List<Message>,  val model: String )
data class OpenAIResponse(val choices: List<Choice>)
data class Choice(val message: Message)

// Define data class to hold analysis results
data class AnalysisData(
    val ceoAnalysis: String,
    val ctoAnalysis: String,
    val pmPlan: String,
    val devPlan: String,
    val clientAnalysis: String
)

class AnalysisViewModel : ViewModel() {
    private val _analysisData = mutableStateOf<AnalysisData?>(null)
    val analysisData: State<AnalysisData?> get() = _analysisData

    private val _apiKey = mutableStateOf(BuildConfig.OPENAI_API_KEY)
    val apiKey: State<String> get() = _apiKey // Expose API key as State

    fun setApiKey(key: String) {
        _apiKey.value = key // Update the API key in ViewModel
        RetrofitInstance.setApiKey(key) // Set API key for RetrofitInstance // Set API key for RetrofitInstance
        Log.d("API_KEY", "API Key: ${_apiKey.value}")

    }

    // Function to fetch analysis using OpenAI API
    fun fetchAndSetAnalysis(
        projectType: String,
        budget: String,
        description: String,
        technicalRequirements: String
    ) {
        // Launch coroutine in the ViewModel scope to fetch data asynchronously
        viewModelScope.launch {
            try {
                val data = fetchAnalysis(projectType, budget, description, technicalRequirements)
                _analysisData.value = data // Update the UI state with the result
            } catch (e: Exception) {
                // Handle the error (e.g., log it or update state with an error message)
                _analysisData.value = null
                e.printStackTrace()
            }
        }
    }

    // Function to fetch analysis data from OpenAI API
    suspend fun fetchAnalysis(
        projectType: String,
        budget: String,
        description: String,
        technicalRequirements: String
    ): AnalysisData = withContext(Dispatchers.IO) {
        delay(1000) // Simulating network delay
        val model = "gpt-3.5-turbo"

        // Create requests for each role (CEO, CTO, PM, Developer, Client Success)
        val ceoRequest = OpenAIRequest(
            messages = listOf(
                Message("system", "You are an expert CEO providing project insights."),
                Message("user", "As a CEO, analyze the following project details and provide your recommendations or strategy:\n\n" +
                        "Project Type: $projectType\n" +
                        "Budget: $budget\n" +
                        "Scope: $description\n" +
                        "Technical Requirements: $technicalRequirements")
            ),
            model = model

        )

        val ctoRequest = OpenAIRequest(
            messages = listOf(
                Message("system", "You are an expert CTO providing technical specifications."),
                Message("user", "As a CTO, analyze the following project details and provide your technical specifications:\n\n" +
                        "Project Type: $projectType\n" +
                        "Budget: $budget\n" +
                        "Scope: $description\n" +
                        "Technical Requirements: $technicalRequirements")
            ),
            model = model // Add the model parameter

        )

        // Similarly, prepare requests for PM, Developer, and Client Success Strategy
        val pmRequest = OpenAIRequest(
            messages = listOf(
                Message("system", "You are an expert Project Manager providing project plans."),
                Message("user", "As a Project Manager, analyze the following project details and provide your plan:\n\n" +
                        "Project Type: $projectType\n" +
                        "Budget: $budget\n" +
                        "Scope: $description\n" +
                        "Technical Requirements: $technicalRequirements")
            ),
            model = model // Add the model parameter

        )

        val devRequest = OpenAIRequest(
            messages = listOf(
                Message("system", "You are an expert Developer providing implementation strategies."),
                Message("user", "As a Developer, analyze the following project details and provide your implementation strategy:\n\n" +
                        "Project Type: $projectType\n" +
                        "Budget: $budget\n" +
                        "Scope: $description\n" +
                        "Technical Requirements: $technicalRequirements")
            ),
            model = model // Add the model parameter

        )

        val clientRequest = OpenAIRequest(
            messages = listOf(
                Message("system", "You are an expert Client Success Manager providing success strategies."),
                Message("user", "As a Client Success Manager, analyze the following project details and provide your strategy:\n\n" +
                        "Project Type: $projectType\n" +
                        "Budget: $budget\n" +
                        "Scope: $description\n" +
                        "Technical Requirements: $technicalRequirements")
            ),
            model = model // Add the model parameter

        )

        // Make API calls for each request
        val ceoResponse = service.getAnalysis(ceoRequest)
        val ctoResponse = service.getAnalysis(ctoRequest)
        val pmResponse = service.getAnalysis(pmRequest)
        val devResponse = service.getAnalysis(devRequest)
        val clientResponse = service.getAnalysis(clientRequest)

        // Map the responses to AnalysisData
        return@withContext AnalysisData(
            ceoAnalysis = ceoResponse.choices.first().message.content,
            ctoAnalysis = ctoResponse.choices.first().message.content,
            pmPlan = pmResponse.choices.first().message.content,
            devPlan = devResponse.choices.first().message.content,
            clientAnalysis = clientResponse.choices.first().message.content
        )
    }

    // Function to update analysis data
    fun updateAnalysisData(analysisData: AnalysisData) {
        _analysisData.value = analysisData
    }
}
