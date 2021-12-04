package kz.chesschicken.fabricruby;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.registry.ItemRegistryEvent;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.ArrayList;
import java.util.List;

public class FabricRubyHandler {

    public static List<IRubyObject> EVENT_POST_INIT = new ArrayList<>();
    public static List<IRubyObject> EVENT_ITEM_INIT = new ArrayList<>();

    @SuppressWarnings("unused")
    @EventListener
    public void initItem(ItemRegistryEvent event) {
        for(IRubyObject o : EVENT_ITEM_INIT) {
            System.out.println("CALLING: " + o.toString());
            Helpers.invoke(FabricRubyInit.getRUBY_INSTANCE().getCurrentContext(), o, "handle");
        }
    }

}
