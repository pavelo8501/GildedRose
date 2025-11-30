package po.gildedrose.refactor

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import po.gildedrose.Item

@Serializable
class GRItem(
    @Transient
    private val itemName: String = "",
    @Transient
    private val itemSellIn: Int = 0,
    @Transient
    private val itemQuality: Int = 0,
    val itemGroup:ItemGroup = ItemGroup.Default
): Item(itemName, itemSellIn,itemQuality) {

   constructor(item: Item):this(item.name, item.quality, item.sellIn, parseNameToGroup(item.name))

    companion object{
        fun parseNameToGroup(name: String):ItemGroup{
            val group = when{
                name.contains("Aged Brie") ->  ItemGroup.AgedBrie
                name.contains("Elixir") -> ItemGroup.Elixir
                name.contains("Sulfuras")-> ItemGroup.Sulfuras
                name.contains("Backstage passes") -> ItemGroup.BackstagePasses
                else -> ItemGroup.Default
            }
            return group
        }
    }
}

fun Item.toGRItem(): GRItem = GRItem(this)
fun Collection<Item>.toGRItems(): List<GRItem> = map { it.toGRItem() }


