require 'java'
require 'fabricruby/minecrafthelper.rb'
require 'fabricruby/fabricruby.rb'

#Worst example of the usage.

class TestPostInitEventImpl
    def handle(event)
        puts "Contains random mod: #{containsFabricRubyMod("random")}"
    end
end

#It automatically tries to find class with uppercase mod id.
class FABRICRUBY < FabricRubyModInstance

    def initialize()
        @event_postInit_AnyCoolNameToo = TestPostInitEventImpl.new
    end

end