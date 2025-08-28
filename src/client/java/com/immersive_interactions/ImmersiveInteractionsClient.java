package com.immersive_interactions;

import com.immersive_interactions.util.ModRenderLayers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import static com.immersive_interactions.datagen.ModItemTagProvider.*;

public class ImmersiveInteractionsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.player == null) return;

			if (!client.player.getMainHandStack().isIn(CAN_WAX_COPPER)) return;

			renderWaxOverlay(context, client);
		});

	}

	private void renderWaxOverlay(WorldRenderContext context, MinecraftClient client) {
		Camera camera = client.gameRenderer.getCamera();
		Vec3d camPos = camera.getPos();
		BlockPos playerPos = client.player.getBlockPos();

		MatrixStack matrices = context.matrixStack();
		VertexConsumerProvider consumers = context.consumers();
		VertexConsumer buffer = consumers.getBuffer(ModRenderLayers.WAX_OVERLAY);

		int radius = 8; // keep it reasonable
		for (BlockPos pos : BlockPos.iterateOutwards(playerPos, radius, radius, radius)) {
			BlockState state = client.world.getBlockState(pos);
			if (!isWaxed(state, pos, client.world)) continue;

			for (Direction dir : Direction.values()) {
				renderFaceQuad(matrices, buffer, pos, camPos, dir);
			}
		}
	}

	private void renderFaceQuad(MatrixStack matrices, VertexConsumer buffer,
								BlockPos pos, Vec3d camPos, Direction dir) {
		MatrixStack.Entry entry = matrices.peek();
		Matrix4f model = entry.getPositionMatrix();

		float ox = pos.getX() - (float) camPos.x;
		float oy = pos.getY() - (float) camPos.y;
		float oz = pos.getZ() - (float) camPos.z;

		Vec3d[] verts = getFaceVertices(dir);

		for (int i = 0; i < 4; i++) {
			Vec3d v = verts[i].add(ox, oy, oz);
			float u = (i == 0 || i == 3) ? 0f : 1f;
			float vTex = (i <= 1) ? 0f : 1f;

			buffer.vertex(model, (float)v.x, (float)v.y, (float)v.z)
					.color(255, 255, 255, 100) // faint, waxy look
					.texture(u, vTex);
		}
	}

	private Vec3d[] getFaceVertices(Direction dir) {
        return switch (dir) {
            case UP -> new Vec3d[]{new Vec3d(0, 1, 0), new Vec3d(1, 1, 0), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1)};
            case DOWN -> new Vec3d[]{new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(0, 0, 1)};
            case NORTH -> new Vec3d[]{new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(1, 1, 0), new Vec3d(0, 1, 0)};
            case SOUTH -> new Vec3d[]{new Vec3d(0, 0, 1), new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(0, 1, 1)};
            case WEST -> new Vec3d[]{new Vec3d(0, 0, 0), new Vec3d(0, 0, 1), new Vec3d(0, 1, 1), new Vec3d(0, 1, 0)};
            case EAST -> new Vec3d[]{new Vec3d(1, 0, 0), new Vec3d(1, 0, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, 0)};
        };
    }


	public static boolean isWaxed(BlockState state, BlockPos pos, World world) {
		return state.getBlock().getName().toString().contains("waxed");
	}
}