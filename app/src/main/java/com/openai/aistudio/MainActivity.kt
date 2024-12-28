@file:OptIn(ExperimentalMaterial3Api::class)

package com.openai.aistudio

import AnalysisData
import AnalysisViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openai.aistudio.ui.theme.AIStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIStudioTheme {
                AppNavigation()
            }
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: AnalysisViewModel = viewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        // Pass innerPadding to the NavHost's Modifier
        NavHost(
            navController = navController,
            startDestination = "formScreen",
            modifier = Modifier.padding(innerPadding) // Apply content padding here
        ) {
            composable("formScreen") {
                ProjectFormScreen(
                    viewModel = viewModel,
                    onSubmit = {
                        navController.navigate("analysisScreen")
                    }
                )
            }
            composable("analysisScreen") {
                AnalysisScreen(viewModel = viewModel)
            }
        }
    }
}




@Composable
fun ExposedDropdownMenuField(
    label: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedItem) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedText,
            onValueChange = { selectedText = it },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        selectedText = item
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ProjectFormScreen(
    viewModel: AnalysisViewModel,
    modifier: Modifier = Modifier,
    onSubmit: () -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var projectType by remember { mutableStateOf("") }
    var budgetRange by remember { mutableStateOf("") }
    var timeline by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var technicalRequirements by remember { mutableStateOf("") }
    var specialConsiderations by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showApiKeyField by remember { mutableStateOf(false) }

    val analysisData by viewModel.analysisData
    val apiKey by viewModel.apiKey // Get API key directly from ViewModel

    // Check if all required fields are filled
    val isFormValid = apiKey.isNotEmpty() &&
            projectName.isNotEmpty() &&
            projectDescription.isNotEmpty() &&
            projectType.isNotEmpty() &&
            budgetRange.isNotEmpty() &&
            timeline.isNotEmpty() &&
            priority.isNotEmpty() &&
            !isLoading
    // Navigate to the next screen when analysis data is received
    LaunchedEffect(analysisData) {
        if (analysisData != null && isLoading) {
            isLoading = false
            onSubmit()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Studio") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Toggle visibility for API Key input
            Button(onClick = { showApiKeyField = !showApiKeyField
                viewModel.setApiKey(viewModel.apiKey.value)
            }) {
                Text(if (showApiKeyField) "Hide API Key" else "Set API Key")
            }

            if (showApiKeyField) {

                OutlinedTextField(
                    value = viewModel.apiKey.value,
                    onValueChange = { viewModel.setApiKey(it) }, // Update the ViewModel directly
                    label = { Text("API Key") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        viewModel.setApiKey(viewModel.apiKey.value)
                        showApiKeyField = false // Hide the field after setting the key
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save API Key")
                }
            }

            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = projectDescription,
                onValueChange = { projectDescription = it },
                label = { Text("Project Description") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuField(
                label = "Project Type",
                items = listOf("Mobile Application", "Website", "Desktop Application"),
                selectedItem = projectType,
                onItemSelected = { projectType = it }
            )
            ExposedDropdownMenuField(
                label = "Budget Range",
                items = listOf("$100k-$250k", "$250k-$350k", "$350k+"),
                selectedItem = budgetRange,
                onItemSelected = { budgetRange = it }
            )
            OutlinedTextField(
                value = timeline,
                onValueChange = { timeline = it },
                label = { Text("Expected Timeline") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuField(
                label = "Priority",
                items = listOf("High", "Low"),
                selectedItem = priority,
                onItemSelected = { priority = it }
            )
            OutlinedTextField(
                value = technicalRequirements,
                onValueChange = { technicalRequirements = it },
                label = { Text("Technical Requirements (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = specialConsiderations,
                onValueChange = { specialConsiderations = it },
                label = { Text("Special Considerations (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    isLoading = true
                    viewModel.fetchAndSetAnalysis(
                        projectType = projectType,
                        budget = budgetRange,
                        description = projectDescription,
                        technicalRequirements = technicalRequirements
                    )
                },
                modifier = Modifier.align(Alignment.End),
                enabled = isFormValid // Use the isFormValid flag
            ) {
                Text(if (isLoading) "Loading..." else "Submit")
            }
        }
    }
}







