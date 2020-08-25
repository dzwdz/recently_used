package dzwdz.recently_used;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class EntryPoint implements ClientModInitializer {
    public static final int HISTORY_LENGTH = 54;
    public static LinkedList<ItemStack> recent = new LinkedList<ItemStack>();

    public static final ItemGroup RECENT_GROUP = FabricItemGroupBuilder.create(
            new Identifier("recently_used", "recently_used"))
            .icon(() -> new ItemStack(Items.COBWEB))
            .appendItems(stacks -> {
                Stream<ItemStack> stream = recent.stream();
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    for (ItemStack hotbarStack : player.inventory.main.subList(0, 9)) {
                        if (hotbarStack.isEmpty()) continue;
                        ItemStack copy = hotbarStack.copy();
                        copy.setCount(1);
                        stream = stream.filter((ItemStack other) -> !ItemStack.areEqual(copy, other));
                    }
                }
                stacks.addAll(stream.limit(45).collect(Collectors.toList()));
            })
            .build();

    @Override
    public void onInitializeClient() {
        ClientPickBlockApplyCallback.EVENT.register((player, result, stack) -> {
            addToRecent(stack);
            return stack;
        });
    }

    public static void addToRecent(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);

        recent.removeIf((ItemStack other) -> ItemStack.areEqual(copy, other));
        recent.addFirst(stack);
        if (recent.size() > HISTORY_LENGTH) recent.removeLast();
    }
}
