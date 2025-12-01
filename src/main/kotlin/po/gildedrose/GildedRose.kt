package po.gildedrose

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.application.GildedRoseApp
import po.gildedrose.refactor.conditions.UpdateCondition
import po.gildedrose.refactor.conditions.agedBrieCondition
import po.gildedrose.refactor.conditions.backStageItemCondition
import po.gildedrose.refactor.conditions.conjuredItemCondition
import po.gildedrose.refactor.conditions.normalItemCondition
import po.gildedrose.refactor.conditions.sulfrasItemCondition
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.ItemRecord
import po.misc.data.output.output
import po.misc.data.styles.Colour



class GildedRose(val items: List<ItemRecord>,  conditions: List<UpdateCondition> = emptyList()): GildedRoseApp{


//   constructor(itemRecords: List<ItemRecord>,  updateConditions: List<UpdateCondition>):this(items = emptyList(), conditions = updateConditions){
//       grItems.addAll(itemRecords.toGRItems())
//   }
//
    private var grItems = mutableListOf<GRItem>()
    private val defaultConditions = listOf(
        normalItemCondition,
        sulfrasItemCondition,
        agedBrieCondition,
        conjuredItemCondition,
        backStageItemCondition
    )

    private val usedConditions: List<UpdateCondition> = conditions.ifEmpty {
        defaultConditions
    }


    internal fun updateQualityLegacy(){
        for (i in items.indices) {
            if (items[i].name != "Aged Brie" && items[i].name != "Backstage passes to a TAFKAL80ETC concert") {
                if (items[i].quality > 0) {
                    if (items[i].name != "Sulfuras, Hand of Ragnaros") {
                        items[i].quality = items[i].quality - 1
                    }
                }
            } else {
                if (items[i].quality < 50) {
                    items[i].quality = items[i].quality + 1

                    if (items[i].name == "Backstage passes to a TAFKAL80ETC concert") {
                        if (items[i].sellIn < 11) {
                            if (items[i].quality < 50) {
                                items[i].quality = items[i].quality + 1
                            }
                        }

                        if (items[i].sellIn < 6) {
                            if (items[i].quality < 50) {
                                items[i].quality = items[i].quality + 1
                            }
                        }
                    }
                }
            }

            if (items[i].name != "Sulfuras, Hand of Ragnaros") {
                items[i].sellIn = items[i].sellIn - 1
            }

            if (items[i].sellIn < 0) {
                if (items[i].name != "Aged Brie") {
                    if (items[i].name != "Backstage passes to a TAFKAL80ETC concert") {
                        if (items[i].quality > 0) {
                            if (items[i].name != "Sulfuras, Hand of Ragnaros") {
                                items[i].quality = items[i].quality - 1
                            }
                        }
                    } else {
                        items[i].quality = items[i].quality - items[i].quality
                    }
                } else {
                    if (items[i].quality < 50) {
                        items[i].quality = items[i].quality + 1
                    }
                }
            }
        }
    }

    private fun fallbackDefault(itemGroup: ItemGroup, inputRecord: ItemRecord): ItemRecord {
        "FallbackDefault was used since to condition provided for Group: ${itemGroup.displayName}".output(Colour.Yellow)
        inputRecord.update(inputRecord.sellIn - 1,  inputRecord.quality -1 )
        return inputRecord
    }

    fun updateQuality(){
        for (item in items){
            val condition = usedConditions.firstOrNull{ it.itemGroup == item.itemGroup }
            if(condition != null){
                condition.update(item)
            }else{
                fallbackDefault(item.itemGroup, item)
            }
        }
    }

}

