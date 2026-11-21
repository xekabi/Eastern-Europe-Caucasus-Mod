package com.eac16;
import net.minecraftforge.common.ForgeConfigSpec;

public final class VisualConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue CAMERA_SWAY;
    static {
        var b = new ForgeConfigSpec.Builder();
        b.comment("Set 0 to disable camera intoxication motion (accessibility)").push("accessibility");
        CAMERA_SWAY = b.defineInRange("cameraSway", 0.7D, 0.0D, 1.0D);
        b.pop();
        SPEC = b.build();
    }
    private VisualConfig() {}
}
