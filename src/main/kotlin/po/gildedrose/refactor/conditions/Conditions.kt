package po.gildedrose.refactor.conditions

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.Quality
import po.gildedrose.refactor.item.clamp


/**
 * Update rule for regular items.
 *
 * Rules:
 * * Quality degrades by 1 each day.
 * * After the sell-in date passes, quality degrades twice as fast.
 * * Quality never drops below 0.
 */
val normalItemCondition = UpdateCondition(ItemGroup.Default){ record->
    val sellIn = record.sellIn -1
    if(record.sellIn >= 1){
        val quality = Quality(record.quality - 1).clamp()
        record.update(sellIn,   quality)
    }else{
        val quality = Quality( record.quality - 2).clamp()
        record.update(sellIn,   quality)
    }
}

/**
 * Update rule for "Sulfuras, Hand of Ragnaros".
 *
 * Rules:
 * * Legendary item — never decreases in quality.
 * * Always has a quality of 80.
 * * Sell-in does not change.
 */
val sulfrasItemCondition = UpdateCondition(ItemGroup.Sulfuras){ record->
    val sellIn =  record.sellIn
    val quality = Quality(80)
    record.update(sellIn, quality)
}

/**
 * Update rule for "Conjured" items.
 *
 * Rules:
 * * Quality degrades twice as fast as normal items.
 * * After sell-in passes, degradation doubles again.
 * * Quality never drops below 0.
 */
val conjuredItemCondition = UpdateCondition(ItemGroup.Conjured){ record->
    val sellIn = record.sellIn -1
    if(record.sellIn >= 1){
        val quality = Quality(record.quality - 2).clamp()
        record.update(sellIn, quality)
    }else{
        val quality = Quality(record.quality - 4).clamp()
        record.update(sellIn, quality)
    }
}

/**
 * Update rule for "Aged Brie".
 *
 * Rules:
 * * Quality increases by 1 each day.
 * * Quality never exceeds 50.
 */
val agedBrieCondition = UpdateCondition(ItemGroup.AgedBrie){ record->
    val sellIn = record.sellIn -1
    val quality = Quality(record.quality + 1).clamp()
    record.update(sellIn, quality)
}

/**
 * Update rule for "Backstage Passes".
 *
 * Rules:
 * * Quality increases as sell-in approaches.
 *   - +1 when sell-in > 10
 *   - +2 when sell-in is 6–10
 *   - +3 when sell-in is 1–5
 * * Quality drops to 0 after the concert (sell-in <= 0).
 * * Quality is clamped between 0 and 50.
 */
val backStageItemCondition = UpdateCondition(ItemGroup.BackstagePasses){ record->
    val sellIn = record.sellIn -1
    val quality =  when {
        record.sellIn <= 0 -> Quality(0)
        record.sellIn <= 3 -> Quality(record.quality + 3)
        record.sellIn > 10 -> Quality(record.quality + 1)
        else -> Quality(record.quality + 2 )
    }
    record.update(sellIn, quality)
}