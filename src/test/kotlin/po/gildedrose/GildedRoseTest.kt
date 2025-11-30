package po.gildedrose

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GildedRoseTest {

    @Test
    fun foo() {
        val productName = "foo"
        val items = listOf(Item(productName, 0, 0))
        val app = GildedRose(items)
        app.updateQuality()
        assertEquals(productName, app.items[0].name)
    }

}


