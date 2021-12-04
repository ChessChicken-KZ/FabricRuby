require 'java'

module Fabric
    include_package 'net.fabricmc.loader'
    include_package 'net.fabricmc.loader.api'
    include_package 'kz.chesschicken.fabricruby'
    include_package 'kz.chesschicken.fabricruby.api'
    include_package 'kz.chesschicken.fabricruby.api.helper'
    include_package 'kz.chesschicken.fabricruby.api.event'
end

def getMinecraftInstance()
    return Fabric::FabricRubyProvider.getMinecraftInstance
end