package po.gildedrose.refactor.item

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import po.gildedrose.Item
import po.gildedrose.refactor.ItemGroup
import po.misc.data.appendGroup


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
    override val itemGroup: ItemGroup,
): Item(itemName, itemSellIn,itemQuality), ItemRecord {

    /**
     * Creates a `GRItem` by wrapping a raw kata [Item].
     * The [itemGroup] is inferred from the item name.
     */
   constructor(item: Item):this(item.name, item.sellIn, item.quality, ItemGroup.parseNameToGroup(item.name))

    override var id: Long = 0
        private set


    override fun setItemId(itemId: Long):GRItem{
         id = itemId
        return this
    }

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
            appendGroup("[", "]", ::id, ::name, ::quality, ::sellIn)
        }
    }
    companion object{
        /**
         * Wraps the given [ItemRecord] into a [GRItem].
         *
         * - If [item] is already a [GRItem], it is returned unchanged.
         * - If [item] is an [Item], the secondary constructor `GRItem(Item)` is used.
         * - Otherwise, a new [GRItem] is created using the interface-based parameters.
         *
         * This method is intentionally defensive and accepts all [ItemRecord] implementations.
         */
        operator fun invoke(item: ItemRecord):GRItem{
           return when(item){
                is GRItem -> item
                is Item -> GRItem(item).setItemId(item.id)
                else -> {
                    val item = GRItem(item.name, item.sellIn, item.quality, ItemGroup.parseNameToGroup(item.name))
                    item.setItemId(item.id)
                }
            }
        }
    }
}

fun Item.toGRItem(): GRItem = GRItem(this)

fun Collection<Item>.toGRItems(): List<GRItem>{
    val grItems =  map { it.toGRItem() }
    return grItems.differentiateItems()
}
