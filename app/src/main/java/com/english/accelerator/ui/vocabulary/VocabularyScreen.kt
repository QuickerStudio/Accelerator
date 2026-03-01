package com.english.accelerator.ui.vocabulary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.english.accelerator.data.sampleWords
import com.english.accelerator.ui.vocabulary.components.WordCard

@Composable
fun VocabularyScreen() {
    val currentIndex by remember { mutableIntStateOf(0) }
    val currentWord = sampleWords[currentIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        WordCard(word = currentWord)
    }
}
