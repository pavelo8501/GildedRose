package po.gildedrose.refactor.conditions

import po.gildedrose.refactor.ItemGroup
import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.item.Quality


class UpdateCondition(
    val itemGroup: ItemGroup,
    val conditionalUpdater: (ItemRecord) -> Unit
){

    fun update(item: ItemRecord){
        conditionalUpdater.invoke(item)
    }
}