require 'java'

module Fabric
    include_package 'net.fabricmc.loader.api'
    include_package 'kz.chesschicken.fabricruby'
end

module StationAPI
    include_package 'net.modificationstation.stationapi.api.registry'
end

module Minecraft
    include_package 'net.minecraft.block.material'
    include_package 'net.minecraft.item'
    include_package 'net.minecraft.item.tool'
end

def getMinecraftInstance()
    return Fabric::FabricLoader.getInstance.getGameInstance
end