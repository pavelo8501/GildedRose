package po.gildedrose.refactor.item

import po.gildedrose.refactor.ItemGroup


interface ItemRecord {
    val id: Long
    val name: String
    var sellIn: Int
    var quality: Int
    val itemGroup: ItemGroup

    fun setItemId(itemId: Long):ItemRecord
    fun update(sellIn: Int, quality: Int)

    fun update(sellIn: Int, quality: Quality){
        update(sellIn, quality.value)
    }
}