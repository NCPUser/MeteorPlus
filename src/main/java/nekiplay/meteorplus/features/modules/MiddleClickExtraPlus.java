package nekiplay.meteorplus.features.modules;

import meteordevelopment.meteorclient.events.entity.player.FinishUsingItemEvent;
import meteordevelopment.meteorclient.events.entity.player.StoppedUsingItemEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import nekiplay.meteorplus.MeteorPlus;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickExtraPlus extends Module {
	public MiddleClickExtraPlus() {
		super(MeteorPlus.CATEGORY, "Middle-click-extra+", "Lets you use items when you middle click.");
	}
	private enum Type {
		Immediate,
		LongerSingleClick,
		Longer
	}

	public enum Mode {
		Pearl(Items.ENDER_PEARL, Type.Immediate),
		Rocket(Items.FIREWORK_ROCKET, Type.Immediate),

		Rod(Items.FISHING_ROD, Type.LongerSingleClick),

		Bow(Items.BOW, Type.Longer),
		Gap(Items.GOLDEN_APPLE, Type.Longer),
		EGap(Items.ENCHANTED_GOLDEN_APPLE, Type.Longer),
		Chorus(Items.CHORUS_FRUIT, Type.Longer);

		private final Item item;
		private final Type type;

		Mode(Item item, Type type) {
			this.item = item;
			this.type = type;
		}
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
		.name("mode")
		.description("Which item to use when you middle click.")
		.defaultValue(Mode.Pearl)
		.build()
	);

	private final Setting<Boolean> noInventory = sgGeneral.add(new BoolSetting.Builder()
		.name("Anti-inventory")
		.description("Not work in inventory.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
		.name("notify")
		.description("Notifies you when you do not have the specified item in your hotbar.")
		.defaultValue(true)
		.build()
	);

	private boolean isUsing;

	@Override
	public void onDeactivate() {
		stopIfUsing();
	}

	@EventHandler
	private void onMouseButton(MouseButtonEvent event) {
		if (event.action != KeyAction.Press || event.button != GLFW_MOUSE_BUTTON_MIDDLE) return;
		if (noInventory.get() && mc.currentScreen != null) return;

		FindItemResult result = InvUtils.findInHotbar(mode.get().item);

		if (!result.found()) {
			if (notify.get()) warning("Unable to find specified item.");
			return;
		}

		InvUtils.swap(result.slot(), true);

		switch (mode.get().type) {
			case Immediate -> {
				if (mc.interactionManager != null){
					mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
					InvUtils.swapBack();
				}
			}
			case LongerSingleClick ->{
				if (mc.interactionManager != null){
					mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
				}
			}
			case Longer -> {
				mc.options.useKey.setPressed(true);
				isUsing = true;
			}
		}
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (isUsing) {
			boolean pressed = true;

			if (mc.player != null && mc.player.getMainHandStack().getItem() instanceof BowItem) {
				pressed = BowItem.getPullProgress(mc.player.getItemUseTime()) < 1;
			}

			mc.options.useKey.setPressed(pressed);
		}
	}

	@EventHandler
	private void onFinishUsingItem(FinishUsingItemEvent event) {
		stopIfUsing();
	}

	@EventHandler
	private void onStoppedUsingItem(StoppedUsingItemEvent event) {
		stopIfUsing();
	}

	private void stopIfUsing() {
		if (isUsing) {
			mc.options.useKey.setPressed(false);
			InvUtils.swapBack();
			isUsing = false;
		}
	}
}
