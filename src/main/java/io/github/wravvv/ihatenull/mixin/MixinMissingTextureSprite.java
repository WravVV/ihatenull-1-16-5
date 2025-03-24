package io.github.wravvv.ihatenull.mixin;

import io.github.wravvv.ihatenull.Ihatenull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static io.github.wravvv.ihatenull.Ihatenull.LOGGER;

@Mixin(MissingTextureSprite.class)
public class MixinMissingTextureSprite{

    @Shadow
    private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
    @Shadow
    private static final LazyValue<NativeImage> MISSING_IMAGE_DATA = new LazyValue(() -> {
        BufferedImage nullTexture = null;
        new File("config/ihatenull").mkdirs();
        File textureFile = new File("config/ihatenull/null.png");
        try {
            if (textureFile.createNewFile()) {

                try (InputStream configStream = Ihatenull.class.getResourceAsStream("/null.png")){
                    if (configStream != null) {
                        LOGGER.info("IHateNull >> Default null.png taken from: /null.png");
                        ImageIO.write(ImageIO.read(configStream),"png",textureFile);
                    } else {
                        LOGGER.info("IHateNull >> Default null.png Resource not found");
                    }
                } catch (Exception e) {
                    LOGGER.error("IHateNull >> Error setting default");
                    LOGGER.error(e.getMessage());
                }

            }
        } catch (IOException e) {
            LOGGER.error("Error setting up config");
            LOGGER.error(e.getMessage());
        }
        try {
            nullTexture = ImageIO.read(textureFile);
        } catch (Exception e) {
            LOGGER.error("Error loading texture");
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("IHateNull >> Texture empty? " + (nullTexture==null));


        NativeImage missingTexture;
        if (nullTexture == null){
            missingTexture = new NativeImage(16, 16, false);
        } else {
            missingTexture = new NativeImage(nullTexture.getWidth(), nullTexture.getHeight(), false);
        }

        for(int i = 0; i < 16; ++i) {
            for(int j = 0; j < 16; ++j) {

                // LOGGER.info("IHateNull >> texture empty? " + (nullTexture==null));

                if (nullTexture == null){
                    if (i < 8 ^ j < 8) {
                        missingTexture.setPixelRGBA(j, i, -1);
                    } else {
                        missingTexture.setPixelRGBA(j, i, -16777216);
                    }
                } else {
                    int argb = nullTexture.getRGB(j,i);
                    missingTexture.setPixelRGBA(j, i, (argb & 0xFF00FF00) | ((argb & 0x000000FF) << 16) | ((argb & 0x00FF0000) >>> 16));
                }
            }
        }

        missingTexture.untrack();
        return missingTexture;
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
