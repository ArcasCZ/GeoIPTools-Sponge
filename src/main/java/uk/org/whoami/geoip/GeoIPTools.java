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

import com.google.inject.Inject;
import java.io.File;
import uk.org.whoami.geoip.util.ConsoleLogger;
import uk.org.whoami.geoip.util.Settings;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
/**
 * This bukkit plugin provides an API for Maxmind GeoIP database lookups.
 *
 * @author Sebastian Köhler <whoami@whoami.org.uk>
 */
//Updating & cleanup by Fishrock123 <Fishrock123@rocketmail.com>
@Plugin(id = "geoiptools", name = "GeoIPTools", version = "1.0")
public class GeoIPTools  {
	
    @Inject
    private Game game;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File configPath;
    
    @Inject
    @ConfigDir (sharedRoot = false)
    private Path configDir;

    @Inject
    private Logger logger;
    
    private Settings settings;
    private GeoIPLookup geo = null;
    
    private static GeoIPTools plugin;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        GeoIPTools.plugin = this;
    	boolean forceUpdate = false;
        try {
            registerCommands();
            settings = new Settings();
            this.geo = new GeoIPLookup(settings);

        } catch (IOException e) {
        		forceUpdate = true;
        		ConsoleLogger.info(e.getMessage());
        } finally{
          if (forceUpdate || settings.shouldUpdate()) {
            ConsoleLogger.info("Starting GeoIP database updates.");
            Task.builder().execute(new UpdateTask(game.getServer().getConsole())).submit(this);
          }
        }
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        if (geo != null) {
            geo.close();
            geo = null;
        }
    }
    
    public static GeoIPTools getPlugin(){
        return plugin;
    }

    private void registerCommands(){
        new GeoIPCommand().register();
    }

    Settings getSettings() {
        return settings;
    }
    
    public Logger getLogger(){
        return this.logger;
    }
    
    /**
     * Get a GeoIPLookup.
     * @return A GeoIPLookup class
     */
    public GeoIPLookup getGeoIPLookup() {
        return geo;
    }

    public File getConfigPath() {
        return this.configPath;
    }
    
    public Path getConfigDir(){
        return this.configDir;
    }
}
