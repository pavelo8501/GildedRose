package po.gildedrose.refactor.reporting

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.ItemRecord
import po.misc.types.token.TypeToken


class ReportEngine<T: ItemRecord>(
    val typeToken: TypeToken<T>,
    val selector: ((T)-> Boolean)? = null
) {
    constructor(typeToken: TypeToken<T>, groupFilter: ItemGroup, range: Pair<Long, Long>? = null):this(typeToken) {
        byGroupFilter = groupFilter
        byRangeFilter = range
    }
    constructor(typeToken: TypeToken<T>, range: Pair<Long, Long>, groupFilter: ItemGroup? = null):this(typeToken) {
        byGroupFilter = groupFilter
        byRangeFilter = range
    }

    private val reportBacking = mutableListOf<T>()
    val report : List<T> get() =  reportBacking
    private val reportRecordsBacking = mutableListOf<ReportRecord>()
    val reportRecords : List<ReportRecord> get() = reportRecordsBacking

    private var byRangeFilter: Pair<Long, Long>? = null
    private var byGroupFilter:ItemGroup? = null

    private fun includeBySelector(item: T): Boolean{
        val shouldInclude = selector?.invoke(item)?:false
        if(shouldInclude){
            reportBacking.add(item)
            return true
        }
        return false
    }

    private fun includeBySelector(item: T, day: Int): ReportRecord?{
        val shouldInclude = selector?.invoke(item)?:false
        if(shouldInclude){
            reportBacking.add(item)
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

    private fun includeByParameters(item: T){
        byGroupFilter?.let {
            if(item.itemGroup != it ){
                return
            }
        }
        byRangeFilter?.let {filter->
            if (item.id >= filter.first && (item.id <= filter.second || filter.second == 0L)) {
                reportBacking.add(item)
                return
            }else{
                return
            }
        }
        if(byGroupFilter!= null){
            reportBacking.add(item)
        }
    }

    private fun includeByParameters(item: T, day: Int):ReportRecord?{
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

    fun includeToReport(item: T){
        if(!includeBySelector(item)){
            includeByParameters(item)
        }
    }

    fun includeToReport(item: T, day: Int, block: (ReportRecord?)-> Unit):T {
        if(includeBySelector(item, day) == null){
           val record = includeByParameters(item, day)
            block.invoke(record)
            return item
        }
        block.invoke(null)
        return item
    }

    fun includeToReport(items: List<T>):List<T>{
        items.forEach {
            includeToReport(it)
        }
        return report
    }

    fun clear(){
        reportBacking.clear()
    }

}