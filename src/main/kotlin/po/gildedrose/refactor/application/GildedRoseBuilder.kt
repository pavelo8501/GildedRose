package po.gildedrose.refactor.application

import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.reporting.ReportEngine
import po.misc.callbacks.signal.signalOf
import po.misc.functions.NoResult
import po.misc.types.token.TypeToken


/**
 * Builder used to configure optional features of the [po.gildedrose.GildedRose] engine.
 *
 * This builder is responsible for constructing and configuring a single
 * instance of [ReportEngine] associated with the item type [T].
 * It is typically used through the DSL-based constructor:
 *
 * ```
 * val app = GildedRose(items) {
 *     configReporting {
 *         // configure reporting engine
 *         enableDailySnapshots()
 *         includeSellIn()
 *     }
 *
 *     includeToReport { item ->
 *         item.quality < 10     // custom filter for reporting
 *     }
 * }
 * ```
 *
 * ### Reporting Engine
 * By default, a new [ReportEngine] is created using the [typeToken] of the item.
 * You can customize reporting in two ways:
 *
 *  - **`configReporting { ... }`** — mutates the existing reporting engine,
 *    allowing you to enable features or adjust formatting.
 *
 *  - **`includeToReport { ... }`** — sets a predicate that selects which items
 *    should be included in the report.
 *
 * ### Usage Notes
 * - The reporting engine is never replaced; instead, it is configured in-place.
 * - Calling both `configReporting` and `includeToReport` is allowed.
 * - The constructed [ReportEngine] is retrieved internally by [po.gildedrose.GildedRose]
 *   through [gerReports].
 *
 * @param typeToken A [TypeToken] describing the concrete item type used
 *                  in the [GildedRose] simulation.
 * @param T The specific subtype of [ItemRecord] handled by this instance.
 */
class GildedRoseBuilder<T: ItemRecord>(

    val typeToken: TypeToken<T>
){
    val reportingEngine  : ReportEngine<T> = ReportEngine(typeToken)

    var withStandardPrintout: Boolean = false

    internal fun gerReports(): ReportEngine<T>{
        return reportingEngine
    }
    fun configReporting(block: (ReportEngine<T>.()-> Unit)):GildedRoseBuilder<T>{
        reportingEngine.block()
        return this
    }
    @GildedDSL
    fun includeToReport(selector:(T) -> Boolean):GildedRoseBuilder<T>{
        reportingEngine.selector = selector
        return this
    }
}