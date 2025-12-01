package po.gildedrose.refactor.item

import org.junit.jupiter.api.Test
import po.gildedrose.setup.GildedTestBase
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ItemRecordExtensionsTest : GildedTestBase() {

    @Test
    fun `ItemRecords  differentiate logic work as expected`(){

        val items =  originalItemList.map { it.toGRItem() }.differentiateItems()
        val groups =  items.groupBy { it.id }.values
        assertTrue { items.isNotEmpty() }
        groups.forEach {group->
            assertEquals(1, group.size)
        }
    }
}