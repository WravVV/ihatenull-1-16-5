package io.github.wravvv.ihatenull;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("ihatenull")
public class Ihatenull {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static BufferedImage nullTexture = null;

    public Ihatenull() {
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client

        new File("config/Ihatenull").mkdirs();
        File textureFile = new File("config/Ihatenull/null.png");
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
            nullTexture = ImageIO.read(Files.newInputStream(Paths.get("config/Ihatenull/null.png")));
        } catch (Exception e) {
            LOGGER.error("Error loading texture");
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("IHateNull >> texture empty? " + (nullTexture==null));

        if (nullTexture!=null){
            for(int lvt_3_1_ = 0; lvt_3_1_ < 16; ++lvt_3_1_) {
                for(int lvt_4_1_ = 0; lvt_4_1_ < 16; ++lvt_4_1_) {
                    int argb = Ihatenull.nullTexture.getRGB(lvt_4_1_,lvt_3_1_);
                    LOGGER.info(String.format("argb 0x%08X", argb));
                }
            }
        }
    }
}
