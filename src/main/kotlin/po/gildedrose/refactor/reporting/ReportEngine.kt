package po.gildedrose.refactor.reporting

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.application.GildedDSL
import po.gildedrose.refactor.item.ItemRecord
import po.misc.types.token.TypeToken


class ReportEngine<T: ItemRecord>(
    val typeToken: TypeToken<T>,
    var selector: ((T)-> Boolean)? = null
) {
    constructor(typeToken: TypeToken<T>, groupFilter: ItemGroup, range: Pair<Long, Long>? = null):this(typeToken) {
        byGroupFilter = groupFilter
        byRangeFilter = range
    }
    constructor(typeToken: TypeToken<T>, range: Pair<Long, Long>, groupFilter: ItemGroup? = null):this(typeToken) {
        byGroupFilter = groupFilter
        byRangeFilter = range
    }

    private val reportRecordsBacking = mutableListOf<ReportRecord>()
    val reportRecords : List<ReportRecord> get() = reportRecordsBacking

    private var byRangeFilter: Pair<Long, Long>? = null
    private var byGroupFilter:ItemGroup? = null

    private fun processBySelector(item: T, day: Int): ReportRecord?{
        val shouldInclude = selector?.invoke(item)?:false
        if(shouldInclude){
             return ReportRecord(item, day).also {
                reportRecordsBacking.add(it)
            }
        }
        return null
    }
    private fun doesFallToGroup(itemGroup: ItemGroup, preciseMatch: Boolean): Boolean{
        val group = byGroupFilter
        if(group == null && !preciseMatch){
            return true
        }else{
            if(group == itemGroup){
                return true
            }
        }
        return false
    }

    private fun  processByParameters(item: T, day: Int):ReportRecord?{
        byGroupFilter?.let {
            if(item.itemGroup != it ){
                return null
            }
        }
        byRangeFilter?.let {filter->
            return if (item.id >= filter.first && (item.id <= filter.second || filter.second == 0L)) {
                ReportRecord(item, day).also {
                    reportRecordsBacking.add(it)
                }
            }else{
                null
            }
        }
        if(byGroupFilter != null){
            return ReportRecord(item, day).also {
                reportRecordsBacking.add(it)
            }
        }
        return null
    }

    @GildedDSL
    fun includeToReport(selector:(T) -> Boolean){
        this.selector = selector
    }
    fun processItem(item: T, day: Int, block: (ReportRecord?)-> Unit):ReportRecord? {
        if(processBySelector(item, day) == null){
           val record = processByParameters(item, day)
            block.invoke(record)
            return record
        }
        block.invoke(null)
        return null
    }
    fun processItem(item: T, day: Int):ReportRecord? {
        val reportRecord = processBySelector(item, day)
        if(reportRecord != null){
            return reportRecord
        }
        return processByParameters(item, day)
    }
    fun processItems(items: List<T>, day: Int): List<ReportRecord> =
        items.mapNotNull { processItem(it, day) }

    fun clear(){
        reportRecordsBacking.clear()
    }
}