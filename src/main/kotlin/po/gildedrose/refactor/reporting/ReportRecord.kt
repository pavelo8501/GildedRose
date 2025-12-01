package po.gildedrose.refactor.reporting

import po.gildedrose.refactor.item.ItemRecord
import po.misc.data.PrettyPrint
import po.misc.data.pretty_print.grid.buildPrettyGrid
import po.misc.data.strings.appendGroup

class ReportRecord(
    private val item: ItemRecord,
    val day: Int
) : PrettyPrint {
    val id: Long = item.id
    val itemName: String = item.name
    val qualityFrom: Int = item.quality
    var qualityTo: Int = -1

    override val formattedString: String get() = reportTemplate.render(this)

    fun provideResult(quality: Int):ReportRecord{
        qualityTo = quality
        return this
    }
    override fun toString(): String {
       return buildString {
            append("ReportRecord")
            appendGroup('[', ']', ::day, ::itemName, ::qualityFrom, ::qualityTo)
        }
    }
    companion object{
        val reportTemplate = buildPrettyGrid<ReportRecord> {
            buildRow{
                addCells(
                    ReportRecord::day,
                    ReportRecord::id,
                    ReportRecord::itemName,
                    ReportRecord::qualityFrom,
                    ReportRecord::qualityTo
                )
            }
        }
    }
}