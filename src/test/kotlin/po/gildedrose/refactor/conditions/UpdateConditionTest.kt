package po.gildedrose.refactor.conditions

import org.junit.jupiter.api.Test
import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.toGRItems
import po.gildedrose.setup.GildedTestBase
import kotlin.test.assertEquals

class UpdateConditionTest: GildedTestBase() {

    private fun makeUpdate(sellIn: Int, quality: Int, item:  GRItem, condition:  UpdateCondition){
        item.sellIn = sellIn
        item.quality = quality
        condition.update(item)
    }

    private val normalPathText : (Int)-> String = { "At normal path Quality should change value to $it" }
    private val dueSellInText : (Int)-> String = { "When Sell-in is 0 Quality should change value to $it" }

    private val negativeQualityText :  String =  "When negative quality supplied it should default to 0"
    private val qualityShouldNeverExceedText = "Quality should never exceed 50"

    @Test
    fun `Condition test for normal item`(){

        val item = originalItemList.toGRItems().first { it.itemGroup == ItemGroup.Default }

        makeUpdate(10, 10, item, normalItemCondition)
        assertEquals(9, item.sellIn)
        assertEquals(9, item.quality, normalPathText(9))

        makeUpdate(0, 10, item, normalItemCondition)
        assertEquals(8, item.quality, dueSellInText(5))

        makeUpdate(-10, -10, item, normalItemCondition)
        assertEquals(0, item.quality, negativeQualityText)
    }

    @Test
    fun `Conjured condition`(){
        val item = originalItemList.toGRItems().first { it.itemGroup == ItemGroup.Conjured }
        makeUpdate(10, 10, item, conjuredItemCondition)
        assertEquals(9, item.sellIn)
        assertEquals(8, item.quality, normalPathText(8))

        makeUpdate(0, 10, item, conjuredItemCondition)
        assertEquals(6, item.quality, dueSellInText(6))
    }

    @Test
    fun `Aged Brie condition`(){

        val item = originalItemList.toGRItems().first { it.itemGroup == ItemGroup.AgedBrie }
        makeUpdate(10, 10, item, agedBrieCondition)
        assertEquals(9, item.sellIn)
        assertEquals(11, item.quality, normalPathText(11))

        makeUpdate(10, 50, item, agedBrieCondition)
        assertEquals(9, item.sellIn)
        assertEquals(50, item.quality, qualityShouldNeverExceedText)
    }

    @Test
    fun `Sulfras  condition`(){
        val item = originalItemList.toGRItems().first { it.itemGroup == ItemGroup.Sulfuras }
        makeUpdate(10, 10, item, sulfrasItemCondition)
        assertEquals(10, item.sellIn)
        assertEquals(80, item.quality, normalPathText(80))

        makeUpdate(0, 10, item, sulfrasItemCondition)
        assertEquals(80, item.quality, dueSellInText(10))
    }


    @Test
    fun `BackStage condition`(){
        val item = originalItemList.toGRItems().first { it.itemGroup == ItemGroup.BackstagePasses }
        makeUpdate(10, 10, item, backStageItemCondition)
        assertEquals(9, item.sellIn)
        assertEquals(12, item.quality, normalPathText(12))

        makeUpdate(3, 10, item, backStageItemCondition)
        assertEquals(13, item.quality, "At sell in 3 Quality should change value to 13" )

        makeUpdate(0, 10, item, backStageItemCondition)
        assertEquals(0, item.quality, dueSellInText(0))
    }
}