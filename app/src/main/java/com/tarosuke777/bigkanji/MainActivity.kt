package com.tarosuke777.bigkanji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarosuke777.bigkanji.ui.theme.BigKanjiAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BigKanjiAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KanjiMagnifierScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// --- State ---

@Stable
class KanjiMagnifierState(
    initialText: String = "",
    initialFontSize: Float = 100f,
    initialIsVertical: Boolean = false
) {
    var text by mutableStateOf(initialText)
    var fontSize by mutableFloatStateOf(initialFontSize)
    var isVertical by mutableStateOf(initialIsVertical)
    var history by mutableStateOf(listOf<String>())
        private set

    val displayText: String
        get() = if (isVertical) text.chunked(1).joinToString("\n") else text

    fun addToHistory() {
        if (text.isNotBlank() && !history.contains(text)) {
            history = (listOf(text) + history).take(10)
        }
    }
}

@Composable
fun rememberKanjiMagnifierState() = remember { KanjiMagnifierState() }

// --- UI Components ---

@Composable
fun KanjiMagnifierScreen(modifier: Modifier = Modifier) {
    val state = rememberKanjiMagnifierState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplayArea(
            text = state.displayText,
            fontSize = state.fontSize,
            isVertical = state.isVertical,
            modifier = Modifier.weight(1f)
        )

        HistorySection(
            history = state.history,
            onHistoryClick = { state.text = it }
        )

        InputSection(
            value = state.text,
            onValueChange = { state.text = it },
            onDone = {
                state.addToHistory()
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ControlSection(
            fontSize = state.fontSize,
            onFontSizeChange = { state.fontSize = it },
            isVertical = state.isVertical,
            onToggleOrientation = { state.isVertical = !state.isVertical }
        )
    }
}

@Composable
fun DisplayArea(
    text: String,
    fontSize: Float,
    isVertical: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            lineHeight = if (isVertical) fontSize.sp else TextUnit.Unspecified,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif,
            softWrap = false
        )
    }
}

@Composable
fun HistorySection(
    history: List<String>,
    onHistoryClick: (String) -> Unit
) {
    if (history.isNotEmpty()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(history) { item ->
                    AssistChip(
                        onClick = { onHistoryClick(item) },
                        label = { Text(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun InputSection(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("漢字を入力") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}

@Composable
fun ControlSection(
    fontSize: Float,
    onFontSizeChange: (Float) -> Unit,
    isVertical: Boolean,
    onToggleOrientation: () -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = fontSize,
                onValueChange = onFontSizeChange,
                valueRange = 50f..400f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = colorScheme.primary,
                    activeTrackColor = colorScheme.primaryContainer
                )
            )
        }

        Button(
            onClick = onToggleOrientation,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isVertical) colorScheme.secondary else colorScheme.primary,
                contentColor = colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (isVertical) "縦書き" else "横書き")
        }
    }
}