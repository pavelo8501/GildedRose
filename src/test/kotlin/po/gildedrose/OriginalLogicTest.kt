package po.gildedrose

import org.junit.jupiter.api.Test
import po.gildedrose.refactor.GRItem
import po.gildedrose.setup.GildedTestBase
import po.misc.io.WriteOptions
import po.misc.io.writeToFile

class OriginalLogicTest : GildedTestBase() {

    @Test
    fun `Testing original logic`(){
        val app = GildedRose(originalItemList)
        app.updateQuality()
        val resultingItems =  app.items.map { GRItem(it) }
        val jsonSnapshot = jsonParser.encodeToString(resultingItems)
        val writeOptions =  WriteOptions(overwriteExistent = false, createSubfolders = true, throwIfFileExists = false)
        jsonSnapshot.writeToFile("/snapshot/original_logic.json", writeOptions)
    }
}