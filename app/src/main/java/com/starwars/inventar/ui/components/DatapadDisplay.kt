package com.starwars.inventar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.starwars.inventar.data.ArticleStatus
import com.starwars.inventar.data.ContractStatus
import com.starwars.inventar.ui.theme.DatapadTheme
import com.starwars.inventar.ui.theme.HoloAmber
import com.starwars.inventar.ui.theme.HoloCyan
import com.starwars.inventar.ui.theme.HoloGreen
import com.starwars.inventar.ui.theme.HoloRed
import com.starwars.inventar.ui.theme.TextDim

/** Abgeschrägte Ecken oben links und unten rechts: das Datapad-Markenzeichen. */
val PanelShape = CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp)
val SmallCutShape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)

/** Holografisches Panel mit Rahmen und leichtem Farbverlauf. Mit onClick wird es antippbar. */
@Composable
fun HoloPanel(
    modifier: Modifier = Modifier,
    accent: Color = HoloAmber,
    title: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardBg = Color(0xFF151820).copy(alpha = 0.7f)
    val cardBorder = Color(0xFF5A6478)

    val base = modifier
        .fillMaxWidth()
        .clip(PanelShape)
        .background(cardBg)
        .border(1.dp, cardBorder, PanelShape)
    val interactive = if (onClick != null) base.clickable(onClick = onClick) else base

    Column(
        modifier = interactive.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (title != null) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = HoloAmber
            )
        }
        content()
    }
}

@Composable
fun StatusBadge(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.7f), CutCornerShape(topStart = 6.dp, bottomEnd = 6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/** Zeile mit Bezeichnung links und Wert rechts. */
@Composable
fun DataRow(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = Color.White) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.LightGray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor
        )
    }
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
    }
}

fun articleStatusColor(status: ArticleStatus): Color = when (status) {
    ArticleStatus.BEREIT -> HoloGreen
    ArticleStatus.IM_EINSATZ -> HoloCyan
    ArticleStatus.BESCHAEDIGT -> HoloRed
    ArticleStatus.IN_LAGER -> HoloAmber
    ArticleStatus.VERSCHOLLEN -> TextDim
}

fun contractStatusColor(status: ContractStatus): Color = when (status) {
    ContractStatus.ENTWURF -> TextDim
    ContractStatus.LAUFEND -> HoloGreen
    ContractStatus.ERFUELLT -> HoloCyan
    ContractStatus.GESCHEITERT -> HoloRed
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1017)
@Composable
private fun DatapadDisplayPreview() {
    DatapadTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HoloPanel(title = "Statusbericht") {
                DataRow("Artikel (Positionen)", "12")
                DataRow("Stückzahl gesamt", "48")
                DataRow("Warenwert", "12.500 cr", valueColor = HoloAmber)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge("Einsatzbereit", HoloGreen)
                StatusBadge("Im Einsatz", HoloCyan)
                StatusBadge("Beschädigt", HoloRed)
            }
            EmptyState("Keine Daten vorhanden")
        }
    }
}
