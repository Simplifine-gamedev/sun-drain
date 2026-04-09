package com.orca.sundrain;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.world.LightType;

public class SunDrainClient implements ClientModInitializer {
    private static float clientDrain = 0.0f;
    private static final float MAX_DRAIN = 100.0f;
    private static final float DRAIN_RATE = 0.5f;
    private static final float RECOVERY_RATE = 0.3f;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            boolean inSunlight = isPlayerInSunlight(client);

            if (inSunlight) {
                clientDrain = Math.min(MAX_DRAIN, clientDrain + DRAIN_RATE);
            } else {
                clientDrain = Math.max(0, clientDrain - RECOVERY_RATE);
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            renderSunDrainBar(drawContext);
        });
    }

    private boolean isPlayerInSunlight(MinecraftClient client) {
        if (client.player == null || client.world == null) return false;

        if (!client.world.isDay()) {
            return false;
        }

        if (client.world.isRaining() && client.world.isSkyVisible(client.player.getBlockPos())) {
            return false;
        }

        int skyLight = client.world.getLightLevel(LightType.SKY, client.player.getBlockPos().up());
        boolean canSeeSky = client.world.isSkyVisible(client.player.getBlockPos());

        return skyLight >= 12 && canSeeSky;
    }

    private void renderSunDrainBar(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int barWidth = 80;
        int barHeight = 8;
        int x = screenWidth - barWidth - 10;
        int y = 10;

        context.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);
        context.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

        int fillWidth = (int) ((clientDrain / MAX_DRAIN) * barWidth);

        int color;
        if (clientDrain >= MAX_DRAIN) {
            color = 0xFFFF0000;
        } else if (clientDrain >= MAX_DRAIN * 0.75f) {
            color = 0xFFFF8800;
        } else if (clientDrain >= MAX_DRAIN * 0.5f) {
            color = 0xFFFFDD00;
        } else {
            color = 0xFFFFFF00;
        }

        if (fillWidth > 0) {
            context.fill(x, y, x + fillWidth, y + barHeight, color);
        }

        String label = "SUN";
        context.drawText(client.textRenderer, label, x - client.textRenderer.getWidth(label) - 4, y, 0xFFFFDD00, true);
    }
}
