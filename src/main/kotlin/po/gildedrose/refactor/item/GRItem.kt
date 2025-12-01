package po.gildedrose.refactor.item

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import po.gildedrose.Item
import po.gildedrose.refactor.ItemGroup
import po.misc.data.strings.appendGroup

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

   constructor(item: Item):this(item.name, itemSellIn =  item.sellIn, item.quality, parseNameToGroup(item.name))
   constructor(item: ItemRecord):this(item.name, itemSellIn =  item.sellIn, item.quality, parseNameToGroup(item.name))

    companion object{
        fun parseNameToGroup(name: String): ItemGroup {
            val group = when{
                name.contains("Aged Brie") ->  ItemGroup.AgedBrie
                name.contains("Elixir") -> ItemGroup.Elixir
                name.contains("Sulfuras")-> ItemGroup.Sulfuras
                name.contains("Backstage passes") -> ItemGroup.BackstagePasses
                name.contains("Conjured") -> ItemGroup.Conjured
                else -> ItemGroup.Default
            }
            return group
        }
    }

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
}

fun Item.toGRItem(): GRItem = GRItem(this)

//fun ItemRecord.toGRItem(): GRItem = this as GRItem

fun Collection<Item>.toGRItems(): List<GRItem> = map { it.toGRItem() }

@JvmName("toGRItemsItemRecordReceiver")
fun Collection<ItemRecord>.toGRItems(): List<GRItem> = map { it as GRItem }