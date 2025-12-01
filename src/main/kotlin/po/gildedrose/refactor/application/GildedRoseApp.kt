package po.gildedrose.refactor.application



interface GildedRoseApp {
    fun clampQuality(value: Int) = value.coerceIn(0..50)
}

