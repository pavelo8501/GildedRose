package po.gildedrose.setup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import po.gildedrose.GildedRose
import po.gildedrose.Item
import po.gildedrose.refactor.item.GRItem
import po.misc.data.strings.appendGroup


abstract class GildedTestBase {

    @Serializable
    data class TestResult(
        @SerialName("Condition")
        val conditionName: String,
        @Transient
        private val item: Item = Item("Default", -1, -1)
    ){
        @SerialName("Item name")
        val itemName: String = item.name
        @SerialName("Initial Sell-in")
        val initialSellIn: Int = item.sellIn
        @SerialName("Initial quality")
        val initialQuality: Int = item.quality

        @SerialName("Resulting Sell-in")
        var resultingSellIn: Int? = null
        @SerialName("Resulting Quality")
        var resultingQuality: Int? = null

        @SerialName("Difference")
        var difference: Int? = null

        fun registerResult(item: Item){
            val quality = item.quality
            resultingSellIn = item.sellIn
            resultingQuality = quality
            difference = initialQuality - quality
        }

        override fun toString(): String {
           return buildString {
                append(conditionName)
                appendGroup('[', ']', ::initialQuality, ::resultingQuality,::difference)
            }
        }
    }



    val jsonParser = Json{
        isLenient = true
        prettyPrint = true
        encodeDefaults = true

    }

    val originalItemList = listOf(
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

    fun simulateFor(sellIn: Int, quality: Int, item: GRItem, app: GildedRose): List<TestResult>{
        val resultList = mutableListOf<TestResult>()
        item.sellIn = sellIn
        item.quality = quality
        for (i in 30 downTo 1){
            if(i == 12 || i == 9 || i == 2 || i == 1){
                val changeText = "from $i to ${i-1}"
                val conditionText = "Changing ${item.name} initial Quality of $quality & SellIn of $sellIn  $changeText"
                val testResult = TestResult(conditionText, item)
                app.updateQuality()
                testResult.registerResult(item)
                resultList.add(testResult)
            }else{
                app.updateQuality()
            }
        }
        return resultList
    }

    fun createItem(name: String, sellIn: Int = 10, quality: Int = 20): Item{
       return Item(name, sellIn, quality)
    }


}