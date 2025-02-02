package nekiplay.meteorplus.features.modules.killaura;

import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.features.modules.nofall.NoFallPlus;
import net.minecraft.client.MinecraftClient;

public class KillAuraPlusMode {
	protected final MinecraftClient mc;
	protected final KillAuraPlus settings;
	private final KillAuraPlusModes type;

	public KillAuraPlusMode(KillAuraPlusModes type) {
		this.settings = Modules.get().get(KillAuraPlus.class);
		this.mc = MinecraftClient.getInstance();
		this.type = type;
	}
}
