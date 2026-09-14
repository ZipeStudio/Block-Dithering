package me.zipestudio.blockdithering.mixin.veil;

//? if <1.21.11 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Map;
import me.zipestudio.blockdithering.BlockDithering;
import me.zipestudio.blockdithering.dithering.DitherTargets;
import me.zipestudio.blockdithering.dithering.DitherVanillaPatcher;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "foundry.veil.impl.client.render.dynamicbuffer.VanillaShaderCompiler")
public class VanillaShaderCompilerMixin {

	@WrapOperation(
			method = "compileShader",
			at = @At(
					value = "INVOKE",
					target = "Lfoundry/veil/impl/client/render/shader/processor/VanillaShaderProcessor;modify(Ljava/util/Map;Ljava/lang/String;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;IILjava/lang/String;Lorg/lwjgl/opengl/GLCapabilities;)Ljava/lang/String;"
			)
	)
	private String blockdithering$patchVeilSource(Map<String, Object> customProgramData, String shaderName, ResourceLocation path,
			VertexFormat vertexFormat, int activeBuffers, int type, String source, GLCapabilities capabilities, Operation<String> original) {
		String processed = original.call(customProgramData, shaderName, path, vertexFormat, activeBuffers, type, source, capabilities);
		if (processed == null || !(DitherTargets.isLegacyTarget(shaderName) || DitherTargets.isLegacyOutline(shaderName))) {
			return processed;
		}
		String patched = DitherVanillaPatcher.patchLegacy(shaderName, type == GL20.GL_VERTEX_SHADER, processed);
		BlockDithering.LOGGER.info("Veil {} shader '{}' dither-patched: {}", type == GL20.GL_VERTEX_SHADER ? "vertex" : "fragment", shaderName, !patched.equals(processed));
		return patched;
	}
}
//?}
