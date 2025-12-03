package po.gildedrose

import po.gildedrose.models.FixtureData
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.item.toGRItems
import po.misc.types.token.isSubclassOf


private fun  runMain(
    args: Array<String>,
    useItems: List<ItemRecord>?,
    useApp: GildedRose<GRItem>? = null,
    beforeCalculated: ((FixtureData)-> Unit)? = null
){

    println("OMGHAI!")


    val app = useApp?:run {
        if (useItems == null) {
            val items = listOf(
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
            GildedRose(items.toGRItems())
        } else {
            GildedRose(useItems)
        }
    }

    var days = 2
    if (args.size > 0) {
        days = Integer.parseInt(args[0]) + 1
    }
    for (i in 0..days - 1) {
        println("-------- day $i --------")
        println("name, sellIn, quality")
        for (item in app.items) {
            beforeCalculated?.let {
                val data = FixtureData(day = i, name = item.name, sellIn = item.sellIn, quality = item.quality)
                it.invoke(data)
            }
            println(item)
        }
        println()
        app.updateQuality(i)
    }
}

fun main(
    numberOfDays: Int,
    useItems: List<GRItem>,
    beforeCalculated: ((FixtureData)-> Unit)?
) = runMain(arrayOf(numberOfDays.toString()), useItems, beforeCalculated = beforeCalculated)


fun main(numberOfDays: Int, beforeCalculated: (FixtureData)-> Unit) =
    runMain(arrayOf(numberOfDays.toString()), useItems = null,  beforeCalculated = beforeCalculated)

fun main(numberOfDays: Int, app: GildedRose<GRItem>,  beforeCalculated: ((FixtureData)-> Unit)? = null) =
    runMain(arrayOf(numberOfDays.toString()), useItems = null, useApp = app, beforeCalculated = beforeCalculated)

fun main(args: Array<String>) = runMain(args, useItems = null)




