package com.starwars.inventar.data

import kotlinx.serialization.Serializable

@Serializable
data class UserConfig(
    val companyName: String = "Logistik-Firma",
    val userName: String = "Spieler",
    val logoPreset: String = "CARGO", // CARGO, KUAT, CZERKA, CORELLIA, ALLIANCE, IMPERIUM
    val customLogoPath: String? = null,
    val deviceId: String = ""
)
