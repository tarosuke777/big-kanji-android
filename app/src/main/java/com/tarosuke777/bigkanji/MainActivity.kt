package com.tarosuke777.bigkanji

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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

@Composable
fun KanjiMagnifierScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    var fontSize by remember { mutableFloatStateOf(100f) }
    var isVertical by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = text,
            onValueChange = { newText -> text = newText },
            label = { Text("漢字を入力") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        val displayPoints = if (isVertical) {
            // 文字の間に改行(\n)を挟んで縦に見せる
            text.map { it }.joinToString("\n")
        } else {
            text
        }

        val horizontalScrollState = rememberScrollState()
        val verticalScrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
                .verticalScroll(verticalScrollState),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayPoints,
                fontSize = fontSize.sp,
                lineHeight = if (isVertical) fontSize.sp else TextUnit.Unspecified,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                softWrap = false
            )
        }


        Spacer(modifier = Modifier.height(20.dp))

        // 文字サイズ調整スライダー
        Slider(
            value = fontSize,
            onValueChange = { fontSize = it },
            valueRange = 50f..300f,
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,      // つまみの色
                activeTrackColor = Color.Black, // 選択されている側のバーの色
                inactiveTrackColor = Color.Gray // 選択されていない側のバーの色
            )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("表示方向:")
            Button(
                onClick = { isVertical = !isVertical },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVertical) Color(0xFF444444) else Color.Black,
                    contentColor = Color.White // 文字は白で見やすく
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isVertical) "縦書き中" else "横書き中")
            }
        }
    }
}
