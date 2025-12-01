package po.gildedrose.refactor.item


/**
 * Assigns unique IDs to items that do not yet have one.
 *
 * In the original Gilded Rose kata, many items share identical names
 * (e.g., multiple `"Backstage passes to a TAFKAL80ETC concert"` entries),
 * which makes it difficult to distinguish them during processing or debugging.
 *
 * This helper scans the list and assigns incremental IDs **only** to the items
 * whose current `id` is `0L`, ensuring that:
 *
 * - existing IDs are preserved and never overwritten;
 * - only unidentified items receive new IDs;
 * - the list becomes safely distinguishable without affecting item behavior.
 *
 * @receiver List of `ItemRecord` objects to process.
 * @return The same list instance, with missing IDs filled in.
 */
fun <T:ItemRecord> List<T>.differentiateItems():List<T>{
    filter { it.id == 0L }.forEachIndexed { index, record ->
        val id = index + 1L
        record.setItemId(id)
    }
    return this
}
