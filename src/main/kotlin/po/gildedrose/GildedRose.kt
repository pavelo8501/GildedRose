package po.gildedrose

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.application.GildedRoseApp
import po.gildedrose.refactor.application.GildedRoseBuilder
import po.gildedrose.refactor.conditions.UpdateCondition
import po.gildedrose.refactor.conditions.agedBrieCondition
import po.gildedrose.refactor.conditions.backStageItemCondition
import po.gildedrose.refactor.conditions.conjuredItemCondition
import po.gildedrose.refactor.conditions.normalItemCondition
import po.gildedrose.refactor.conditions.sulfrasItemCondition
import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.item.differentiateItems
import po.gildedrose.refactor.reporting.ReportEngine
import po.gildedrose.refactor.reporting.ReportRecord
import po.misc.collections.asList
import po.misc.data.output.output
import po.misc.data.styles.Colour
import po.misc.types.token.TypeToken


class GildedRose<T:ItemRecord>(
    val typeToken: TypeToken<T>,
    val items: List<T>, conditions: List<UpdateCondition> = emptyList(),
): GildedRoseApp{

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

    var reports = listOf<ReportEngine<T>>()
        private set

    @PublishedApi
    internal fun resolveConfig(builder :GildedRoseBuilder<T>){
        builder.gerReports()?.let {
            reports = it.asList()
        }
    }

    internal fun differentiateItems(itemRecords: List<T>):List<T>{
        return itemRecords.differentiateItems()
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
    internal fun fallbackDefault(itemGroup: ItemGroup, inputRecord: ItemRecord): ItemRecord {
        "FallbackDefault was used since to condition provided for Group: ${itemGroup.displayName}".output(Colour.Yellow)
        inputRecord.update(inputRecord.sellIn - 1,  inputRecord.quality -1 )
        return inputRecord
    }

    internal fun updateQualityByConditions(day: Int?){
        val processedItems = differentiateItems(items)
        for (item in processedItems){
            val condition = usedConditions.firstOrNull{ it.itemGroup == item.itemGroup }
            if(condition != null){
                if(day != null){
                    reports.forEach {engine->
                        engine.processItem(item, day){record->
                            condition.update(item)
                            record?.provideResult(item.quality)
                        }
                       // engine.processItem(item, day)
                    }
                }else{
                    condition.update(item)
                }
            }else{
                fallbackDefault(item.itemGroup, item)
            }
        }
    }

    fun updateQuality(day: Int? = null){
        when(typeToken.kClass){
            is Item -> updateQualityLegacy()
            else -> updateQualityByConditions(day)
        }
    }

    fun collectReport(): List<ReportRecord>{
       return reports.flatMap { it.reportRecords }
    }

    companion object {
        inline operator fun <reified T:ItemRecord> invoke(
            items: List<T>,
            conditions: List<UpdateCondition>? = null
        ):GildedRose<T>{
          return  conditions?.let {
                GildedRose(TypeToken.create<T>(),  items, it)
            }?:run {
                GildedRose(TypeToken.create<T>(), items)
            }
        }

        inline operator fun <reified T:ItemRecord> invoke(
            items: List<T>,
            noinline builderAction: GildedRoseBuilder<T>.()-> Unit,
        ):GildedRose<T>{
           val app = GildedRose(TypeToken.create<T>(), items, emptyList())
            val builder =  GildedRoseBuilder(TypeToken.create<T>())
            builderAction.invoke(builder)
            app.resolveConfig(builder)
           return app
        }

    }
}

