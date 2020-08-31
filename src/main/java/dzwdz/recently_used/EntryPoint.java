package dzwdz.recently_used;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class EntryPoint implements ClientModInitializer {
    public static final int HISTORY_LENGTH = 54;
    public static LinkedList<ItemStack> recent = new LinkedList<ItemStack>();

    public static final Identifier TRASH_ICON = new Identifier("recently_used", "textures/gui/trash.png");

    private static File storageFile;

    public static final ItemGroup RECENT_GROUP = FabricItemGroupBuilder.create(
            new Identifier("recently_used", "recently_used"))
            .icon(() -> new ItemStack(Items.CLOCK))
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


    public static void load() {
        try {
            CompoundTag compoundTag = NbtIo.read(storageFile);
            if (compoundTag == null) return;
            ListTag listTag = compoundTag.getList("history", 10);

            recent.clear();
            for (int i = 0; i < listTag.size(); i++)
                recent.add(ItemStack.fromTag(listTag.getCompound(i)));

        } catch (Exception e) {
            LogManager.getLogger().error("Failed to load block history", e);
        }
    }

    public static void save() {
        try {
            CompoundTag compoundTag = new CompoundTag();
            ListTag listTag = new ListTag();
            for (ItemStack item : recent) {
                listTag.add(item.toTag(new CompoundTag()));
            }
            compoundTag.put("history", listTag);
            NbtIo.write(compoundTag, storageFile);
        } catch (Exception e) {
            LogManager.getLogger().error("Failed to save block history", e);
        }
    }

    @Override
    public void onInitializeClient() {
        storageFile = new File(MinecraftClient.getInstance().runDirectory, "recentlyUsed.nbt");

        load();
        ClientLifecycleEvents.CLIENT_STOPPING.register((t) -> save());

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
