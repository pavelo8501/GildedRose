package po.gildedrose.refactor

import kotlinx.serialization.Serializable

/**
 * Logical categories of items within the Gilded Rose system.
 *
 * Each group corresponds to a specific update rule (see `UpdateCondition`).
 * The enum is annotated with `@Serializable` to support snapshot generation.
 *
 * @property displayName A human-readable label used in snapshots and tests.
 */
@Serializable
enum class ItemGroup(val displayName: String) {
    Default(""),
    AgedBrie("Aged brie"),
    Sulfuras("Sulfuras"),
    Conjured("Conjured"),
    BackstagePasses("Backstage passes");

    companion object{

        fun resolveGroup(displayName: String):ItemGroup{
            return ItemGroup.entries.firstOrNull { it.displayName == displayName }?: Default
        }

        /**
         * Attempts to infer the logical [ItemGroup] from the item name.
         * This heuristic is intentionally simple and case-insensitive.
         *
         * Unknown names default to [ItemGroup.Default].
         */
        fun parseNameToGroup(name: String): ItemGroup {
            val lowercasedName = name.lowercase()
            val group = when{
                lowercasedName.contains("conjured") -> ItemGroup.Conjured
                lowercasedName.contains("aged brie") -> ItemGroup.AgedBrie
                lowercasedName.contains("sulfuras")-> ItemGroup.Sulfuras
                lowercasedName.contains("backstage passes") -> ItemGroup.BackstagePasses
                else -> ItemGroup.Default
            }
            return group
        }
    }
}