package cinnamon.gsl;

import cinnamon.gsl.api.GSLAPI;
import cinnamon.gsl.api.capability.AffectedCapability;
import cinnamon.gsl.api.capability.SkilledCapability;
import cinnamon.gsl.api.registry.BehaviorType;
import cinnamon.gsl.api.registry.EffectType;
import cinnamon.gsl.api.registry.Skill;
import cinnamon.gsl.api.registry.data.SerDes;
import cinnamon.gsl.client.ClientProxy;
import cinnamon.gsl.client.render.DefaultRenders;
import cinnamon.gsl.common.Registry;
import cinnamon.gsl.common.ServerProxy;
import cinnamon.gsl.common.impl.DefaultBehaviors;
import cinnamon.gsl.common.impl.DefaultEntities;
import cinnamon.gsl.common.impl.example.ExamplesImpl;
import cinnamon.gsl.common.network.PacketHandler;
import cinnamon.gsl.common.proxy.IProxy;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(GSL.ID)
public class GSL {

    public static final DeferredRegister<Skill<? extends SerDes>> SKILL_DEFERRED_REGISTER = DeferredRegister.create(Skill.getType(), GSL.ID);
    public static final DeferredRegister<EffectType<?>> EFFECT_TYPE_DEFERRED_REGISTER = DeferredRegister.create(EffectType.getType(), GSL.ID);
    public static final DeferredRegister<BehaviorType<?>> BEHAVIOR_TYPE_DEFERRED_REGISTER = DeferredRegister.create(BehaviorType.getType(), GSL.ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, GSL.ID);

    //Useful names
    public static final String ID = "genericskilllib";
    public static final String NAME = "Generic Skill Lib";
    public static final Logger LOG = LogManager.getLogger(NAME);
    private static IProxy proxy;

    public static IProxy getProxy() {
        return proxy;
    }

    public GSL() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GSLConfig.Holder.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GSLConfig.Holder.COMMON_SPEC);
        //Mod Bus
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ExamplesImpl.init();
        DefaultBehaviors.init();
        DefaultEntities.init();
        modBus.addListener(this::setup);
        modBus.addListener(this::setupClient);
        modBus.addListener(this::setupRegistry);
        modBus.addListener(this::onModConfigEvent);
        SKILL_DEFERRED_REGISTER.register(modBus);
        EFFECT_TYPE_DEFERRED_REGISTER.register(modBus);
        BEHAVIOR_TYPE_DEFERRED_REGISTER.register(modBus);
        ENTITY_TYPE_DEFERRED_REGISTER.register(modBus);
        //Forge Bus
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::setupServer);
    }

    public void setup(final FMLCommonSetupEvent event) {
        SkilledCapability.init();
        AffectedCapability.init();
        PacketHandler.init();
    }

    public void setupClient(final FMLClientSetupEvent event) {
        DefaultRenders.init();
    }

    public void setupServer(final FMLServerStartingEvent event) {

    }

    public void setupRegistry(final RegistryEvent.NewRegistry event) {
        Registry.init();;
    }

    public void onModConfigEvent(final ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == GSLConfig.Holder.CLIENT_SPEC) {
            GSLConfig.Setup.client(config);
            LOG.debug("Baked client config");
        } else if (config.getSpec() == GSLConfig.Holder.COMMON_SPEC) {
            GSLConfig.Setup.server(config);
            this.initTeamOptions();
            LOG.debug("Baked server config");
        }
    }

    public void initTeamOptions() {
        GSLAPI.defaultHumanTeam = GSLConfig.Runtime.TeamOptions.defaultHumanTeam;
        GSLAPI.defaultAnimalTeam = GSLConfig.Runtime.TeamOptions.defaultAnimalTeam;
    }
}
