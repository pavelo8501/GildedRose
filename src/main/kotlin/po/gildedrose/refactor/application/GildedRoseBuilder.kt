package po.gildedrose.refactor.application

import po.gildedrose.refactor.item.ItemRecord
import po.misc.callbacks.signal.signalOf
import po.misc.functions.NoResult
import po.misc.types.token.TypeToken


class GildedRoseBuilder<T: ItemRecord>(
    val typeToken: TypeToken<T>
){

    val onReportComplete = signalOf(typeToken, NoResult)


}