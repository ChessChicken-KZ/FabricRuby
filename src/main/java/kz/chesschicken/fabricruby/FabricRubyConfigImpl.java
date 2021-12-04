package kz.chesschicken.fabricruby;

import kz.chesschicken.configapi.ConfigInstance;
import kz.chesschicken.configapi.instance.Group;
import kz.chesschicken.configapi.instance.Property;

public class FabricRubyConfigImpl extends ConfigInstance {
    public byte multithreading = 0;
    public byte debug = 0;

    public FabricRubyConfigImpl() {
        super("fabricruby");
    }

    @Override
    public void saveConfig() {
        Group general = Group.createGroup("general");
        general.add(Property.createProperty("multithreading", false), "Launches ruby processes as an another parallel thread.");
        general.add(Property.createProperty("debug", false), "Output everything. Debug.");

        instance.add(general);
    }

    @Override
    public void applyConfig() {
        multithreading = (byte) (((boolean) getValue("general", "multithreading")) ? 1 : 0);
        debug = (byte) (((boolean) getValue("general", "debug")) ? 1 : 0);
    }
}
