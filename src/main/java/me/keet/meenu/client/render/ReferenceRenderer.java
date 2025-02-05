package me.keet.meenu.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.keet.meenu.networking.StateUpdatePayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MenuRender {
    static HashMap<UUID, BlockPos> coordinates = new HashMap<>();
    private static KeyBinding keyBinding;
    private static final String DOG_TEXTURE = "meenu:textures/dog.png";

    private static float ticks = 0;

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(StateUpdatePayload.ID, (payload, context) -> {
            coordinates.put(payload.sender(), payload.blockPos());
        });

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.examplemod.spook", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_G, // The keycode of the key
                "category.examplemod.test" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                System.out.println("key pressed");
                assert client.player != null;

                var uuid = client.player.getUuid();
                var pos = client.player.getBlockPos();

                coordinates.put(uuid, pos);

                ClientPlayNetworking.send(new StateUpdatePayload(pos, uuid));
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register((context) -> {
            for (var coordinate : coordinates.values()) {
                var matrices = context.matrixStack();

                if (matrices == null) {
                    System.out.println("fallback!");
                    return;
                }
                matrices.push();

                Vec3d cameraPos = context.camera().getPos();
                float x = (float) (coordinate.getX() - cameraPos.x);
                float y = (float) (coordinate.getY() - cameraPos.y) + 0.5f;
                float z = (float) (coordinate.getZ() - cameraPos.z);

                matrices.translate(x + 0.25f, y, z + 0.25f);

                ticks += context.tickCounter().getTickDelta(true);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ticks));

                GL11.glDisable(GL11.GL_CULL_FACE);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

                Matrix4f matrix = Objects.requireNonNull(context.matrixStack()).peek().getPositionMatrix();

                // Привязка текстуры
                RenderSystem.setShaderTexture(0, Identifier.of(DOG_TEXTURE));
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);

                // Рендеринг квадрата с текстурой
                buffer.vertex(matrix, 0, 0, 0).texture(0, 0);
                buffer.vertex(matrix, 0, 0, 0.5f).texture(0, 1);
                buffer.vertex(matrix, 0.5f, 0, 0.5f).texture(1, 1);
                buffer.vertex(matrix, 0.5f, 0, 0).texture(1, 0);

                // Отрисовка буфера
                BufferRenderer.drawWithGlobalProgram(buffer.end());

                GL11.glEnable(GL11.GL_CULL_FACE);

                matrices.pop();
            }
        });
    }
}
