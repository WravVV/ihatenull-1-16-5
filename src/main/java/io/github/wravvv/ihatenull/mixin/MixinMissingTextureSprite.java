package io.github.wravvv.ihatenull.mixin;

import io.github.wravvv.ihatenull.Ihatenull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(MissingTextureSprite.class)
public class MixinMissingTextureSprite{

    @Shadow
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
    @Shadow
    private static final LazyValue<NativeImage> MISSING_IMAGE_DATA = new LazyValue(() -> {
        NativeImage lvt_0_1_;
        if (Ihatenull.nullTexture == null){
            lvt_0_1_ = new NativeImage(16, 16, false);
        } else {
            lvt_0_1_ = new NativeImage(Ihatenull.nullTexture.getWidth(), Ihatenull.nullTexture.getHeight(), false);
        }

        for(int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_) {
            for(int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_) {

                Ihatenull.LOGGER.info("IHateNull >> texture empty? " + (Ihatenull.nullTexture==null));

                if (Ihatenull.nullTexture == null){
                    if (lvt_3_1_ < 8 ^ lvt_4_1_ < 8) {
                        lvt_0_1_.setPixelRGBA(lvt_4_1_, lvt_3_1_, -1);
                    } else {
                        lvt_0_1_.setPixelRGBA(lvt_4_1_, lvt_3_1_, -16777216);
                    }
                } else {
                    int argb = Ihatenull.nullTexture.getRGB(lvt_4_1_,lvt_3_1_);
                    lvt_0_1_.setPixelRGBA(lvt_4_1_, lvt_3_1_, (argb & 0xFF00FF00) | ((argb & 0x000000FF) << 16) | ((argb & 0x00FF0000) >>> 16));
                }
            }
        }

        lvt_0_1_.untrack();
        return lvt_0_1_;
    });

    @Inject(at=@At(value="HEAD"), method= "getTexture()Lnet/minecraft/client/renderer/texture/DynamicTexture;", cancellable = true)
    private static void getTexture(CallbackInfoReturnable<DynamicTexture> cir){
        cir.cancel();

        if (missingTexture == null) {
            missingTexture = new DynamicTexture(MISSING_IMAGE_DATA.get());
            Minecraft.getInstance().getTextureManager().register(MISSING_TEXTURE_LOCATION, missingTexture);
        }

        cir.setReturnValue(missingTexture);
    }

    @Shadow
    @Nullable
    private static DynamicTexture missingTexture;

}
