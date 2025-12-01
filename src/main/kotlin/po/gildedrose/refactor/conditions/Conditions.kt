package po.gildedrose.refactor.conditions

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.application.clamp
import po.gildedrose.refactor.item.Quality


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

val sulfrasItemCondition = UpdateCondition(ItemGroup.Sulfuras){ record->
    val sellIn =  record.sellIn
    val quality = Quality(80)
    record.update(sellIn, quality)
}

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

val agedBrieCondition = UpdateCondition(ItemGroup.AgedBrie){ record->
    val sellIn = record.sellIn -1
    val quality = Quality(record.quality  + 1 ).clamp()
    record.update(sellIn, quality)
}


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