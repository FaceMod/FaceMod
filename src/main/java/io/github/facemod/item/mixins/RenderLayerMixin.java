package io.github.facemod.item.mixins;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import static net.minecraft.client.render.RenderPhase.*;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {

    @Unique
    private static final Identifier GLINT01_TEXTURE = new Identifier("textures/misc/enchanted_glint_item.png");
    @Unique
    private static final Identifier GLINT02_TEXTURE = new Identifier("textures/misc/enchanted_glint_entity.png");
    @Unique
    private static final RenderLayer GLINT_01 = RenderLayer.of("glint", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, 1536, RenderLayer.MultiPhaseParameters.builder().program(GLINT_PROGRAM).texture(new RenderPhase.Texture(GLINT02_TEXTURE, true, false)).writeMaskState(COLOR_MASK).cull(DISABLE_CULLING).depthTest(EQUAL_DEPTH_TEST).transparency(GLINT_TRANSPARENCY).texturing(GLINT_TEXTURING).build(false));

    /**
     * @author IAmSpade
     * @reason Faceguy requested multiple glint support; essentially we have a value parameter for the item (reforge past 10) that decides what glint the item will get.
     */

    @Overwrite
    public static RenderLayer getGlint() {

        return GLINT_01;

    }
}
