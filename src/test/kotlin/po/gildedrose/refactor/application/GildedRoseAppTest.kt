package po.gildedrose.refactor.application

import org.junit.jupiter.api.Test
import po.gildedrose.GildedRose
import po.gildedrose.models.FixtureData
import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.toGRItems
import po.gildedrose.setup.GildedTestBase
import po.misc.collections.asList
import po.misc.functions.Throwing
import po.misc.io.readFile
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GildedRoseAppTest : GildedTestBase() {

    private val initialSellIn = 28
    private val initialQuality = 50

    private fun loadSnapshot(snapshot: Snapshot):List<TestResult>{
        return readFile(snapshot.path, Throwing){
            onSuccess {
                jsonParser.decodeFromString<List<TestResult>>(it.bytes.toString(Charsets.UTF_8))
            }
        }
    }

    private fun loadFixtureSnapshot():List<FixtureData>{
        return readFile("snapshot/fixture.json", Throwing){
            onSuccess {
                jsonParser.decodeFromString<List<FixtureData>>(it.bytes.toString(Charsets.UTF_8))
            }
        }
    }

    @Test
    fun `Refactored code did  did not changed original logic`(){
        val snapshot = loadFixtureSnapshot()
        val items = originalItemList.toGRItems()
        val app = GildedRose(items)
        var tickNo = 0
        val failingRecord : (FixtureData) -> String = {
            "For record $it"
        }
        app.beforeUpdate.onSignal {item->
            val fixtureRecord = assertNotNull(snapshot.getOrNull(tickNo))
            assertEquals(item.quality, fixtureRecord.quality, failingRecord(fixtureRecord))
            assertEquals(item.name, fixtureRecord.name, failingRecord(fixtureRecord))
            assertEquals(item.sellIn, fixtureRecord.sellIn, failingRecord(fixtureRecord))
            tickNo += 1
        }
        app.updateQualityLegacy()
    }

    @Test
    fun `Refactored app test for normal item type`(){
        val defaultProductSnapshot = loadSnapshot(Snapshot.NormalItem)
        val normalItem = originalItemList.toGRItems().firstOrNull { it.itemGroup == ItemGroup.Default }
        assertNotNull(normalItem)
        val app = GildedRose(normalItem.asList())
        val resultList = simulateFor(initialSellIn, initialQuality, normalItem, app)
        assertEquals(defaultProductSnapshot.size, resultList.size)

        defaultProductSnapshot.forEachIndexed {index, savedResult->
            val fromResult = resultList[index]
            assertEquals(savedResult.initialQuality, fromResult.initialQuality)
            assertEquals(savedResult.resultingQuality, fromResult.resultingQuality)
        }
    }

    @Test
    fun `Conjured items degrade twice as fast`() {
        val item = GRItem("Conjured Mana Cake", 3, 6, ItemGroup.Conjured)
        val app = GildedRose(listOf(item))
        val expected = listOf(6, 4, 2, 0, 0, 0)
        expected.forEachIndexed { day, expectedQuality ->
            assertEquals(expectedQuality, item.quality, "Day $day")
            app.updateQuality()
        }
    }

    @Test
    fun `Refactored app test for aged item type`(){
        val agedProductSnapshot = loadSnapshot(Snapshot.AgedItem)
        val normalItem = originalItemList.toGRItems().firstOrNull { it.itemGroup == ItemGroup.AgedBrie }
        assertNotNull(normalItem)
        val app = GildedRose(normalItem.asList())
        val resultList = simulateFor(initialSellIn, initialQuality, normalItem, app)
        assertEquals(agedProductSnapshot.size, resultList.size)
        agedProductSnapshot.forEachIndexed {index, savedResult->
            val fromResult = resultList[index]
            assertEquals(savedResult.initialQuality, fromResult.initialQuality)
            assertEquals(savedResult.resultingQuality, fromResult.resultingQuality)
        }
    }

    @Test
    fun `Refactored app test for sulfras item type`(){
        val sulfrasSnapshot = loadSnapshot(Snapshot.SulfrasItem)
        val sulfrasItem = originalItemList.toGRItems().firstOrNull { it.itemGroup == ItemGroup.Sulfuras }

        assertNotNull(sulfrasItem)
        val app = GildedRose(sulfrasItem.asList())
        val resultList = simulateFor(initialSellIn, initialQuality, sulfrasItem, app)
        assertEquals(sulfrasSnapshot.size, resultList.size)
        sulfrasSnapshot.forEachIndexed {index, savedResult->
            val fromResult = resultList[index]
            assertEquals(savedResult.initialQuality, fromResult.initialQuality)
            assertEquals(savedResult.resultingQuality, fromResult.resultingQuality)
        }
    }

    @Test
    fun `Refactored app test for backstage item type`(){
        val backStageSnapshot = loadSnapshot(Snapshot.BackstageItem)
        val backstageItem = originalItemList.toGRItems().firstOrNull { it.itemGroup == ItemGroup.BackstagePasses }
        assertNotNull(backstageItem)
        val app = GildedRose(backstageItem.asList())
        val resultList = simulateFor(initialSellIn, initialQuality, backstageItem, app)
        assertEquals(backStageSnapshot.size, resultList.size)
        backStageSnapshot.forEachIndexed {index, savedResult->
            val fromResult = resultList[index]
            assertEquals(savedResult.initialQuality, fromResult.initialQuality, "${savedResult.conditionName} initialQuality")
            assertEquals(savedResult.resultingQuality, fromResult.resultingQuality, "${savedResult.conditionName} resultingQuality")
        }
    }
}