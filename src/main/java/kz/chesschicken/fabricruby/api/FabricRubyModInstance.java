package kz.chesschicken.fabricruby.api;

import lombok.Getter;
import net.fabricmc.loader.api.ModContainer;
import org.jruby.runtime.builtin.IRubyObject;

public class FabricRubyModInstance {
    @Getter
    private final ModContainer currentModContainer;
    @Getter
    private IRubyObject classInstance;

    public FabricRubyModInstance(ModContainer m, IRubyObject c) {
        this.currentModContainer = m;
        this.classInstance = c;
    }

    public FabricRubyModInstance(ModContainer m) {
        this.currentModContainer = m;
    }

    public void set(IRubyObject object) {
        if(this.classInstance == null)
            this.classInstance = object;
    }
}
