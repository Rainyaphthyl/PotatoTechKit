package io.github.rainyaphthyl.potteckit.mixin.core;

import io.github.rainyaphthyl.potteckit.core.portal.PortalSearcher;
import io.github.rainyaphthyl.potteckit.core.portal.PortalSearcherPointForward;
import io.github.rainyaphthyl.potteckit.core.portal.PortalSearcherRangeForward;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandShowSeed;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
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
        // /seed 26.0 117.0 31.0 27.0 123.0 34.0
        // /seed 223.0 91.0 255.0 227.0 97.0 256.0
        PortalSearcher searcher = null;
        if (args.length == 3) {
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);
            Vec3d posSource = new Vec3d(x, y, z);
            DimensionType dimSource = sender.getEntityWorld().provider.getDimensionType();
            searcher = new PortalSearcherPointForward(server, dimSource, posSource);
        } else if (args.length == 6) {
            double x1 = Double.parseDouble(args[0]);
            double y1 = Double.parseDouble(args[1]);
            double z1 = Double.parseDouble(args[2]);
            double x2 = Double.parseDouble(args[3]);
            double y2 = Double.parseDouble(args[4]);
            double z2 = Double.parseDouble(args[5]);
            DimensionType dimSource = sender.getEntityWorld().provider.getDimensionType();
            AxisAlignedBB boxSource = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
            searcher = new PortalSearcherRangeForward(server, dimSource, boxSource);
        }
        if (searcher != null) {
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
