package po.gildedrose

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.toGRItem
import po.gildedrose.refactor.item.toGRItems
import po.gildedrose.setup.GildedTestBase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class GRItemTest : GildedTestBase() {

    @Test
    fun `GRItems match original list`(){
        val grItems = originalItemList.toGRItems()
        assertEquals(originalItemList.size, grItems.size)
        originalItemList.forEachIndexed { index, item->
            assertEquals(item.name, grItems[index].name)
            assertEquals(item.quality, grItems[index].quality)
            assertEquals(item.sellIn, grItems[index].sellIn)
        }
    }


    @Test
    fun `GRItem parameters should match original class instance`(){
        val originalItem = originalItemList.first()
        var grItem =  originalItem.toGRItem()
        assertEquals(ItemGroup.Default, grItem.itemGroup)
        assertEquals(originalItem.name, grItem.name)
        assertEquals(originalItem.quality, grItem.quality)
        assertEquals(originalItem.sellIn, grItem.sellIn)

        val elixirItem = assertNotNull( originalItemList.firstOrNull{ it.name.contains("Elixir") } )
        grItem = elixirItem.toGRItem()
        assertEquals(ItemGroup.Elixir, grItem.itemGroup)
        assertEquals(elixirItem.name, grItem.name)
        assertEquals(elixirItem.quality, grItem.quality)
        assertEquals(elixirItem.sellIn, grItem.sellIn)

        val sulfurasItem = assertNotNull( originalItemList.firstOrNull{ it.name.contains("Sulfuras") } )
        grItem = sulfurasItem.toGRItem()
        assertEquals(ItemGroup.Sulfuras, grItem.itemGroup)
        assertEquals(sulfurasItem.name, grItem.name)
        assertEquals(sulfurasItem.quality, grItem.quality)
        assertEquals(sulfurasItem.sellIn, grItem.sellIn)

        val backStageItem = assertNotNull( originalItemList.firstOrNull{ it.name.contains("Backstage passes") } )
        grItem = backStageItem.toGRItem()
        assertEquals(ItemGroup.BackstagePasses, grItem.itemGroup)
        assertEquals(backStageItem.name, grItem.name)
        assertEquals(backStageItem.quality, grItem.quality)
        assertEquals(backStageItem.sellIn, grItem.sellIn)
    }

    @Test
    fun `Created GRItem  should match original parameters exactly`(){

        val name1 =  "+5 Dexterity Vest"
        val agedName = "Aged Brie"
        val sulfurasName = "Sulfuras, Hand of Ragnaros"
        val vestItem = createItem(name1)
        var grItem =  GRItem(name1, itemSellIn = vestItem.sellIn,  itemQuality = vestItem.quality, ItemGroup.Default)
        assertEquals(vestItem.name, grItem.name)
        assertEquals(ItemGroup.Default, grItem.itemGroup)

        val agedItem = createItem(agedName)
        grItem = GRItem(agedName, itemSellIn = vestItem.sellIn,  itemQuality = vestItem.quality, ItemGroup.AgedBrie)
        assertEquals(agedItem.name, grItem.name)

        val sulfurasItem = createItem(sulfurasName)
        grItem = GRItem(sulfurasName, itemSellIn = sulfurasItem.sellIn,  itemQuality = sulfurasItem.quality, ItemGroup.Sulfuras)
        assertEquals(sulfurasItem.name, grItem.name)
    }

    @Test
    fun `Serialization of GRItem`(){
        val firstItem = originalItemList.first()
        val jsonString = assertDoesNotThrow {
           val item = firstItem.toGRItem()
            jsonParser.encodeToString(item)
        }
        assertTrue {
            jsonString.contains(firstItem.name) &&
                    jsonString.contains(firstItem.quality.toString()) &&
                        jsonString.contains(firstItem.sellIn.toString())
        }
    }



}