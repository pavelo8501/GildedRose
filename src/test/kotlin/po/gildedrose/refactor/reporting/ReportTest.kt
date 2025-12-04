package po.gildedrose.refactor.reporting


import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.toGRItems
import po.gildedrose.setup.GildedTestBase
import po.misc.types.token.TypeToken
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ReportTest : GildedTestBase(){

    @Test
    fun `Report parametrized conditions work as expected`(){
        val items =  originalItemList.toGRItems()
        val reportByGroup  = ReportEngine(TypeToken.create<GRItem>(), ItemGroup.BackstagePasses)
        items.forEach {
            reportByGroup.reportItem(it, 0)
        }
        assertEquals(3,  reportByGroup.reportRecords.size)

        val itemsStartingFromId = items.filter { it.id >= 8L }
        val reportByRange = ReportEngine(TypeToken.create<GRItem>(), Pair(8, 0))
        items.forEach {
            reportByRange.reportItem(it, 0)
        }
        assertEquals(itemsStartingFromId.size, reportByRange.reportRecords.size)
    }

    @Test
    fun `All report conditions work as expected`(){
        val items =  originalItemList.toGRItems()
        val reportEngine  = ReportEngine(TypeToken.create<GRItem>()){item->
            item.itemGroup == ItemGroup.BackstagePasses
        }
        items.forEach {
            reportEngine.reportItem(it, 0)
        }
        val report = reportEngine.reportRecords
        assertEquals(3, report.size)
        reportEngine.clear()
    }

    @Test
    fun `Report parametrized conditions work as expected 2`(){
        val items =  originalItemList.toGRItems()
        val projectedResult = 10
        val reportByGroup  = ReportEngine(TypeToken.create<GRItem>(), ItemGroup.BackstagePasses)
        items.forEach {item->
           reportByGroup.reportItem(item, day = 1){
            item.quality = projectedResult
           }
        }
        val reportRecords = reportByGroup.reportRecords
        assertEquals(3, reportRecords.size)
        assertNotNull(reportRecords.firstOrNull()){ firstRecord->
            assertEquals(1, firstRecord.day)
            assertEquals(projectedResult, firstRecord.qualityTo)
        }
    }

}