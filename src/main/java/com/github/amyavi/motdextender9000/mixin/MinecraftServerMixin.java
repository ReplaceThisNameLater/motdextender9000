package com.github.amyavi.motdextender9000.mixin;

import com.github.amyavi.motdextender9000.MotdExtender9000;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"AddedMixinMembersNamePattern"})
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private final MinecraftServerAudiences audience =
            MinecraftServerAudiences.of((MinecraftServer) (Object) this);

    @Unique
    private boolean failed = false;

    @WrapOperation(method = "buildServerStatus",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;" +
                    "nullToEmpty(Ljava/lang/String;)Lnet/minecraft/network/chat/Component;"))
    private Component buildServerStatus$nullToEmpty(final String stringMotd, final Operation<Component> original) {
        if (!this.failed) {
            try {
                return this.audience.asNative(MotdExtender9000.INSTANCE.miniMessage.deserialize(stringMotd));
            } catch (final Exception e) {
                MotdExtender9000.LOGGER.warn("Failed to parse server MOTD with MiniMessage (strict):", e);
                this.failed = true;
            }
        }

        return original.call(stringMotd);
    }

    // legacy/LAN queries
    @Inject(method = "getMotd", at = @At("HEAD"), cancellable = true)
    private void getMotd(final CallbackInfoReturnable<String> cir) {
        if (!this.failed) cir.setReturnValue("A Minecraft Server");
    }
}
