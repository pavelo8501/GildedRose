package po.gildedrose

import org.junit.jupiter.api.Test
import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.toGRItem
import po.gildedrose.setup.GildedTestBase
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class GRItemTest : GildedTestBase() {

    @Test
    fun `GRItem parameters should match original class instance`(){
        val originalItem = originalItemList.first()
        var grItem =  originalItem.toGRItem()
        assertEquals(ItemGroup.Default, grItem.itemGroup)
        assertEquals(originalItem.name, grItem.name)

        val elixirItem = assertNotNull( originalItemList.firstOrNull{ it.name.contains("Elixir") } )
        grItem = elixirItem.toGRItem()
        assertEquals(ItemGroup.Elixir, grItem.itemGroup)
        assertEquals(elixirItem.name, grItem.name)

    }
}