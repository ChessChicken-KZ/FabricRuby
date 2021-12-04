package kz.chesschicken.fabricruby.api.helper;

import kz.chesschicken.fabricruby.FabricRubyInit;
import kz.chesschicken.fabricruby.api.FabricRubyInternal;
import kz.chesschicken.fabricruby.api.FabricRubyModInstance;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricRubyProvider {
    @FabricRubyInternal
    public static boolean containsFabricRubyMod(@NotNull String s) {
        return FabricLoader.getInstance().isModLoaded(s) && FabricRubyInit.FABRICRUBY_MODS.containsKey(s);
    }

    @FabricRubyInternal
    public static @Nullable FabricRubyModInstance getFabricRubyMod(@NotNull String s) {
        if(containsFabricRubyMod(s))
            return FabricRubyInit.FABRICRUBY_MODS.get(s);
        return null;
    }

    @FabricRubyInternal
    public static @Nullable Object getMinecraftInstance() {
        return FabricLoader.getInstance().getGameInstance();
    }
}
