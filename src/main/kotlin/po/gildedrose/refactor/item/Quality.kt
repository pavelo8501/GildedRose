package po.gildedrose.refactor.item

@JvmInline
value class Quality(val value: Int)

fun Quality.clamp():Quality{
    return Quality(value.coerceIn(0..50))
}