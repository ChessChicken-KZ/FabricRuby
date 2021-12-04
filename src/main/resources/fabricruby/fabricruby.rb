require 'java'
require 'fabricruby/minecrafthelper.rb'

class FabricRubyModInstance include Fabric

end

def containsFabricRubyMod(id)
    return Fabric::FabricRubyInit.containsFabricRubyMod(id)
end

def getFabricRubyMod(id)
    return Fabric::FabricRubyInit.getFabricRubyMod(id)
end
