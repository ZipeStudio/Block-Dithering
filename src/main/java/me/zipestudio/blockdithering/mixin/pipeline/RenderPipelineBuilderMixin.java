package me.zipestudio.blockdithering.mixin.pipeline;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.shaders.UniformType;
import java.util.List;
import java.util.Optional;
import me.zipestudio.blockdithering.dithering.DitherTargets;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@Mixin(RenderPipeline.Builder.class)
public abstract class RenderPipelineBuilderMixin {

	@Shadow private Optional<Identifier> fragmentShader;
	@Shadow private Optional<List<RenderPipeline.UniformDescription>> uniforms;

	@Shadow public abstract Builder withUniform(String name, UniformType type);

	@Inject(method = "build", at = @At("HEAD"))
	private void blockdithering$addDitheringUniform(CallbackInfoReturnable<RenderPipeline> cir) {
		if (this.fragmentShader.isEmpty() || !DitherTargets.isTarget(this.fragmentShader.get())) {
			return;
		}
		boolean already = this.uniforms.isPresent() && this.uniforms.get().stream()
				.anyMatch(u -> "DitheringData".equals(u.name()));
		if (!already) {
			this.withUniform("DitheringData", UniformType.UNIFORM_BUFFER);
		}
	}
}
