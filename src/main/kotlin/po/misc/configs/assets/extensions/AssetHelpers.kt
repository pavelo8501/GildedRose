package po.misc.configs.assets.extensions

import po.misc.configs.assets.registry.AssetRegistry


fun List<AssetRegistry>.first(
    category: String,
): AssetRegistry = first { it.equals(category)}

fun List<AssetRegistry>.first(
    category: Enum<*>,
): AssetRegistry = first { it.equals(category)}


fun List<AssetRegistry>.firstOrNull(
    category: String,
): AssetRegistry? = firstOrNull{ it.equals(category) }

fun List<AssetRegistry>.firstOrNull(
    category: Enum<*>,
): AssetRegistry? = firstOrNull{ it.equals(category) }

