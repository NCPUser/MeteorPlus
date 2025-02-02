package nekiplay.meteorplus.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static nekiplay.meteorplus.features.modules.AutoDropPlus.invIndexToSlotId;

public class ClearInventory extends Command {
	public ClearInventory() {
		super("clearinv", "Clear inventory");
	}
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			for (int i = 0; i < mc.player.getInventory().size(); i++) {
				ItemStack itemStack = mc.player.getInventory().getStack(i);
				if (itemStack != null) {
					mc.interactionManager.clickSlot(0, invIndexToSlotId(i), 300, SlotActionType.SWAP, mc.player);
				}
			}
			return SINGLE_SUCCESS;
		});


	}
}
