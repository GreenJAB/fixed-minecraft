package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRules;

public class GameRuleStatus {
    public boolean combine_items;
    public boolean mending_on_op;
    public boolean grinstone_damage;
    public boolean global_locator_bar;
    public boolean elytra_drag;
    public int elytra_fly_in_rain;
    public int elytra_firework_nerf;
    public int elytra_deployment_ticks;
    public int elytra_hit_cancel_ticks;
    public boolean use_stamina;
    public boolean use_totem;
    public boolean eat_duration;
    public boolean modified_beacon;
    public boolean modified_dragon;
    public boolean modified_wither;

    public GameRuleStatus(){
    }

    public void updateRules(GameRules rules) {
        this.combine_items = rules.get(GameRuleRegistry.COMBINE_ENCHANTED_ITEMS);
        this.mending_on_op = rules.get(GameRuleRegistry.MENDING_ON_OP_ITEMS);
        this.grinstone_damage = rules.get(GameRuleRegistry.GRINDSTONE_DAMAGES_ITEM);
        this.global_locator_bar = rules.get(GameRuleRegistry.GLOBAL_PLAYER_LOCATOR_BAR);
        this.elytra_drag = rules.get(GameRuleRegistry.ELYTRA_DRAG);
        this.elytra_fly_in_rain = rules.get(GameRuleRegistry.ELYTRA_FLY_IN_RAIN);
        this.elytra_firework_nerf = rules.get(GameRuleRegistry.ELYTRA_FIREWORK_NERF);
        this.elytra_deployment_ticks = rules.get(GameRuleRegistry.ELYTRA_DEPLOYMENT_TICKS);
        this.elytra_hit_cancel_ticks = rules.get(GameRuleRegistry.ELYTRA_HIT_CANCEL_TICKS);
        this.use_stamina = rules.get(GameRuleRegistry.STAMINA_DRAIN_SPEED)>0;
        this.use_totem = rules.get(GameRuleRegistry.REQUIRE_TOTEM_USE);
        this.eat_duration = rules.get(GameRuleRegistry.EAT_DURATION_PROPORTIONAL_TO_FOOD);
        this.modified_beacon = rules.get(GameRuleRegistry.MODIFIED_BEACON);
        this.modified_dragon = rules.get(GameRuleRegistry.MODIFIED_DRAGON_FIGHT);
        this.modified_wither = rules.get(GameRuleRegistry.MODIFIED_WITHER_FIGHT);
    }

    void toPacket(FriendlyByteBuf buf) {
        buf.writeBoolean(combine_items);
        buf.writeBoolean(mending_on_op);
        buf.writeBoolean(grinstone_damage);
        buf.writeBoolean(global_locator_bar);
        buf.writeBoolean(elytra_drag);
        buf.writeInt(elytra_fly_in_rain);
        buf.writeInt(elytra_firework_nerf);
        buf.writeInt(elytra_deployment_ticks);
        buf.writeInt(elytra_hit_cancel_ticks);
        buf.writeBoolean(use_stamina);
        buf.writeBoolean(use_totem);
        buf.writeBoolean(eat_duration);
        buf.writeBoolean(modified_beacon);
        buf.writeBoolean(modified_dragon);
        buf.writeBoolean(modified_wither);
    }

    static GameRuleStatus fromPacket(FriendlyByteBuf buf) {
        GameRuleStatus p = new GameRuleStatus();
        p.combine_items = buf.readBoolean();
        p.mending_on_op = buf.readBoolean();
        p.grinstone_damage = buf.readBoolean();
        p.global_locator_bar = buf.readBoolean();
        p.elytra_drag = buf.readBoolean();
        p.elytra_fly_in_rain = buf.readInt();
        p.elytra_firework_nerf = buf.readInt();
        p.elytra_deployment_ticks = buf.readInt();
        p.elytra_hit_cancel_ticks = buf.readInt();
        p.use_stamina = buf.readBoolean();
        p.use_totem = buf.readBoolean();
        p.eat_duration = buf.readBoolean();
        p.modified_beacon = buf.readBoolean();
        p.modified_dragon = buf.readBoolean();
        p.modified_wither = buf.readBoolean();
        return p;
    }

    public static void sendData(MinecraftServer server) {
        FixedMinecraft.gameRules.updateRules(server.getGameRules());
        GameRulePayload payload = new GameRulePayload(FixedMinecraft.gameRules);
        server.getPlayerList().getPlayers().forEach(player -> ServerPlayNetworking.send(player, payload));
    }
}
