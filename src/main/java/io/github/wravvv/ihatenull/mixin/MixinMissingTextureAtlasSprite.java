package io.github.wravvv.ihatenull.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.wravvv.ihatenull.Ihatenull;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static io.github.wravvv.ihatenull.Ihatenull.LOGGER;

@Mixin(MissingTextureAtlasSprite.class)
public class MixinMissingTextureAtlasSprite {

    @Inject(at=@At(value="HEAD"),method= "generateMissingImage(II)Lcom/mojang/blaze3d/platform/NativeImage;",cancellable = true)
    private static void generateMissingImage(int p_249811_, int p_249362_, CallbackInfoReturnable<NativeImage> cir){
        cir.cancel();

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
            LOGGER.error("IHateNull >> Error setting up config");
            LOGGER.error(e.getMessage());
        }
        try {
            nullTexture = ImageIO.read(textureFile);
        } catch (Exception e) {
            LOGGER.error("IHateNull >> Error loading texture");
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("IHateNull >> Texture empty? " + (nullTexture==null));

        NativeImage missingTexture;
        if (nullTexture == null){
            missingTexture = new NativeImage(p_249811_, p_249362_, false);
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

        cir.setReturnValue(missingTexture);
    }
}
