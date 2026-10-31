package com.starwars.inventar.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.starwars.inventar.ui.theme.DatapadBg
import com.starwars.inventar.ui.theme.DatapadBgTop
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloCyan
import com.starwars.inventar.ui.theme.HoloGreen
import com.starwars.inventar.ui.theme.TextDim

/** Dunkler Verlauf mit feinen Scanlines, wie ein Röhren-Display. */
@Composable
fun DatapadBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DatapadBgTop, DatapadBg)))
            .drawWithContent {
                drawContent()
                val step = 4.dp.toPx()
                var y = 0f
                while (y < size.height) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.025f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                    y += step
                }
            },
        content = content
    )
}

/** Grundgerüst jedes Bildschirms: Datapad-Hintergrund, Kopfzeile und Inhalt. */
@Composable
fun DatapadScreen(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    DatapadBackground(modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { DatapadTopBar(title, subtitle, onBack, actions) },
            floatingActionButton = floatingActionButton,
            content = content
        )
    }
}

@Composable
private fun DatapadTopBar(
    title: String,
    subtitle: String?,
    onBack: (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "statusLight")
    val pulse by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "statusLightAlpha"
    )

    Column(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Zurück",
                        tint = HoloCyan
                    )
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = if (onBack == null) 4.dp else 0.dp)
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = HoloCyan
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDim
                    )
                }
            }
            actions()
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .size(8.dp)
                    .alpha(pulse)
                    .background(HoloGreen, CircleShape)
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Brush.horizontalGradient(listOf(HoloCyan, HoloCyan.copy(alpha = 0.05f))))
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DatapadScreenPreview() {
    DatapadTheme {
        DatapadScreen(
            title = "Datapad",
            subtitle = "Inventar der Produktion"
        ) { padding ->
            Box(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Inhalt des Datapads...", color = HoloCyan)
            }
        }
    }
}

