package de.chestcolor.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.chestcolor.ChestColorMod;
import de.chestcolor.OpenedChests;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * ChestRenderer#submit übergibt beim Zeichnen des Modells die Tönung -1 (weiß).
 * Diese Konstante ersetzen wir je nach Kiste durch Rot oder Grün.
 */
@Mixin(ChestRenderer.class)
public class ChestRendererMixin {

    @ModifyExpressionValue(method = "submit",
            at = @At(value = "CONSTANT", args = "intValue=-1"))
    private int chestcolor$tint(int original, @Local(argsOnly = true) ChestRenderState state) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return original;
        boolean opened = OpenedChests.isOpened(OpenedChests.dim(mc.level), state.blockPos);
        return opened ? ChestColorMod.RED : ChestColorMod.GREEN;
    }
}
