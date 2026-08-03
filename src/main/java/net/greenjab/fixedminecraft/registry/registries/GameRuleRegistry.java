package net.greenjab.fixedminecraft.registry.registries;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.minecraft.world.level.gamerules.GameRules;
import java.util.function.ToIntFunction;

public class GameRuleRegistry {

    public static final GameRuleCategory JABSFIXEDENCHANTING = GameRuleCategory.register(FixedMinecraft.id("aaa_jabsfixedenchanting"));
    public static final GameRuleCategory JABSFIXEDTRANSPORT = GameRuleCategory.register(FixedMinecraft.id("aab_jabsfixedtransport"));
    public static final GameRuleCategory JABSFIXEDCOMBAT = GameRuleCategory.register(FixedMinecraft.id("aac_jabsfixedcombat"));
    public static final GameRuleCategory JABSFIXEDMOBSANDBLOCKS = GameRuleCategory.register(FixedMinecraft.id("aad_jabsfixedmobsandblocks"));

    public static GameRule<Integer> ENCHANT_CAPACITY_PERCENTAGE;
    public static GameRule<Integer> SUPER_ENCHANT_CHANCE;
    public static GameRule<Boolean> COMBINE_ENCHANTED_ITEMS;
    public static GameRule<Boolean> MENDING_ON_OP_ITEMS;
    public static GameRule<Boolean> VILLAGERS_BIOME_ENCHANTED_BOOKS;
    public static GameRule<Boolean> GRINDSTONE_DAMAGES_ITEM;
    public static GameRule<Boolean> GOLD_GEAR_AUTO_REPAIRS;

    public static GameRule<Boolean> ICE_MELT_IN_NETHER;
    public static GameRule<Boolean> REMOVE_VANILLA_NAUTILUS_ARMOUR;
    public static GameRule<Boolean> ELYTRA_DRAG;
    public static GameRule<Integer> ELYTRA_FLY_IN_RAIN;
    public static GameRule<Integer> ELYTRA_FIREWORK_NERF;
    public static GameRule<Integer> ELYTRA_DEPLOYMENT_TICKS;
    public static GameRule<Integer> ELYTRA_HIT_CANCEL_TICKS;

    public static GameRule<Boolean> REQUIRE_TOTEM_USE;
    public static GameRule<Boolean> RAID_REPLACE_EVOKERS_WITH_ILLUSIONERS;
    public static GameRule<Boolean> NERF_VANILLA_SPEARS;
    public static GameRule<Boolean> SPEARS_ONLY_HORIZONTAL;
    public static GameRule<Integer> STAMINA_DRAIN_SPEED;
    public static GameRule<Boolean> EAT_DURATION_PROPORTIONAL_TO_FOOD;
    public static GameRule<Boolean> EAT_HIT_CANCELLING;
    public static GameRule<Boolean> RESPAWN_WITH_LESS_HEALTH;
    public static GameRule<Boolean> PARTIAL_KEEP_INVENTORY;
    public static GameRule<Integer> ITEM_DEATH_DESPAWN_TIME;
    public static GameRule<Boolean> STRONGER_MOBS;
    public static GameRule<Boolean> MOBS_LEAVE_VEHICLES_WHEN_ATTACKED;
    public static GameRule<Boolean> BETTER_WITHER_FIGHT;
    public static GameRule<Boolean> BETTER_DRAGON_FIGHT;
    public static GameRule<Boolean> DRAGON_WORLD_BORDER_BEFORE_KILL;

    public static GameRule<Boolean> PEACEFUL_MOB_GRIEFING;
    public static GameRule<Integer> NIGHTS_UNTIL_INSOMNIA;
    public static GameRule<Boolean> INSOMNIA_SLEEP_REQUIREMENT;
    public static GameRule<Boolean> VILLAGERS_NEED_SLEEP;
    public static GameRule<Boolean> VILLAGERS_NEED_FOOD;
    public static GameRule<Boolean> VILLAGERS_NEED_SUNLIGHT;
    public static GameRule<Boolean> VILLAGERS_NEED_FRIENDS;
    public static GameRule<Boolean> VILLAGERS_NEED_SPACE;
    public static GameRule<Boolean> VILLAGERS_TRADE_AT_NIGHT;
    public static GameRule<Boolean> VILLAGERS_STRONGER_DEMAND;
    public static GameRule<Boolean> VILLAGERS_NITWITIFY_ON_ZOMBIFICATION;
    public static GameRule<Boolean> ONE_IRON_GOLEM_PER_MOB;
    public static GameRule<Boolean> HOSTILE_SNIFFER_PLANTS;


    public static void registerGameRules() {
        System.out.println("register GameRules");
        ENCHANT_CAPACITY_PERCENTAGE = registerInteger("enchant_capacity_percentage", JABSFIXEDENCHANTING, 54, 1, 100);
        SUPER_ENCHANT_CHANCE = registerInteger("super_enchant_chance", JABSFIXEDENCHANTING, 5, 0, 100);
        COMBINE_ENCHANTED_ITEMS = registerBoolean("combine_enchanted_items", JABSFIXEDENCHANTING, false);
        MENDING_ON_OP_ITEMS = registerBoolean("mending_on_op_items", JABSFIXEDENCHANTING, false);
        VILLAGERS_BIOME_ENCHANTED_BOOKS = registerBoolean("villagers_biome_enchanted_books", JABSFIXEDENCHANTING, true);
        GRINDSTONE_DAMAGES_ITEM = registerBoolean("grindstone_damages_item", JABSFIXEDENCHANTING, true);
        GOLD_GEAR_AUTO_REPAIRS = registerBoolean("gold_gear_auto_repairs", JABSFIXEDENCHANTING, true);

        ICE_MELT_IN_NETHER = registerBoolean("ice_melt_in_nether", JABSFIXEDTRANSPORT, true);
        REMOVE_VANILLA_NAUTILUS_ARMOUR = registerBoolean("remove_vanilla_nautilus_rmour", JABSFIXEDTRANSPORT, true);
        ELYTRA_DRAG = registerBoolean("elytra_drag", JABSFIXEDTRANSPORT, false);
        ELYTRA_FLY_IN_RAIN = registerInteger("elytra_fly_in_rain", JABSFIXEDTRANSPORT, 0, 0, 2);
        ELYTRA_FIREWORK_NERF = registerInteger("elytra_firework_nerf", JABSFIXEDTRANSPORT, 1, 0, 2);
        ELYTRA_DEPLOYMENT_TICKS = registerInteger("elytra_deployment_ticks", JABSFIXEDTRANSPORT, 15, 0, Integer.MAX_VALUE);
        ELYTRA_HIT_CANCEL_TICKS = registerInteger("elytra_hit_cancel_ticks", JABSFIXEDTRANSPORT, 40, 0, Integer.MAX_VALUE);

        REQUIRE_TOTEM_USE = registerBoolean("require_totem_use", JABSFIXEDCOMBAT, false);
        RAID_REPLACE_EVOKERS_WITH_ILLUSIONERS = registerBoolean("raid_replace_evokers_with_illusioners", JABSFIXEDCOMBAT, true);
        NERF_VANILLA_SPEARS = registerBoolean("nerf_vanilla_spears", JABSFIXEDCOMBAT, true);
        SPEARS_ONLY_HORIZONTAL = registerBoolean("spears_only_horizontal", JABSFIXEDCOMBAT, true);
        STAMINA_DRAIN_SPEED = registerInteger("stamina_drain_speed", JABSFIXEDCOMBAT, 100, 0, 1000);
        EAT_DURATION_PROPORTIONAL_TO_FOOD = registerBoolean("eat_duration_proportional_to_food", JABSFIXEDCOMBAT, true);
        EAT_HIT_CANCELLING = registerBoolean("eat_hit_cancelling", JABSFIXEDCOMBAT, true);
        RESPAWN_WITH_LESS_HEALTH = registerBoolean("respawn_with_less_health", JABSFIXEDCOMBAT, true);
        PARTIAL_KEEP_INVENTORY = registerBoolean("partial_keep_inventory", JABSFIXEDCOMBAT, false);
        ITEM_DEATH_DESPAWN_TIME = registerInteger("item_death_despawn_time", JABSFIXEDCOMBAT, 30, 0, 30);
        STRONGER_MOBS = registerBoolean("stronger_mobs", JABSFIXEDCOMBAT, true);
        MOBS_LEAVE_VEHICLES_WHEN_ATTACKED = registerBoolean("mobs_leave_vehicles_when_attacked", JABSFIXEDCOMBAT, true);
        BETTER_WITHER_FIGHT = registerBoolean("better_wither_fight", JABSFIXEDCOMBAT, true);
        BETTER_DRAGON_FIGHT = registerBoolean("better_dragon_fight", JABSFIXEDCOMBAT, true);
        DRAGON_WORLD_BORDER_BEFORE_KILL = registerBoolean("dragon_world_border_before_kill", JABSFIXEDCOMBAT, true);

        PEACEFUL_MOB_GRIEFING = registerBoolean("peaceful_mob_griefing", GameRuleCategory.MOBS, true);
        NIGHTS_UNTIL_INSOMNIA = registerInteger("nights_until_insomnia", JABSFIXEDMOBSANDBLOCKS, 7, 0, Integer.MAX_VALUE);
        INSOMNIA_SLEEP_REQUIREMENT = registerBoolean("insomnia_sleep_requirement", JABSFIXEDMOBSANDBLOCKS, false);
        VILLAGERS_NEED_SLEEP = registerBoolean("villagers_need_sleep", JABSFIXEDMOBSANDBLOCKS, true);
        VILLAGERS_NEED_FOOD = registerBoolean("villagers_need_food", JABSFIXEDMOBSANDBLOCKS, true);
        VILLAGERS_NEED_SUNLIGHT = registerBoolean("villagers_need_sunlight", JABSFIXEDMOBSANDBLOCKS, true);
        VILLAGERS_NEED_FRIENDS = registerBoolean("villagers_need_friends", JABSFIXEDMOBSANDBLOCKS, true);
        VILLAGERS_NEED_SPACE = registerBoolean("villagers_need_space", JABSFIXEDMOBSANDBLOCKS, true);
        VILLAGERS_TRADE_AT_NIGHT = registerBoolean("villagers_trade_at_night", JABSFIXEDMOBSANDBLOCKS, true);
        VILLAGERS_STRONGER_DEMAND = registerBoolean("villagers_stronger_demand", JABSFIXEDMOBSANDBLOCKS, true);
        VILLAGERS_NITWITIFY_ON_ZOMBIFICATION = registerBoolean("villagers_nitwitify_on_zombification", JABSFIXEDMOBSANDBLOCKS, true);
        ONE_IRON_GOLEM_PER_MOB = registerBoolean("one_iron_golem_per_mob", JABSFIXEDMOBSANDBLOCKS, true);
        HOSTILE_SNIFFER_PLANTS = registerBoolean("hostile_sniffer_plants", JABSFIXEDMOBSANDBLOCKS, true);
    }

    private static GameRule<Boolean> registerBoolean(String name, GameRuleCategory category, boolean defaultValue) {
        return register(name, category, GameRuleType.BOOL, BoolArgumentType.bool(), Codec.BOOL, defaultValue,
                FeatureFlagSet.of(), GameRuleTypeVisitor::visitBoolean,value -> value ? 1 : 0);
    }

    private static GameRule<Integer> registerInteger(
            final String id, GameRuleCategory category, final int defaultValue, final int min, final int max) {
        return register(id, category, GameRuleType.INT, IntegerArgumentType.integer(min, max), Codec.intRange(min, max),
                defaultValue, FeatureFlagSet.of(), GameRuleTypeVisitor::visitInteger, i -> i);
    }

    private static <T> GameRule<T> register(String name, GameRuleCategory category, GameRuleType type,
                                            ArgumentType<T> argumentType, Codec<T> codec, T defaultValue, FeatureFlagSet requiredFeatures,
                                            GameRules.VisitorCaller<T> acceptor, ToIntFunction<T> commandResultSupplier) {
        return Registry.register(
                BuiltInRegistries.GAME_RULE, FixedMinecraft.id(name),
                new GameRule<>(category, type, argumentType, acceptor, codec, commandResultSupplier, defaultValue, requiredFeatures));
    }
}
