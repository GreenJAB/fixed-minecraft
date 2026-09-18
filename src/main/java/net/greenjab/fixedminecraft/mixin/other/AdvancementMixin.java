package net.greenjab.fixedminecraft.mixin.other;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Function4;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.HolderSet;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Mixin(Advancement.class)
public abstract class AdvancementMixin {

    @WrapOperation(method = "<clinit>", at=
    @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"))
    private static <B, C, T1, T2, T3, T4> StreamCodec<B, C> storeExperienceReward(
            StreamCodec<? super B, T1> codec1, Function<C, T1> getter1, StreamCodec<? super B, T2> codec2, Function<C, T2> getter2,
            StreamCodec<? super B, T3> codec3, Function<C, T3> getter3, StreamCodec<? super B, T4> codec4, Function<C, T4> getter4,
            Function4<T1, T2, T3, T4, C> constructor, Operation<StreamCodec<B, C>> original) {
        return (StreamCodec<B, C>) StreamCodec.composite(
                Identifier.STREAM_CODEC.apply(ByteBufCodecs::optional),
                Advancement::parent,
                DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional),
                Advancement::display,
                AdvancementRequirements.STREAM_CODEC,
                Advancement::requirements,
                ByteBufCodecs.BOOL,
                Advancement::sendsTelemetryEvent,
                StreamCodec.composite(
                        ByteBufCodecs.INT,
                        AdvancementRewards::experience,
                        (rewards) -> new AdvancementRewards(rewards, HolderSet.empty(), List.of(), Optional.empty())),
                Advancement::rewards,
                (parent, display, requirements, sendsTelemetryEvent, rewards) -> new Advancement(
                        parent,  display, AdvancementRewards.Builder.experience(rewards.experience())
                        .build(), Map.of(), requirements,  sendsTelemetryEvent
                )
        );
    }
}
