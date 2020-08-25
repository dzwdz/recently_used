package dzwdz.recently_used;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

@Environment(EnvType.CLIENT)
public class EntryPoint implements ClientModInitializer {
    public static final int HISTORY_LENGTH = 45;
    public static LinkedList<ItemStack> recent = new LinkedList<ItemStack>();

    public static final ItemGroup RECENT_GROUP = FabricItemGroupBuilder.create(
            new Identifier("recently_used", "recently_used"))
            .icon(() -> new ItemStack(Items.COBWEB))
            .appendItems(stacks -> {
                for (ItemStack s : recent) stacks.add(s);
            })
            .build();

    @Override
    public void onInitializeClient() {

    }

    public static void addToRecent(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);

        recent.removeIf((ItemStack other) -> {return ItemStack.areEqual(copy, other);});
        recent.addFirst(stack);
        if (recent.size() > HISTORY_LENGTH) recent.removeLast();
    }
}
