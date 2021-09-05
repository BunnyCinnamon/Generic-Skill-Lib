package arekkuusu.gsl;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public final class GSLConfig {

    public static class Common {

        public final ForgeConfigSpec.BooleanValue defaultHumanTeam;
        public final ForgeConfigSpec.BooleanValue defaultAnimalTeam;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");
            // damageFrames
            builder.comment("Team options for group fights")
                    .push("teamOptions");
            defaultHumanTeam = builder
                    .comment("When enabled players cannot be hurt by abilities.")
                    .define("defaultHumanTeam", false);
            defaultAnimalTeam = builder
                    .comment("When enabled passive animals cannot be hurt by abilities.")
                    .define("defaultAnimalTeam", false);
            builder.pop();
            builder.pop();
        }
    }

    public static class Client {

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                    .push("client");
            builder.pop();
        }
    }

    public static final class Holder {

        public static final Common COMMON;
        public static final ForgeConfigSpec COMMON_SPEC;

        public static final Client CLIENT;
        public static final ForgeConfigSpec CLIENT_SPEC;

        static {
            final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON_SPEC = specPair.getRight();
            COMMON = specPair.getLeft();
        }

        static {
            final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
            CLIENT_SPEC = specPair.getRight();
            CLIENT = specPair.getLeft();
        }
    }

    public static final class Setup {

        public static void client(final ModConfig config) {
        }

        public static void server(final ModConfig config) {
            Runtime.TeamOptions.defaultHumanTeam = Holder.COMMON.defaultHumanTeam.get();
            Runtime.TeamOptions.defaultAnimalTeam = Holder.COMMON.defaultAnimalTeam.get();
        }
    }

    public static final class Runtime {

        public static class TeamOptions {
            public static boolean defaultHumanTeam;
            public static boolean defaultAnimalTeam;
        }

        public static class Rendering {
        }
    }
}