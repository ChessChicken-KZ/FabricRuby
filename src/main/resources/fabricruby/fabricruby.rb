require 'java'
require 'fabricruby/minecrafthelper.rb'

class FabricRubyModInstance include Fabric

end

def containsFabricRubyMod(id)
    return Fabric::FabricRubyProvider.containsFabricRubyMod(id)
end

def getFabricRubyMod(id)
    return Fabric::FabricRubyProvider.getFabricRubyMod(id)
end
