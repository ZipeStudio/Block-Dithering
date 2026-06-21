package me.zipestudio.blockdithering.mixin;

import dev.isxander.yacl3.api.ListOptionEntry;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import dev.isxander.yacl3.gui.controllers.ListEntryWidget;
import me.zipestudio.blockdithering.yacl.LeafyYaclScreen;
import me.zipestudio.blockdithering.yacl.YACLSearchState;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;

@Mixin(ListEntryWidget.class)
public class YACLSearchMixin {

    @Shadow
    @Final
    private ListOptionEntry<?> listOptionEntry;

    @Shadow
    @Final
    private TooltipButtonWidget moveUpButton;

    @Shadow
    @Final
    private TooltipButtonWidget moveDownButton;

    @Inject(method = "matchesSearch", at = @At("HEAD"), cancellable = true)
    private void injectMatchesSearch(String query, CallbackInfoReturnable<Boolean> cir) {

        if (!(Minecraft.getInstance().screen instanceof LeafyYaclScreen)) return;

        Object value = this.listOptionEntry.pendingValue();
        if (value == null) return;

        cir.setReturnValue(value.toString().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));

    }

    @Inject(method = "updateButtonStates", at = @At("TAIL"))
    private void disableMoveWhileSearching(CallbackInfo ci) {

        if (!(Minecraft.getInstance().screen instanceof LeafyYaclScreen)) return;
        if (!YACLSearchState.isSearchActive()) return;
        this.moveUpButton.active = false;
        this.moveDownButton.active = false;

    }


}
