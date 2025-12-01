package po.gildedrose.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FixtureData(
    @SerialName("Day")
    val day: Int,
    @SerialName("Name")
    val name: String,
    @SerialName("SellIn")
    val sellIn: Int,
    @SerialName("Quality")
    val quality: Int,
)