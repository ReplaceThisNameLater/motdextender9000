package com.github.amyavi.motdextender9000;

import com.github.amyavi.motdextender9000.tag.CustomTagResolvers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

@Mod(value = "motdextender9000", dist = Dist.DEDICATED_SERVER)
public final class MotdExtender9000 {
    private static final Path QUOTES_PATH = Path.of("server-splashes.txt");

    public static final Logger LOGGER = LoggerFactory.getLogger("motdextender9000");
    public static MotdExtender9000 INSTANCE;

    private final Set<String> quotes = new ObjectOpenHashSet<>(64);
    public final MiniMessage miniMessage = MiniMessage.builder()
            .strict(true)
            .editTags(tags -> tags.resolvers(
                    CustomTagResolvers.quoteTag(this.quotes),
                    CustomTagResolvers.ENV_TAG).build())
            .build();

    public MotdExtender9000(final IEventBus eventBus) {
        assert INSTANCE != null;
        INSTANCE = this;

        eventBus.addListener(this::onStarting);
    }

    // Fine, here's your event bus:
    private void onStarting(final FMLDedicatedServerSetupEvent event) {
        final Path quotesFile = FMLPaths.CONFIGDIR.get().resolve(QUOTES_PATH);
        try (final Stream<String> stream = Files.lines(quotesFile)) {
            stream.forEach(quotes::add);
        } catch (final NoSuchFileException ignored) {
        } catch (final IOException e) {
            LOGGER.warn("Failed to load quotes file:", e);
        }
    }
}
