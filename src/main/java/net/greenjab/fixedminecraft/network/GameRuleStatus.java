package net.greenjab.fixedminecraft.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.greenjab.fixedminecraft.FixedMinecraft;
import net.greenjab.fixedminecraft.registry.registries.GameRuleRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRules;

public class GameRuleStatus {
    public boolean elytra_drag;
    public int elytra_fly_in_rain;
    public int elytra_firework_nerf;
    public int elytra_deployment_ticks;
    public int elytra_hit_cancel_ticks;
    public boolean use_stamina;
    public boolean eat_duration;

    public GameRuleStatus(){
    }

    public void updateRules(GameRules rules) {
        this.elytra_drag = rules.get(GameRuleRegistry.ELYTRA_DRAG);
        this.elytra_fly_in_rain = rules.get(GameRuleRegistry.ELYTRA_FLY_IN_RAIN);
        this.elytra_firework_nerf = rules.get(GameRuleRegistry.ELYTRA_FIREWORK_NERF);
        this.elytra_deployment_ticks = rules.get(GameRuleRegistry.ELYTRA_DEPLOYMENT_TICKS);
        this.elytra_hit_cancel_ticks = rules.get(GameRuleRegistry.ELYTRA_HIT_CANCEL_TICKS);
        this.use_stamina = rules.get(GameRuleRegistry.STAMINA_DRAIN_SPEED)>0;
        this.eat_duration = rules.get(GameRuleRegistry.EAT_DURATION_PROPORTIONAL_TO_FOOD);
    }

    void toPacket(FriendlyByteBuf buf) {
        buf.writeBoolean(elytra_drag);
        buf.writeInt(elytra_fly_in_rain);
        buf.writeInt(elytra_firework_nerf);
        buf.writeInt(elytra_deployment_ticks);
        buf.writeInt(elytra_hit_cancel_ticks);
        buf.writeBoolean(use_stamina);
        buf.writeBoolean(eat_duration);
    }

    static GameRuleStatus fromPacket(FriendlyByteBuf buf) {
        GameRuleStatus p = new GameRuleStatus();
        p.elytra_drag = buf.readBoolean();
        p.elytra_fly_in_rain = buf.readInt();
        p.elytra_firework_nerf = buf.readInt();
        p.elytra_deployment_ticks = buf.readInt();
        p.elytra_hit_cancel_ticks = buf.readInt();
        p.use_stamina = buf.readBoolean();
        p.eat_duration = buf.readBoolean();
        return p;
    }

    public static void sendData(MinecraftServer server) {
        FixedMinecraft.gameRules.updateRules(server.getGameRules());
        GameRulePayload payload = new GameRulePayload(FixedMinecraft.gameRules);
        server.getPlayerList().getPlayers().forEach(player -> ServerPlayNetworking.send(player, payload));
    }
}
