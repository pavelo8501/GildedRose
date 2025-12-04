package po.misc.configs.assets.registry

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import po.misc.configs.assets.asset.Asset
import po.misc.configs.assets.AssetManager
import po.misc.configs.assets.asset.AssetPayload
import po.misc.configs.assets.asset.AssetSource
import po.misc.configs.assets.ConfigHolder
import po.misc.configs.assets.asset.NamedAsset
import po.misc.context.component.Component
import po.misc.context.component.ComponentID
import po.misc.context.component.componentID
import po.misc.data.logging.Verbosity
import po.misc.exceptions.managedException
import po.misc.functions.Throwing
import po.misc.io.LocalFile
import po.misc.io.SourcedFile
import po.misc.io.WriteOptions
import po.misc.io.buildRelativePath
import po.misc.io.deleteFile
import po.misc.io.fileExists
import po.misc.io.readSourced
import po.misc.io.writeSourced


@Serializable
data class RegistryPayload(
    val category: String,
    @SerialName("assets")
    val assetSources: Map<String, AssetSource>
)

class AssetRegistry(
    val category: String,
    override var basePath: String,
    val json: Json
): Component, ConfigHolder {

    override val componentID: ComponentID = componentID("AssetRegistry[$category]")

    private var assetChanged: (AssetRegistry.(Asset)-> Unit)? = null

    internal var assetManager: AssetManager? = null

    val registryPath: String = buildRelativePath(basePath, category) + ".json"

    val registrySource: SourcedFile<RegistryPayload>  = loadRegistrySource()

    private var _assets: MutableMap<String, Asset> = mutableMapOf()
    var assets: MutableMap<String, Asset>
        get() = _assets
        set(value) {
            _assets = value
            val payload = RegistryPayload(category, assetsToSource(value))
            val bytes = json.encodeToString(payload).toByteArray()
            registrySource.updateSource(payload, bytes)
        }

    override var config: AssetManager.ConfigData = AssetManager.ConfigData(basePath = registryPath)
    override var verbosity: Verbosity = Verbosity.Info

    val updatePending: Int get() = assets.values.count{ it.updatePending }

    constructor(category: Enum<*>, relativePath: String, json: Json):this(category.name, relativePath, json)

    constructor(manager: AssetManager, category: String,):this(category, manager.basePath, manager.jsonEncoder){
        assetManager = manager
    }

    init {
        initializeFromSource()
    }

    private fun initializeFromSource() {
        val source = registrySource.source
        if (source.assetSources.isNotEmpty()) {
            _assets = sourceToAssets(source.assetSources).toMutableMap()
            _assets.values.forEach { it.state = Asset.State.InSync }
        }
    }

    private fun sourceToAssets(sourceMap:  Map<String, AssetSource>): Map<String, Asset>{
       return sourceMap.mapValues { (_, source) ->
           val newAsset = Asset(this, source)
           addOrReinit(newAsset)
        }
    }

    private fun assetsToSource(assetsMap: Map<String, Asset>): Map<String, AssetSource> = assetsMap.mapValues { (_, asset) -> asset.source }

    private fun loadRegistrySource(): SourcedFile<RegistryPayload> {
        val subject = "Load Registry"
        val meta = fileExists(registryPath)
        return if (meta == null) {

            info(subject, "Created registry $category")
            RegistryPayload(category, emptyMap()).writeSourced(registryPath, WriteOptions(overwriteExistent = false)) {
                    json.encodeToString(it).toByteArray()
                }
        } else {
            info(subject, "Registry $category loaded")
            readSourced(registryPath, Charsets.UTF_8) {
                json.decodeFromString<RegistryPayload>(it)
            }
        }
    }

    private fun addNewAsset(asset: Asset){
        asset.updated = {
            assetChanged?.invoke(this, it)
        }
        assets[asset.name] = asset
        asset.state = Asset.State.Updated
    }

    private fun addOrReinit(asset: Asset): Asset {
        val existent =  assets[asset.name]
        if(existent != null){
            if(existent != asset){
                addNewAsset(asset)
            }else{
                return existent
            }
        }else{
            addNewAsset(asset)
        }
        return asset
    }

    fun onAssetChanged(callback: AssetRegistry.(Asset)-> Unit){
        assetChanged = callback
    }

    fun addAsset(localFile: LocalFile, name: String): Asset {
        val assetName = normalizeAssetName(name)
        val newAsset = Asset(this, localFile, assetName)
        return addOrReinit(newAsset)
    }
    fun addAsset(localFile: LocalFile, assetName: NamedAsset): Asset = addAsset(localFile, assetName.name)

    fun addAsset(assetSource : AssetPayload): Asset {
        val newAsset = Asset(this, assetSource)
        return  addOrReinit(newAsset)
    }

    fun assets(state: Asset.State): List<Asset> = assets.values.filter { it.state == state }

    fun get(name: String): Asset?{
       return assets[name]
    }
    fun get(assetName: NamedAsset): Asset? = get(assetName.name)

    fun get(throwing: Throwing, name: String): Asset {
        return assets[name].getOrThrow {
            managedException("Asset $name not found")
        }
    }
    fun get(throwing: Throwing, assetName: NamedAsset): Asset = get(throwing, assetName.name)

    fun commitChanges(): Boolean {
        if (updatePending == 0) {
            return false
        }
        val newMap = mutableMapOf<String, Asset>()
        for(asset in assets.values){
            when(asset.state){
                Asset.State.InSync ->{
                    newMap[asset.name] = asset
                }
                Asset.State.Updated ->{
                    asset.state = Asset.State.InSync
                    newMap[asset.name] = asset
                }
                Asset.State.MarkedDeleteWithFile->{
                    deleteFile(asset.filePath)
                }
                else -> {}
            }
        }
        assets = newMap
        return true
    }

    fun deleteAsset(name: String, includingFile: Boolean): Boolean{
       return assets[name]?.let {
            if(includingFile) {
                it.state =  Asset.State.MarkedDeleteWithFile
            }else {
                it.state = Asset.State.MarkedDelete
            }
            true
        }?:false
    }

    fun deleteAsset(asset: Asset, includingFile: Boolean): Boolean
        = deleteAsset(asset.name, includingFile)

    fun purge(){
        assets = mutableMapOf()
    }

    companion object{
        fun normalizeAssetName(name: String): String{
            return name.trim()
        }
        fun normalizeAssetName(assetName: NamedAsset): String{
            return assetName.name.trim()
        }
    }
}
