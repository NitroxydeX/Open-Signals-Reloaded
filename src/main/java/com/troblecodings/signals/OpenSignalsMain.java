package com.troblecodings.signals;

import com.troblecodings.signals.items.ItemArmorTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.troblecodings.contentpacklib.ContentPackHandler;
import com.troblecodings.core.net.NetworkHandler;
import com.troblecodings.guilib.ecs.GuiHandler;
import com.troblecodings.signals.config.ConfigHandler;
import com.troblecodings.signals.handler.NameHandler;
import com.troblecodings.signals.handler.SignalBoxHandler;
import com.troblecodings.signals.init.OSBlocks;
import com.troblecodings.signals.init.OSItems;
import com.troblecodings.signals.init.OSModels;
import com.troblecodings.signals.init.OSSounds;
import com.troblecodings.signals.proxy.ClientProxy;
import com.troblecodings.signals.proxy.CommonProxy;
import com.troblecodings.signals.tileentitys.IChunkLoadable;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforgespi.locating.IModFile;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;

@Mod(OpenSignalsMain.MODID)
public class OpenSignalsMain {

    public static final String MODID = "opensignals";
    private static OpenSignalsMain instance;
    private final ModContainer container;
    private final IEventBus modBus;

    public static OpenSignalsMain getInstance() {
        return instance;
    }

    public OpenSignalsMain(final IEventBus eventBus, final ModContainer container) {
        instance = this;
        this.container = container;
        this.modBus = eventBus;
        ItemArmorTemplate.registerMaterials(eventBus);
        eventBus.addListener((RegisterTicketControllersEvent event) ->
                event.register(IChunkLoadable.CHUNK_TICKETS));
        eventBus.register(this);
        eventBus.register(OSBlocks.class);
        eventBus.register(OSItems.class);
        eventBus.register(OSSounds.class);
        NeoForge.EVENT_BUS.register(NameHandler.class);
        NeoForge.EVENT_BUS.register(SignalBoxHandler.class);
        container.registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
        container.registerConfig(ModConfig.Type.COMMON, ConfigHandler.GENERAL_SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            proxy = new ClientProxy();
            eventBus.register(OSModels.class);
            eventBus.addListener(ClientSetup::init);
        } else {
            proxy = new CommonProxy();
        }
    }

    public static CommonProxy proxy;
    public static GuiHandler handler = null;
    public static NetworkHandler network = null;
    public static ContentPackHandler contentPacks = null;
    private static boolean debug;
    private static Logger log = null;

    /**
     * @return the debug
     */
    public static boolean isDebug() {
        return debug;
    }

    public IModFile file;

    @SubscribeEvent
    public void preInit(final FMLConstructModEvent event) {
        debug = false;
        log = LogManager.getLogger(MODID);
        file = container.getModInfo().getOwningFile().getFile();
        contentPacks = new ContentPackHandler(MODID, "assets/" + MODID, log,
                name -> file.findResource(name), modBus);
        proxy.initModEvent(event, modBus);
    }

    private static final class ClientSetup {
        private static void init(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> OSBlocks.BLOCKS_TO_REGISTER.forEach(block ->
                    ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped())));
        }
    }

    @SubscribeEvent
    public void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> proxy.preinit(event));
    }

    public static Logger getLogger() {
        if (log == null)
            log = LogManager.getLogger(MODID);
        return log;
    }

    public static void exitMinecraftWithMessage(final String message) {
        getLogger().error(message);
        System.exit(0);
    }
}
