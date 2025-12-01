package po.gildedrose.refactor.application

import org.junit.jupiter.api.Test
import po.gildedrose.GildedRose
import po.gildedrose.main
import po.gildedrose.models.FixtureData
import po.gildedrose.refactor.item.toGRItems
import po.gildedrose.setup.GildedTestBase
import po.misc.functions.Throwing
import po.misc.io.readFile
import kotlin.test.assertEquals

class GildedRoseAppTest : GildedTestBase() {

    val fixturePath = "/snapshot/fixture.json"

    private fun loadSnapshot():List<FixtureData>{
       return readFile(fixturePath, Throwing) {
           onSuccess {
               jsonParser.decodeFromString<List<FixtureData>>(it.readText(Charsets.UTF_8))
           }
       }
    }

    @Test
    fun `Refactored application class produce same logic as legacy code did`(){

        val snapshot = loadSnapshot()
        val fixtureResult = mutableListOf<FixtureData>()
        main(arrayOf("30")){
            fixtureResult.add(it)
        }
        assertEquals(snapshot.size, fixtureResult.size)
        snapshot.forEachIndexed {index,  data->
            val resultData = fixtureResult[index]
            assertEquals(data.name, resultData.name, "Day # ${data.day} failed for name")
            assertEquals(data.sellIn, resultData.sellIn, "Day # ${data.day} failed for sellIn")
            assertEquals(data.quality, resultData.quality, "Day # ${data.day} failed for quality")
        }
    }



}