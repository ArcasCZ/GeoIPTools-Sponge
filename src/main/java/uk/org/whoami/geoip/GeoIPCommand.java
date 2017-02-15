/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.whoami.geoip;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import uk.org.whoami.geoip.util.ConsoleLogger;

/**
 *
 * @author Arcas
 */
public class GeoIPCommand implements CommandExecutor{
    

    public GeoIPCommand() { }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src.hasPermission("GeoIPTools.geoupdate")) {
            if (src instanceof Player) {
                src.sendMessage(Text.of("You have started a GeoIP database update."));
            }
            ConsoleLogger.info(src.getName() + " started a GeoIP database update.");
            
            Task.Builder sched = Task.builder().async();
            sched.execute(new UpdateTask(src)).submit(GeoIPTools.getPlugin());
        }
        return CommandResult.success();

    }

    public void register() {
        CommandSpec geoCommand = CommandSpec.builder()
            .description(Text.of("GeoIPTools DB update"))
            .permission("GeoIPTools.geoupdate")
            .executor(this)
            .build();

        Sponge.getCommandManager().register(GeoIPTools.getPlugin(), geoCommand, "geoupdate");
    }
    
}
