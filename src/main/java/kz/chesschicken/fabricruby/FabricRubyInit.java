package kz.chesschicken.fabricruby;

import kz.chesschicken.fabricruby.api.FabricRubyModInstance;
import kz.chesschicken.fabricruby.api.event.EventType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.mod.PreInitEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.builtin.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class FabricRubyInit {

    //Logger.
    static String debugInstance(FabricRubyModInstance instance) {
        StringBuilder builder = new StringBuilder();
        builder.append("Instance: ").append(instance.getClassInstance().toString()).append("\n")
                .append("Total variables: ").append(instance.getClassInstance().getVariableCount()).append("\n");

        for(Variable<Object> o : instance.getClassInstance().getVariableList()) {
            builder.append(o.getName()).append(" ==> ").append(o.getValue()).append(" (")
            .append("CLASSVAR:").append(o.isClassVariable()).append(";")
            .append("INSTANCEVAR:").append(o.isInstanceVariable()).append(";")
            .append("CONSTANT:").append(o.isConstant()).append(")").append("\n");
        }

        return builder.toString();
    }




    static Consumer<Byte> FUNC_LOAD_RUBY_MAIN = o -> {

        for(ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            if(modContainer.getMetadata().containsCustomValue("fabricruby:load")) {
                FabricRubyModInstance instance = new FabricRubyModInstance(modContainer);

                for (CustomValue value : modContainer.getMetadata().getCustomValue("fabricruby:load").getAsArray())
                    FabricRubyInit.RUBY_INSTANCE.evalScriptlet("require '" + modContainer.getPath(value.getAsString()).toString() + "'");

                RubyModule currentCallerClass = FabricRubyInit.RUBY_INSTANCE.getClassFromPath(modContainer.getMetadata().getId().toUpperCase());
                instance.set(Helpers.invoke(FabricRubyInit.RUBY_INSTANCE.getCurrentContext(), currentCallerClass, "new"));

                if(FabricRubyInit.CONFIG.debug == 1) {
                    FabricRubyInit.LOGGER.info(debugInstance(instance));
                }

                FabricRubyInit.FABRICRUBY_MODS.put(modContainer.getMetadata().getId(), instance);
            }
        }
    };

    static Consumer<Byte> FUNC_PREPARE_LOADED = o -> {
        for(EventType r1 : EventType.values())
            FabricRubyHandler.EVENT_LIST.putIfAbsent(r1, new ArrayList<>());

        for(FabricRubyModInstance instance : FabricRubyInit.FABRICRUBY_MODS.values()) {
            for(Variable<Object> variable : instance.getClassInstance().getVariableList()) {
                if(variable.isInstanceVariable() && variable.getName().startsWith("@event")) {
                    EventType type = EventType.getByName(variable.getName().split("_", 2)[0]);
                    if(type != null)
                        FabricRubyHandler.EVENT_LIST.get(type).add((IRubyObject) variable.getValue());
                }
            }
        }
    };


    @SuppressWarnings("unused")
    @EventListener
    public void initLoader(PreInitEvent event) {
        LOGGER = LogManager.getLogger("FabricRuby");
        RUBY_INSTANCE = Ruby.getGlobalRuntime();
        FABRICRUBY_MODS = new HashMap<>();

        CONFIG = new FabricRubyConfigImpl();
        CONFIG.start();

        FUNC_LOAD_RUBY_MAIN.accept((byte) 0);
        FUNC_PREPARE_LOADED.accept((byte) 0);

    }

    public static Ruby RUBY_INSTANCE;
    public static Map<String, FabricRubyModInstance> FABRICRUBY_MODS;
    public static FabricRubyConfigImpl CONFIG;
    public static Logger LOGGER;
}
