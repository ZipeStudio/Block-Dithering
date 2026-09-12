package me.zipestudio.blockdithering.mixin.pipeline;

//? if >=26.2
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.shaders.UniformType;
import java.util.List;
import java.util.Optional;
import me.zipestudio.blockdithering.dithering.DitherTargets;
import me.zipestudio.blockdithering.dithering.sodium.SodiumDitherShaderPatcher;
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

	//? if >=26.2 {
	@Shadow private Optional<List<BindGroupLayout>> bindGroupLayouts;
	@Shadow public abstract Builder withBindGroupLayout(BindGroupLayout bindGroupLayout);
	//?} else {
	/*@Shadow private Optional<List<RenderPipeline.UniformDescription>> uniforms;
	@Shadow public abstract Builder withUniform(String name, UniformType type);
	*///?}

	@Inject(method = "build", at = @At("HEAD"))
	private void blockdithering$addDitheringUniform(CallbackInfoReturnable<RenderPipeline> cir) {
		if (this.fragmentShader.isEmpty()) {
			return;
		}
		Identifier fragment = this.fragmentShader.get();
		//? if >=26.2 {
		String name;
		if (DitherTargets.isTarget(fragment)) {
			name = "DitheringData";
		} else if (DitherTargets.isSodiumTarget(fragment)) {
			name = SodiumDitherShaderPatcher.UNIFORM_BLOCK_NAME;
		} else {
			return;
		}
		boolean already = this.bindGroupLayouts.isPresent() && BindGroupLayout.flattenUniforms(this.bindGroupLayouts.get()).stream()
				.anyMatch(u -> name.equals(u.name()));
		if (!already) {
			this.withBindGroupLayout(BindGroupLayout.builder().withUniform(name, UniformType.UNIFORM_BUFFER).build());
		}
		//?} else {
		/*if (!DitherTargets.isTarget(fragment)) {
			return;
		}
		boolean already = this.uniforms.isPresent() && this.uniforms.get().stream()
				.anyMatch(u -> "DitheringData".equals(u.name()));
		if (!already) {
			this.withUniform("DitheringData", UniformType.UNIFORM_BUFFER);
		}
		*///?}
	}
}
