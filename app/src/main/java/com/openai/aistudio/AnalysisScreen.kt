package com.openai.aistudio

import AnalysisViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel) {
    // Observe the state of analysisData
    val analysisData by viewModel.analysisData // Observe analysis data directly using State

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Analysis") }
            )
        }
    ) { paddingValues ->
        if (analysisData == null) {
            // Display a loading or empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Apply paddingValues here
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Fetching analysis data...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            // Display the analysis data
            // Show analysis content
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues) // Apply paddingValues here to avoid overlap with TopAppBar
                    .padding(16.dp), // Additional inner padding for content spacing
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Display each analysis part
                Text("CEO Strategic Analysis", style = MaterialTheme.typography.titleMedium)
                Text(analysisData?.ceoAnalysis ?: "No data available")  // Safely access the property

                Divider()

                Text("CTO Technical Specifications", style = MaterialTheme.typography.titleMedium)
                Text(analysisData?.ctoAnalysis ?: "No data available")

                Divider()

                Text("Project Manager's Plan", style = MaterialTheme.typography.titleMedium)
                Text(analysisData?.pmPlan ?: "No data available")

                Divider()

                Text("Developer's Implementation Strategy", style = MaterialTheme.typography.titleMedium)
                Text(analysisData?.devPlan ?: "No data available")

                Divider()

                Text("Client Success Strategy", style = MaterialTheme.typography.titleMedium)
                Text(analysisData?.clientAnalysis ?: "No data available")
            }
        }
    }
}
