package po.gildedrose.refactor.item

import po.gildedrose.refactor.ItemGroup


interface ItemRecord {
    val name: String
    val sellIn: Int
    val quality: Int
    val itemGroup: ItemGroup

    fun update(sellIn: Int, quality: Int)

    fun update(sellIn: Int, quality: Quality){
        update(sellIn, quality.value)
    }

}