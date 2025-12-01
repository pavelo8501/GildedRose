package po.gildedrose.refactor.item

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import po.gildedrose.Item
import po.gildedrose.refactor.ItemGroup
import po.misc.data.strings.appendGroup

/**
 * A Gilded Rose domain wrapper that adapts the original [Item] class into the
 * standardized [ItemRecord] interface used by the refactored update system.
 *
 * `GRItem` introduces:
 * - Strongly typed item categorization via [ItemGroup]
 * - An overridable [update] method for mutation
 * - A serializable representation suitable for snapshots and test fixtures
 *
 * ## Why a Wrapper?
 * The original kata `Item` class is a simple data holder with public mutable fields.
 * By wrapping it in `GRItem`, we gain:
 *
 *  * A unified abstraction (`ItemRecord`)
 *  * Compatibility with JSON serialization (thanks to `@Serializable`)
 *  * Item-group detection for routing to correct update logic
 *  * Consistent and inspectable `toString()` output
 *
 * ## Construction
 * Instances can be created:
 *
 * * Directly using constructor arguments
 * * From an existing [Item]
 * * From any [ItemRecord] via the `invoke(item: ItemRecord)` factory
 *
 * The secondary constructor automatically detects the item's [ItemGroup] by name.
 *
 * @property itemName The base item's name (transient — not serialized)
 * @property itemSellIn The initial sell-in value (transient — not serialized)
 * @property itemQuality The initial quality value (transient — not serialized)
 * @property itemGroup The assigned logical group that determines update rules
 */
@Serializable
class GRItem(
    @Transient
    private val itemName: String = "",
    @Transient
    private val itemSellIn: Int = 0,
    @Transient
    private val itemQuality: Int = 0,
    override val itemGroup: ItemGroup
): Item(itemName, itemSellIn,itemQuality), ItemRecord {

    /**
     * Creates a `GRItem` by wrapping a raw kata [Item].
     * The [itemGroup] is inferred from the item name.
     */
   constructor(item: Item):this(item.name, itemSellIn =  item.sellIn, item.quality, parseNameToGroup(item.name))

    /**
     * Updates the mutable state of the underlying Gilded Rose item.
     */
    override fun update(sellIn: Int, quality: Int){
        super.sellIn = sellIn
        super.quality  = quality
    }

    override fun toString(): String {
       return buildString {
            append("GRItem")
            appendGroup('[', ']', ::name, ::quality, ::sellIn)
        }
    }

    companion object{

        /**
         * Wraps the given [ItemRecord] into a `GRItem`.
         * If it is already a `GRItem`, it is returned unchanged.
         */
        operator fun invoke(item: ItemRecord):GRItem{
           return when(item){
                is GRItem -> return item
                else -> GRItem(item.name, itemSellIn =  item.sellIn, item.quality, parseNameToGroup(item.name))
            }
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
                lowercasedName.contains("aged brie") -> ItemGroup.AgedBrie
                lowercasedName.contains("sulfuras")-> ItemGroup.Sulfuras
                lowercasedName.contains("backstage passes") -> ItemGroup.BackstagePasses
                lowercasedName.contains("conjured") -> ItemGroup.Conjured
                else -> ItemGroup.Default
            }
            return group
        }
    }
}


fun Item.toGRItem(): GRItem = GRItem(this)

fun Collection<Item>.toGRItems(): List<GRItem> = map { it.toGRItem() }

@JvmName("toGRItemsItemRecordReceiver")
fun Collection<ItemRecord>.toGRItems(): List<GRItem> = map { it as GRItem }