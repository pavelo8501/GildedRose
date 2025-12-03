package po.gildedrose.refactor.conditions

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.item.Quality


/**
 * Represents a reusable update rule for a specific [ItemGroup].
 *
 * `UpdateCondition` encapsulates the logic that modifies the state of an [ItemRecord]
 * during the daily update step of the Gilded Rose inventory system.
 *
 * ## Purpose
 * Instead of embedding large conditional structures inside `GildedRose.updateQuality()`,
 * each item category provides its own independent update function. This makes the
 * behaviour:
 *
 * * Explicit
 * * Extendable
 * * Easy to test
 * * Fully decoupled from the application flow
 *
 * ## Usage
 * A condition is defined by specifying the [itemGroup] and a lambda that receives an
 * [ItemRecord] and updates its `sellIn` and `quality` values.
 *
 * Example:
 * ```
 * val normalItemCondition = UpdateCondition(ItemGroup.Default) { item ->
 *     val sellIn = item.sellIn - 1
 *     val quality = Quality(item.quality - 1).clamp()
 *     item.update(sellIn, quality)
 * }
 * ```
 *
 * Conditions are then collected inside `GildedRoseApp`, which selects the
 * appropriate rule based on each item’s [ItemGroup].
 *
 * @property itemGroup The category of item this condition applies to.
 * @property conditionalUpdater A function that performs the update logic for the item.
 */
class UpdateCondition(
    val itemGroup: ItemGroup,
    val conditionalUpdater: (ItemRecord) -> Unit
){
    /**
     * Applies this condition to the given [item].
     */
    fun update(item: ItemRecord){
        conditionalUpdater.invoke(item)
    }

    fun copy(useGroup: ItemGroup):UpdateCondition{
       return UpdateCondition(useGroup, conditionalUpdater)
    }

}