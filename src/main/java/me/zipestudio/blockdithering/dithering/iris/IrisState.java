package me.zipestudio.blockdithering.dithering.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.lopymine.mossylib.loader.MossyLoader;

public final class IrisState {

	private static final boolean IRIS_LOADED = MossyLoader.isModLoaded("iris", true);

	private static volatile boolean outlineProgramPatched;

	public static boolean isOutlineProgramPatched() {
		return outlineProgramPatched;
	}

	public static void setOutlineProgramPatched(boolean patched) {
		outlineProgramPatched = patched;
	}

	public static boolean isShaderPackInUse() {
		return IRIS_LOADED && Holder.isShaderPackInUse();
	}

	private static final class Holder {

		private static boolean isShaderPackInUse() {
			return IrisApi.getInstance().isShaderPackInUse();
		}
	}

	private IrisState() { }
}
