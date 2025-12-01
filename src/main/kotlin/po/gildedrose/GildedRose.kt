package po.gildedrose

import po.gildedrose.refactor.application.GildedRoseApp
import po.gildedrose.refactor.conditions.UpdateCondition
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.item.toGRItems


class GildedRose(val items: List<Item>): GildedRoseApp{


    private var grItems = mutableListOf<GRItem>()
    private var conditionsBacking : List<UpdateCondition>? = null

   constructor(itemRecords: List<ItemRecord>, conditions: List<UpdateCondition>):this(items = emptyList()){
       grItems.addAll(itemRecords.toGRItems())
       conditionsBacking = conditions
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



}

