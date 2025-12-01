package po.gildedrose.refactor.application

import po.gildedrose.refactor.item.Quality


interface GildedRoseApp {
    fun clampQuality(value: Int) = value.coerceIn(0..50)
}

fun Quality.clamp():Quality{
   return Quality(value.coerceIn(0..50))
}