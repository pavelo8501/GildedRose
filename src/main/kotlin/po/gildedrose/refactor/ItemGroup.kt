package po.gildedrose.refactor

import kotlinx.serialization.Serializable
import po.misc.dsl.configurator.ConfigPriority


@Serializable
enum class ItemGroup(val displayName: String) {
    Default(""),
    Elixir("Elixir"),
    AgedBrie("Aged brie"),
    Sulfuras("Sulfuras"),
    BackstagePasses("Backstage passes");

    companion object{

        fun itemGroup(displayName: String):ItemGroup{
            return ItemGroup.entries.firstOrNull { it.displayName == displayName }?: Default
        }
    }
}