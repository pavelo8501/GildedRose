package po.misc.configs.assets.registry

import po.misc.configs.assets.asset.AssetPayload

interface RegistrySource {
    val name: String
    val assets: Array<out AssetPayload>
}