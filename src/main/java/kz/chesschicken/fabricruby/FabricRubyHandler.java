package kz.chesschicken.fabricruby;

import kz.chesschicken.fabricruby.api.event.EventType;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.mod.PostInitEvent;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricRubyHandler {

    public static Map<EventType, List<IRubyObject>> EVENT_LIST = new HashMap<>();

    @SuppressWarnings("unused")
    @EventListener
    public void initItem(ItemRegistryEvent event) {
        for(IRubyObject o : EVENT_LIST.get(EventType.ITEMINIT)) {

            if(FabricRubyInit.CONFIG.debug == 1)
                FabricRubyInit.LOGGER.info("CALLING: " + o.toString());

            Helpers.invoke(FabricRubyInit.RUBY_INSTANCE.getCurrentContext(), o, "handle", JavaUtil.convertJavaToRuby(FabricRubyInit.RUBY_INSTANCE, event));
        }
    }


    @SuppressWarnings("unused")
    @EventListener
    public void postInitLoader(PostInitEvent event) {
        for(IRubyObject o : EVENT_LIST.get(EventType.POSTINIT)) {

            if(FabricRubyInit.CONFIG.debug == 1)
                FabricRubyInit.LOGGER.info("CALLING: " + o.toString());

            Helpers.invoke(FabricRubyInit.RUBY_INSTANCE.getCurrentContext(), o, "handle", JavaUtil.convertJavaToRuby(FabricRubyInit.RUBY_INSTANCE, event));
        }
    }

}
