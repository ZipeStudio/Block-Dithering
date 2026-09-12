package me.zipestudio.blockdithering.mixin;

import dev.isxander.yacl3.gui.OptionListWidget;
import me.zipestudio.blockdithering.yacl.YACLSearchState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionListWidget.class)
public class YACLSearchQueryMixin {

    @Inject(method = "updateSearchQuery", at = @At("HEAD"))
    private void captureSearchQuery(String query, CallbackInfo ci) {
        if (!YACLSearchState.isLeafyScreen()) return;
        YACLSearchState.setQuery(query);
    }

}
