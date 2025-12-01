package po.gildedrose

import org.junit.jupiter.api.Test
import po.gildedrose.models.FixtureData
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.toGRItems
import po.gildedrose.setup.GildedTestBase
import po.misc.collections.asList
import po.misc.data.output.output
import po.misc.io.WriteOptions
import po.misc.io.toSafePathName
import po.misc.io.writeToFile

class OriginalLogicTest : GildedTestBase() {

    private val fwOptions = WriteOptions(overwriteExistent = false, createSubfolders = true, throwIfFileExists = false)

    @Test
    fun `Creating snapshot of original list and update logic`(){
        val app = GildedRose(originalItemList)
        app.updateQualityLegacy()
        val resultingItems =  app.items.map { GRItem(it) }
        val jsonSnapshot = jsonParser.encodeToString(resultingItems)
        jsonSnapshot.writeToFile("/snapshot/original_logic.json", fwOptions)
    }

    @Test
    fun `Creating snapshot from TextFixture`(){
        val resultingList = mutableListOf<FixtureData>()
        main(arrayOf("30")){
            resultingList.add(it)
        }
        val jsonSnapshot = jsonParser.encodeToString(resultingList)
        jsonSnapshot.writeToFile("/snapshot/fixture.json", fwOptions)
    }

    @Test
    fun `Creating snapshot of original logic per category (product) `(){
        val items = originalItemList.toGRItems().distinctBy { it.itemGroup }.distinctBy { it.name }
        val initialSellIn = 28
        val initialQuality = 50
        for(item in items){
            val app = GildedRose(item.asList(), emptyList())
            val resultList = simulateFor(initialSellIn, initialQuality, item, app)
            println()
            resultList.output()
            val jsonSnapshot = jsonParser.encodeToString(resultList)
            val fileName = item.name.toSafePathName()
            jsonSnapshot.writeToFile("/snapshot/${fileName}.json", fwOptions)
        }
    }
}