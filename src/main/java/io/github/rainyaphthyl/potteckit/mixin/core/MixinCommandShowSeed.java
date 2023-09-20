package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.core.portal.PortalSearcherPointForward;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandShowSeed;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(CommandShowSeed.class)
public abstract class MixinCommandShowSeed extends CommandBase {
    @Inject(method = "execute", at = @At(value = "HEAD"), cancellable = true)
    public void onExecute(MinecraftServer server, ICommandSender sender, @Nonnull String[] args, CallbackInfo ci) {
        if (args.length == 3) {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            Vec3d posSource = new Vec3d(x, y, z);
            DimensionType dimSource = sender.getEntityWorld().provider.getDimensionType();
            PortalSearcherPointForward searcher = new PortalSearcherPointForward(server, posSource, dimSource);
            Thread thread = new Thread(searcher);
            sender.sendMessage(new TextComponentString("Start searching portal destination..."));
            thread.setName("Portal Searcher");
            thread.setDaemon(true);
            thread.start();
            if (ci.isCancellable() && !ci.isCancelled()) {
                ci.cancel();
            }
        }
    }
}
