package po.gildedrose.refactor.application

import po.gildedrose.GildedRose
import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.toGRItems
import po.gildedrose.setup.GildedTestBase
import po.misc.data.output.output
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class GildedRoseBuilderTest : GildedTestBase(){

    @Test
    fun `GildedRoseBuilder usage test`(){
        val app = GildedRose<GRItem>(originalItemList.toGRItems()){
            includeToReport{item->
                item.itemGroup == ItemGroup.BackstagePasses && item.id == 6L
            }
        }
        assertNotNull(app.reports.firstOrNull()){
             assertNotNull(it.selector)
        }
        repeat(2){
            app.updateQuality(it)
        }
        val report = app.collectReport()
        report.output()
        assertEquals(2, report.size)
    }

    @Test
    fun `GildedRoseBuilder shorthand usage test`(){
        val app2 = GildedRose<GRItem>(originalItemList.toGRItems()){
            configReporting {
                includeToReport{ item->
                    item.itemGroup == ItemGroup.BackstagePasses
                }
            }
        }
        assertNotNull(app2.reports.firstOrNull()){
            assertNotNull(it.selector)
        }
    }
}