/*
 * Copyright 2011 Sebastian Köhler <sebkoehler@whoami.org.uk>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.whoami.geoip;

import java.io.IOException;
import java.net.MalformedURLException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import uk.org.whoami.geoip.util.Updater;

/**
 * @author Sebastian Köhler <whoami@whoami.org.uk>
 */
//Cleanup by Fishrock123 <Fishrock123@rocketmail.com>
public class UpdateTask implements Runnable {
    
    /* 
     * I can not describe how much the bukkit thread system sucks.
     * In order to safely send messages to a user from a ASyncThread I have to
     * schedule a SyncThread. They should all be shot. 
     * */
    private class SyncMessageTask implements Runnable {
        
        private final String message;

        public SyncMessageTask(String message) {
            this.message = message;
        }
        
        @Override
        public void run() {
            try {
                admin.sendMessage(Text.of(message));
            } catch (NullPointerException ex) {
            }
        } 
    }
    
    private final GeoIPTools plugin;
    private final CommandSource admin;
    
    public UpdateTask(CommandSource admin) {
        this.plugin = GeoIPTools.getPlugin();
        this.admin = admin;
    }
        
    @Override
    public void run() {
        //BukkitScheduler sched = plugin.getServer().getScheduler();
        Task.Builder sched = Task.builder();
        
        sched.execute(new SyncMessageTask("[GeoIPTools] Starting update"));
        try {
            Updater.update(plugin.getSettings());
        } catch (MalformedURLException ex) {
            sched.execute(new SyncMessageTask("[GeoIPTools] Error: " + ex.getMessage()));
            return;
        }
        sched.execute(new SyncMessageTask("[GeoIPTools] Update finished"));
        
        GeoIPLookup geo = plugin.getGeoIPLookup();
        if (geo == null) return;
        
        try {
            geo.reload();
        } catch (IOException ex) {
            sched.execute(new SyncMessageTask("[GeoIPTools] Error: " + ex.getMessage()));
            return;
        }
        sched.execute(new SyncMessageTask("[GeoIPTools] database reloaded"));
    }
}
