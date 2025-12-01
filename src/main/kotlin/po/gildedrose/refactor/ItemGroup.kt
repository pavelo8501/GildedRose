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
        fun itemGroup(displayName: String):ItemGroup{
            return ItemGroup.entries.firstOrNull { it.displayName == displayName }?: Default
        }
    }
}