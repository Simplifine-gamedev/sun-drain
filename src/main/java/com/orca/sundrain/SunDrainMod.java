package com.orca.sundrain;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.LightType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class SunDrainMod implements ModInitializer {
    public static final String MOD_ID = "sun-drain";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Map<UUID, Float> playerSunDrain = new ConcurrentHashMap<>();
    public static final float MAX_DRAIN = 100.0f;
    public static final float DRAIN_RATE = 0.25f;
    public static final float RECOVERY_RATE = 0.15f;

    @Override
    public void onInitialize() {
        LOGGER.info("Sun Drain mod initialized!");

        // Use END_WORLD_TICK which is more stable
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getServer().getTicks() % 2 != 0) return; // Only process every other tick

            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player.isCreative() || player.isSpectator()) continue;

                UUID playerId = player.getUuid();
                float currentDrain = playerSunDrain.getOrDefault(playerId, 0.0f);

                boolean inSunlight = isPlayerInSunlight(player);

                if (inSunlight) {
                    currentDrain = Math.min(MAX_DRAIN, currentDrain + DRAIN_RATE);
                } else {
                    currentDrain = Math.max(0, currentDrain - RECOVERY_RATE);
                }

                playerSunDrain.put(playerId, currentDrain);

                // Apply effects only when crossing thresholds, not every tick
                if (currentDrain >= MAX_DRAIN) {
                    if (!player.hasStatusEffect(StatusEffects.WEAKNESS)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0, false, true));
                    }
                    if (!player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 0, false, true));
                    }
                    // Apply hunger instead of damage - much safer
                    if (!player.hasStatusEffect(StatusEffects.HUNGER)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 200, 1, false, true));
                    }
                } else if (currentDrain >= MAX_DRAIN * 0.75f) {
                    if (!player.hasStatusEffect(StatusEffects.WEAKNESS)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 0, false, true));
                    }
                }
            }
        });
    }

    private boolean isPlayerInSunlight(ServerPlayerEntity player) {
        var world = player.getServerWorld();

        if (!world.isDay()) {
            return false;
        }

        if (world.isRaining() && world.isSkyVisible(player.getBlockPos())) {
            return false;
        }

        int skyLight = world.getLightLevel(LightType.SKY, player.getBlockPos().up());
        boolean canSeeSky = world.isSkyVisible(player.getBlockPos());

        return skyLight >= 12 && canSeeSky;
    }
}
