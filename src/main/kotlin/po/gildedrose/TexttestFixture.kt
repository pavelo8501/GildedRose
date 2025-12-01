package po.gildedrose

import po.gildedrose.models.FixtureData
import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.conditions.UpdateCondition
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.ItemRecord
import java.util.concurrent.locks.Condition


private fun runMain(
    args: Array<String>,
    useItems: List<ItemRecord>?,
    onCalculated: ((FixtureData)-> Unit)?){

    val printout = onCalculated == null

    if(printout){
        println("OMGHAI!")
    }

    val items= listOf(
        Item("+5 Dexterity Vest", 10, 20), //
        Item("Aged Brie", 2, 0), //
        Item("Elixir of the Mongoose", 5, 7), //
        Item("Sulfuras, Hand of Ragnaros", 0, 80), //
        Item("Sulfuras, Hand of Ragnaros", -1, 80),
        Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
        Item("Backstage passes to a TAFKAL80ETC concert", 10, 49),
        Item("Backstage passes to a TAFKAL80ETC concert", 5, 49),
        // this conjured item does not work properly yet
        Item("Conjured Mana Cake", 3, 6)
    )

    val app = if (useItems == null){
        GildedRose(items)
    }else{
        GildedRose(useItems, listOf(UpdateCondition(ItemGroup.Default,  {  } )))
    }

    var days = 2
    if (args.size > 0) {
        days = Integer.parseInt(args[0]) + 1
    }
    for (i in 0..days - 1) {
        if(printout){
            println("-------- day $i --------")
            println("name, sellIn, quality")
        }
        for (item in items) {
            onCalculated?.let {
                val data = FixtureData(day = i, name = item.name, sellIn = item.sellIn, quality = item.quality)
                it.invoke(data)
            }
            if(printout){
                println(item)
            }
        }
        if(printout) {
            println()
        }
        app.updateQualityLegacy()
    }
}

fun main(
    args: Array<String>,
    useItems: List<GRItem>,
    onCalculated: ((FixtureData)-> Unit)?
) = runMain(args, useItems, onCalculated)


fun main(args: Array<String>, onCalculated: (FixtureData)-> Unit) =
    runMain(args, useItems = null,  onCalculated = onCalculated)

fun main(args: Array<String>) = runMain(args, useItems = null,  onCalculated = null)




