package com.github.amyavi.motdextender9000.tag;

import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.util.RandomSource;

import java.util.Objects;
import java.util.Set;

public final class CustomTagResolvers {
    private CustomTagResolvers() {
    }

    public static final TagResolver ENV_TAG = TagResolver.resolver(
            "env", (args, context) -> {
                final String key = args.popOr("key expected").value();
                final String value = System.getenv(key);

                return Tag.selfClosingInserting(context.deserialize(Objects.requireNonNullElseGet(
                        value,
                        () -> args.popOr("missing env variable").value()
                )));
            });

    public static TagResolver quoteTag(final Set<String> quotes) {
        final RandomSource random = RandomSource.create();

        return TagResolver.resolver("quote", (args, context) -> {
            if (quotes.isEmpty())
                return Tag.selfClosingInserting(context.deserialize(args.popOr("missing quotes").value()));

            final String value = quotes.stream()
                    .skip(random.nextInt(quotes.size())).findFirst()
                    .orElseThrow(); // Never throws, we have an isEmpty() check
            return Tag.selfClosingInserting(context.deserialize(value));
        });
    }
}
