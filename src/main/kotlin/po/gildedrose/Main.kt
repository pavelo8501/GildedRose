package po.gildedrose

import po.gildedrose.models.FixtureData
import po.gildedrose.refactor.item.GRItem
import po.gildedrose.refactor.item.ItemRecord
import po.gildedrose.refactor.item.toGRItems
import po.misc.data.output.output


private fun  runMain(
    args: Array<String>,
    useItems: List<ItemRecord>?,
    useApp: GildedRose<GRItem>? = null,
    beforeCalculated: ((FixtureData)-> Unit)? = null
){

    println("OMGHAI!")


    val app = useApp?:run {
        if (useItems == null) {
            GildedRose(GildedRose.defaultItems.toGRItems())
        } else {
            GildedRose(useItems)
        }
    }

    var days = 2
    if (args.size > 0) {
        days = Integer.parseInt(args[0]) + 1
    }
    for (i in 0..days - 1) {
        println("-------- day $i --------")
        println("name, sellIn, quality")
        for (item in app.items) {
            beforeCalculated?.let {
                val data = FixtureData(day = i, name = item.name, sellIn = item.sellIn, quality = item.quality)
                it.invoke(data)
            }
            println(item)
        }
        println()
        app.updateQuality(i)
    }
}

fun printHelp() {
    println(
        """
        Commands:
          run <days>                   - run using default items
          run <days> pretty            - run using default items and pretty console printout
          exit                         - quit program
        """.trimIndent()
    )
}

fun runCommand(input: String) {
    val tokens = input.split(" ")
    val days = tokens.getOrNull(1)?.toIntOrNull()
    if (days == null){
        println("Invalid number of days.")
        return
    }
    val modifier = tokens.getOrNull(2)?:""
    when {
        tokens.size < 2 -> {
            println("Missing day count.")
            return
        }
        tokens.size == 2 -> {
            runMain(arrayOf(days.toString()), useItems = null)
        }
        tokens.size == 3 && modifier == "pretty" -> {
            val app = GildedRose(GildedRose.defaultItems.toGRItems()){
                includeToReport {
                    true
                }
            }
            runMain(arrayOf(days.toString()), useApp = app, useItems = null)
            app.collectReport().output()
        }
        else -> println("Unknown run format. Type 'help'.")
    }
}

fun interactiveConsole() {
    println("=== Gilded Rose Simulator ===")
    println("Type 'help' for commands, 'exit' to quit")
    while (true) {
        println("> ")
        val input = readLine() ?: break
        //val input = readlnOrNull()?.trim() ?: continue
        when {
            input.equals("exit", true) -> {
                println("Goodbye!")
                return
            }
            input.equals("help", true) -> printHelp()
            input.startsWith("run ") -> runCommand(input)
            else -> println("Unknown command. Type 'help'.")
        }
    }
}


fun main(
    numberOfDays: Int,
    useItems: List<GRItem>,
    beforeCalculated: ((FixtureData)-> Unit)?
) = runMain(arrayOf(numberOfDays.toString()), useItems, beforeCalculated = beforeCalculated)


fun main(numberOfDays: Int, beforeCalculated: (FixtureData)-> Unit) =
    runMain(arrayOf(numberOfDays.toString()), useItems = null,  beforeCalculated = beforeCalculated)

fun main(numberOfDays: Int, app: GildedRose<GRItem>,  beforeCalculated: ((FixtureData)-> Unit)? = null) =
    runMain(arrayOf(numberOfDays.toString()), useItems = null, useApp = app, beforeCalculated = beforeCalculated)

//fun main(args: Array<String>) = runMain(args, useItems = null)

fun main(){
    interactiveConsole()
}



