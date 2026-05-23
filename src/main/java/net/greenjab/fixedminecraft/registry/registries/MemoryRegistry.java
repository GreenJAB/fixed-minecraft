package net.greenjab.fixedminecraft.registry.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.Item;
import java.util.Optional;

public class MemoryRegistry {

    public static final MemoryModuleType<Item> LAST_ITEM_TYPE = register("last_item_type");
    public static final MemoryModuleType<Integer> TIME_SINCE_GOSSIP = register("time_since_gossip");
    public static final MemoryModuleType<Integer> TIME_SINCE_SLEEP = register("time_since_sleep");
    public static final MemoryModuleType<Integer> TIME_SINCE_WALK = register("time_since_walk");
    public static final MemoryModuleType<Integer> TIME_SINCE_EAT = register("time_since_eat");
    public static final MemoryModuleType<Integer> TIME_SINCE_SUN = register("time_since_sun");


    private static <U> MemoryModuleType<U> register(String id) {
        return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, Identifier.withDefaultNamespace(id), new MemoryModuleType<>(Optional.empty()));
    }
    public static void registerMemories() {
        System.out.println("register Memories");
    }
}
