package po.gildedrose.refactor.application

import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.reporting.ReportEngine
import po.misc.callbacks.signal.signalOf
import po.misc.functions.NoResult
import po.misc.types.token.TypeToken


class GildedRoseBuilder<T: ItemRecord>(
    val typeToken: TypeToken<T>

){
    var reportingEngine  : ReportEngine<T> ? = null


    internal fun gerReports(): ReportEngine<T>?{
        return reportingEngine
    }

    fun configReporting(buildrAction: (ReportEngine<T>.()-> Unit)):GildedRoseBuilder<T>{
        val engine = ReportEngine(typeToken)
        buildrAction.invoke(engine)
        reportingEngine = engine
        return this
    }

    @GildedDSL
    fun includeToReport(selector:(T) -> Boolean):GildedRoseBuilder<T>{
        val engine = ReportEngine(typeToken, selector)
        reportingEngine = engine
        return this
    }

    val onReportComplete = signalOf(typeToken, NoResult)


}