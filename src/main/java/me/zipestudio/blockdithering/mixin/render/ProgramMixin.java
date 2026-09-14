package me.zipestudio.blockdithering.mixin.render;

//? if <1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import java.io.InputStream;
import java.util.List;
import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.dithering.DitherTargets;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Program.class)
public class ProgramMixin {

	@WrapOperation(
			method = "compileShaderInternal",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;process(Ljava/lang/String;)Ljava/util/List;"
			)
	)
	private static List<String> blockdithering$injectDitheringSource(GlslPreprocessor preprocessor, String source, Operation<List<String>> original,
			Program.Type type, String name, InputStream stream, String packName, GlslPreprocessor unused) {
		if (DitherTargets.isLegacyTarget(name) || DitherTargets.isLegacyOutline(name)) {
			String patched = DitherVanillaPatcher.patchLegacy(name, type == Program.Type.VERTEX, source);
			BlockDithering.LOGGER.info("Vanilla {} shader '{}' dither-patched: {}", type.getName(), name, !patched.equals(source));
			source = patched;
		}
		return original.call(preprocessor, source);
	}
}
//?}
