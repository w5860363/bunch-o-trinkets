package dev.emi.bunchotrinkets.items;

import java.util.List;

import dev.emi.trinkets.api.ITrinket;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GluttonyAmulet extends Item implements ITrinket {

	public GluttonyAmulet() {
		super(new Settings().group(ItemGroup.TOOLS).maxCount(1));
		DispenserBlock.registerBehavior(this, TRINKET_DISPENSER_BEHAVIOR);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		return ITrinket.equipTrinket(player, hand);
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World world, List<Text> list, TooltipContext context) {
		list.add(new LiteralText("Consumes food so you don't have to").formatted(Formatting.GOLD));
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.CHEST) && slot.equals(Slots.NECKLACE);
	}

	@Override
	public void tick(PlayerEntity player, ItemStack trinket) {
		if (player.getHungerManager().isNotFull()) {
			ItemStack toEat = ItemStack.EMPTY;
			for (int i = 0; i < player.inventory.getInvSize(); i++) {
				ItemStack stack = player.inventory.getInvStack(i);
				if (stack.isFood()) {
					if (toEat.isEmpty()) toEat = stack;
					else toEat = getOptimalFood(toEat, stack);
				}
			}
			if (!toEat.isEmpty()) {
				player.eatFood(player.getEntityWorld(), toEat);
			}
		}
	}

	public ItemStack getOptimalFood(ItemStack stack1, ItemStack stack2) {
		if (stack1.getItem().getFoodComponent().getStatusEffects().isEmpty() && !stack2.getItem().getFoodComponent().getStatusEffects().isEmpty()) {
			return stack1;
		} else if (!stack1.getItem().getFoodComponent().getStatusEffects().isEmpty() && stack2.getItem().getFoodComponent().getStatusEffects().isEmpty()) {
			return stack2;
		}
		if (stack1.getItem().getFoodComponent().getHunger() > stack2.getItem().getFoodComponent().getHunger()) {
			return stack2;
		} else {
			return stack1;
		}
	}
}