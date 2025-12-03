package po.gildedrose

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.application.GildedRoseApp
import po.gildedrose.refactor.application.GildedRoseBuilder
import po.gildedrose.refactor.conditions.UpdateCondition
import po.gildedrose.refactor.conditions.agedBrieCondition
import po.gildedrose.refactor.conditions.backStageItemCondition
import po.gildedrose.refactor.conditions.conjuredItemCondition
import po.gildedrose.refactor.conditions.normalItemCondition
import po.gildedrose.refactor.conditions.sulfrasItemCondition
import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.item.differentiateItems
import po.gildedrose.refactor.reporting.ReportEngine
import po.gildedrose.refactor.reporting.ReportRecord
import po.misc.callbacks.signal.Signal
import po.misc.callbacks.signal.signalOf
import po.misc.data.output.output
import po.misc.data.styles.Colour
import po.misc.functions.NoResult
import po.misc.types.token.TypeToken


/**
 * Implementation of the Gilded Rose inventory update system with a pluggable,
 * type-safe and fully extensible update pipeline.
 *
 * This class is a generic engine that operates on a list of items of type [T],
 * where [T] must implement [ItemRecord]. It supports:
 *
 * * **Rule-based item updates** using a list of [UpdateCondition]
 * * **Dynamic overriding of conditions** on per-item-group basis
 * * **Observability** via [beforeUpdate] and [itemUpdated] signals
 * * **Optional reporting** via [ReportEngine]
 * * **Legacy update algorithm** identical to the original Gilded Rose kata
 *
 * @param T The concrete type of the inventory items processed by this engine.
 * @param typeToken Describes the item type at runtime for signal/report support.
 * @param items Initial list of items.
 * @param conditions Optional list of custom update conditions, overriding defaults.
 *
 * @constructor Creates a new GildedRose engine and prepares the internal item map.
 */
class GildedRose<T>(
    val typeToken: TypeToken<T>,
    var items: List<T>,
    conditions: List<UpdateCondition> = emptyList(),
): GildedRoseApp where T: ItemRecord  {

    private val usedConditions: MutableList<UpdateCondition> = mutableListOf(
        normalItemCondition,
        sulfrasItemCondition,
        agedBrieCondition,
        conjuredItemCondition,
        backStageItemCondition
    )

    private  val processedItems = differentiateItems(items)

    /**
     * Optional reporting engine that tracks per-item updates across days.
     * Configured via the [GildedRoseBuilder] DSL.
     */
    var report : ReportEngine<T>? = null
        private set

    /**
     * Signal emitted **before** an item is updated.
     *
     * Allows external listeners to react to pre-update state without altering it.
     */
    val beforeUpdate : Signal<T, Unit> = signalOf(typeToken, NoResult)

    /**
     * Signal emitted **after** an item is updated.
     *
     * Useful for logging, metrics, or debugging incremental changes.
     */
    val itemUpdated : Signal<T, Unit> = signalOf(typeToken, NoResult)

    var useLegacyCode: Boolean = false
        internal set

    var withStandardPrintout: Boolean = true

    /**
     * Initializes the custom-condition overrides when supplied.
     * A condition replaces another only if both have the same [ItemGroup].
     * The replacement is logged using terminal output for user visibility.
     */
    init {
        if(conditions.isNotEmpty()){
            updateConditions(conditions)
        }
    }

    private fun updateConditions(newUpdateCondition: List<UpdateCondition>) {
        if (newUpdateCondition.isNotEmpty()) {
            newUpdateCondition.forEach { suppliedCondition ->
                usedConditions.indexOfFirst { it.itemGroup == suppliedCondition.itemGroup }.let {
                    if (it > -1) {
                        usedConditions[it] = suppliedCondition
                        "Condition type: ${suppliedCondition.itemGroup.displayName} was overwritten".output(Colour.Yellow)
                    }
                }
            }
        }
    }

    /**
     * Applies the configuration produced by a [GildedRoseBuilder].
     *
     * Used internally by the DSL-based constructor.
     */
    @PublishedApi
    internal fun resolveConfig(builder :GildedRoseBuilder<T>){
        builder.gerReports()?.let {
            report = it
        }
        withStandardPrintout = builder.withStandardPrintout
    }

    internal fun differentiateItems(itemRecords: List<T>):List<T>{
        return itemRecords.differentiateItems()
    }
    internal fun fallbackDefault(itemGroup: ItemGroup, inputRecord: ItemRecord): ItemRecord {
        "FallbackDefault was used since to condition provided for Group: ${itemGroup.displayName}".output(Colour.Yellow)
        inputRecord.update(inputRecord.sellIn - 1,  inputRecord.quality -1 )
        return inputRecord
    }
    internal fun updateQualityByConditions(day: Int?){
        for (item in processedItems){
            val condition = usedConditions.firstOrNull{ it.itemGroup == item.itemGroup }
            beforeUpdate.trigger(item)
            val reportToUse = report
            if(condition != null){
                if(reportToUse != null && day != null){
                    reportToUse.reportItem(item, day){
                        condition.update(item)
                    }
                }else{
                    condition.update(item)
                }
            }else{
                fallbackDefault(item.itemGroup, item)
            }
            itemUpdated.trigger(item)
        }
    }

    /**
     * Exact implementation of the original Gilded Rose specification.
     *
     * This method is intentionally kept verbose and unoptimized in order to
     * preserve the reference behavior from the kata. Used only when
     * [useLegacyCode] is set to `true`.
     */
    fun updateQualityLegacy(){

        for (i in processedItems.indices) {
            beforeUpdate.trigger(items[i])
            if (items[i].name != "Aged Brie" && items[i].name != "Backstage passes to a TAFKAL80ETC concert") {
                if (items[i].quality > 0) {
                    if (items[i].name != "Sulfuras, Hand of Ragnaros") {
                        items[i].quality = items[i].quality - 1
                        itemUpdated.trigger(items[i])
                    }
                }
            } else {
                if (items[i].quality < 50) {
                    items[i].quality = items[i].quality + 1
                    itemUpdated.trigger(items[i])
                    if (items[i].name == "Backstage passes to a TAFKAL80ETC concert") {
                        if (items[i].sellIn < 11) {
                            if (items[i].quality < 50) {
                                items[i].quality = items[i].quality + 1
                                itemUpdated.trigger(items[i])
                            }
                        }

                        if (items[i].sellIn < 6) {
                            if (items[i].quality < 50) {
                                items[i].quality = items[i].quality + 1
                                itemUpdated.trigger(items[i])
                            }
                        }
                    }
                }
            }

            if (items[i].name != "Sulfuras, Hand of Ragnaros") {
                items[i].sellIn = items[i].sellIn - 1
                itemUpdated.trigger(items[i])
            }

            if (items[i].sellIn < 0) {
                if (items[i].name != "Aged Brie") {
                    if (items[i].name != "Backstage passes to a TAFKAL80ETC concert") {
                        if (items[i].quality > 0) {
                            if (items[i].name != "Sulfuras, Hand of Ragnaros") {
                                items[i].quality = items[i].quality - 1
                                itemUpdated.trigger(items[i])
                            }
                        }
                    } else {
                        items[i].quality = items[i].quality - items[i].quality
                        itemUpdated.trigger(items[i])
                    }
                } else {
                    if (items[i].quality < 50) {
                        items[i].quality = items[i].quality + 1
                        itemUpdated.trigger(items[i])
                    }
                }
            }
        }
    }

    /**
     * Updates the quality of all items using either:
     *
     * * the extensible rule-based system (default), or
     * * the original legacy implementation (when [useLegacyCode] = true)
     *
     * @param day Optional day index used for reporting.
     */
    fun updateQuality(day: Int? = null){
        if(useLegacyCode){
            updateQualityLegacy()
        }else{
            updateQualityByConditions(day)
        }
    }

    /**
     * Collects all records produced by the configured [ReportEngine].
     *
     * @return A list of [ReportRecord], or an empty list if no reporting is enabled.
     */
    fun collectReport(): List<ReportRecord>{
       return report?.reportRecords?:emptyList()
    }

    companion object {

        val defaultItems = listOf(
            Item("+5 Dexterity Vest", 10, 20), //
            Item("Aged Brie", 2, 0), //
            Item("Elixir of the Mongoose", 5, 7), //
            Item("Sulfuras, Hand of Ragnaros", 0, 80), //
            Item("Sulfuras, Hand of Ragnaros", -1, 80),
            Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
            Item("Backstage passes to a TAFKAL80ETC concert", 10, 49),
            Item("Backstage passes to a TAFKAL80ETC concert", 5, 49),
            // this conjured item does not work properly yet
            Item("Conjured Mana Cake", 3, 6)
        )

        /**
         * Creates a Gilded Rose engine using optional custom update conditions.
         *
         * @param items Item list to process.
         * @param conditions Optional overrides for update rules.
         */
        inline operator fun <reified T:ItemRecord> invoke(
            items: List<T>,
            vararg conditions: UpdateCondition
        ):GildedRose<T>{
            val conditionList = conditions.toList()
            return if(conditionList.isNotEmpty()){
                GildedRose(TypeToken.create<T>(),  items, conditionList)
            }else{
                GildedRose(TypeToken.create<T>(), items)
            }
        }

        /**
         * Creates a Gilded Rose engine using the configuration DSL.
         *
         * @param items Item list to process.
         * @param builderAction DSL block configuring reporting or flags.
         */
        inline operator fun <reified T:ItemRecord> invoke(
            items: List<T>,
            noinline builderAction: GildedRoseBuilder<T>.()-> Unit,
        ):GildedRose<T>{
            val app = GildedRose(TypeToken.create<T>(), items, emptyList())
            val builder =  GildedRoseBuilder(TypeToken.create<T>())
            builderAction.invoke(builder)
            app.resolveConfig(builder)
           return app
        }
    }
}

