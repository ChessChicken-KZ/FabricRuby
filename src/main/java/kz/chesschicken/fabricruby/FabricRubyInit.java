package kz.chesschicken.fabricruby;

import kz.chesschicken.fabricruby.api.FabricRubyInternal;
import kz.chesschicken.fabricruby.api.FabricRubyModInstance;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.modificationstation.stationapi.api.event.mod.PostInitEvent;
import net.modificationstation.stationapi.api.event.mod.PreInitEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Helpers;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.builtin.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static kz.chesschicken.fabricruby.FabricRubyHandler.EVENT_ITEM_INIT;
import static kz.chesschicken.fabricruby.FabricRubyHandler.EVENT_POST_INIT;

public class FabricRubyInit {


    private static String buildDebug(FabricRubyModInstance instance) {
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
                    FabricRubyInit.LOGGER.info(buildDebug(instance));
                }

                FabricRubyInit.FABRICRUBY_MODS.put(modContainer.getMetadata().getId(), instance);
            }
        }
    };

    static Consumer<Byte> FUNC_PREPARE_LOADED = o -> {
        for(FabricRubyModInstance instance : FabricRubyInit.FABRICRUBY_MODS.values()) {
            for(Variable<Object> variable : instance.getClassInstance().getVariableList()) {
                if(variable.isInstanceVariable() && variable.getName().startsWith("@event_")) {
                    //Release
                    if(variable.getName().startsWith("@event_postInit_")) {
                        EVENT_POST_INIT.add((IRubyObject) variable.getValue());
                    }
                    if(variable.getName().startsWith("@event_item_")) {
                        EVENT_ITEM_INIT.add((IRubyObject) variable.getValue());
                    }
                }
            }
        }
    };


    @SuppressWarnings("unused")
    @EventListener
    public void initLoader(PreInitEvent event) {
        RUBY_INSTANCE = Ruby.getGlobalRuntime();
        FABRICRUBY_MODS = new HashMap<>();

        CONFIG = new FabricRubyConfigImpl();
        CONFIG.start();

        FUNC_LOAD_RUBY_MAIN.accept((byte) 0);
        FUNC_PREPARE_LOADED.accept((byte) 0);

    }

    @SuppressWarnings("unused")
    @EventListener
    public void postInitLoader(PostInitEvent event) {
        for(IRubyObject object : EVENT_POST_INIT) {
            System.out.println("CALLING: " + object.toString());
            Helpers.invoke(FabricRubyInit.getRUBY_INSTANCE().getCurrentContext(), object, "handle", JavaUtil.convertJavaToRuby(FabricRubyInit.getRUBY_INSTANCE(), object));
        }
    }


    @Getter
    private static Ruby RUBY_INSTANCE;

    private static Map<String, FabricRubyModInstance> FABRICRUBY_MODS;

    @FabricRubyInternal
    public static boolean containsFabricRubyMod(@NotNull String s) {
        return FabricLoader.getInstance().isModLoaded(s) && FABRICRUBY_MODS.containsKey(s);
    }

    @FabricRubyInternal
    public static @Nullable FabricRubyModInstance getFabricRubyMod(@NotNull String s) {
        if(containsFabricRubyMod(s))
            return FABRICRUBY_MODS.get(s);
        return null;
    }

    @Getter
    private static FabricRubyConfigImpl CONFIG;

    public static final Logger LOGGER = LogManager.getLogger("FabricRuby");
}
