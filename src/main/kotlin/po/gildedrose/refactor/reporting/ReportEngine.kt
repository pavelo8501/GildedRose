package po.gildedrose.refactor.reporting

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.application.GildedDSL
import po.gildedrose.refactor.item.ItemRecord
import po.misc.data.PrettyPrint
import po.misc.data.styles.SpecialChars
import po.misc.types.token.TypeToken


class ReportEngine<T: ItemRecord>(
    val typeToken: TypeToken<T>,
    var selector: ((T)-> Boolean)? = null
) : PrettyPrint {
    constructor(
        typeToken: TypeToken<T>,
        groupFilter: ItemGroup,
        range: Pair<Long, Long>? = null
    ):this(typeToken) {
        byGroupFilter = groupFilter
        byRangeFilter = range
    }
    constructor(
        typeToken: TypeToken<T>,
        range: Pair<Long, Long>,
        groupFilter: ItemGroup? = null
    ):this(typeToken) {
        byGroupFilter = groupFilter
        byRangeFilter = range
    }

    private val reportRecordsBacking = mutableListOf<ReportRecord>()
    val reportRecords : List<ReportRecord> get() = reportRecordsBacking

    internal var byRangeFilter: Pair<Long, Long>? = null
    internal var byGroupFilter:ItemGroup? = null
    override val formattedString: String get() {
           val str =  reportRecords.joinToString(separator = SpecialChars.NEW_LINE) {
                it.formattedString
            }
            return str
        }

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

    private fun processByParameters(item: T, day: Int):ReportRecord?{
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
    private fun satisfiesParameters(item: T): Boolean{
        byGroupFilter?.let {
            if(item.itemGroup == it ){
                return true
            }
        }
        val filter = byRangeFilter
        if(filter == null){
            return false
        }else{
            return if (item.id >= filter.first && (item.id <= filter.second || filter.second == 0L)) {
                return true
            }else{
                false
            }
        }
    }
    private fun satisfiesSelector(item: T): Boolean{
        return selector?.invoke(item)?:false
    }

    @GildedDSL
    fun includeToReport(selector:(T) -> Boolean){
        this.selector = selector
    }

    internal fun reportItem(item: T, day: Int):ReportRecord?{
        val record =  ReportRecord(item, day)
        val bySelector = satisfiesSelector(item)
        val byParams = satisfiesParameters(item)
        if(bySelector || byParams){
            reportRecordsBacking.add(record)
            return record
        }
        return null
    }

    internal fun <R> reportItem(item: T, day: Int, block: ()-> R):R{
        val blockResult = block.invoke()
        val record =  reportItem(item, day)
        record?.provideResult(item.quality)
        return blockResult
    }
    fun clear(){
        reportRecordsBacking.clear()
    }
}