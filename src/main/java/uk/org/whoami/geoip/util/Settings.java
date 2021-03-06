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
package uk.org.whoami.geoip.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import uk.org.whoami.geoip.GeoIPTools;


/**
 * @author Sebastian Köhler <whoami@whoami.org.uk>
 */
//Updating & cleanup by Fishrock123 <Fishrock123@rocketmail.com>
public class Settings {

    final String CITYDATABASEPATH = "GeoLiteCity.dat";
    final String COUNTRYDATABASEPATH = "GeoIP.dat";
    final String IPV6DATABASEBATH = "GeoIPv6.dat";
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode rootNode;
    private GeoIPTools plugin;
    private ConfigurationNode config;

    public Settings() {
        this.plugin = GeoIPTools.getPlugin();

        boolean firstRun = false;
        if (!plugin.getConfigPath().exists()) try {
            plugin.getConfigPath().createNewFile();
            firstRun = true;
        } catch (IOException ex) {
            plugin.getLogger().error("The default configuration could not be loaded or created!");
        }
                
        Path potentialFile = plugin.getConfigPath().toPath();
        
        loader = HoconConfigurationLoader.builder()
                .setPath(potentialFile)
                .build();
        
        
        
        
        try {
             if (firstRun){
                plugin.getConfigPath().createNewFile();
                config = loader.load();

                config.getNode("URL", "IPv6Database").setValue("http://geolite.maxmind.com/download/geoip/database/GeoIPv6.dat.gz");
                config.getNode("URL", "CityDatabase").setValue("http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz");
                config.getNode("URL", "CountryDatabase").setValue("http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz");
                config.getNode("Path", "IPv6Database").setValue(this.IPV6DATABASEBATH);                
                config.getNode("Path", "cityDatabase").setValue(this.CITYDATABASEPATH);
                config.getNode("Path", "countryDatabase").setValue(this.COUNTRYDATABASEPATH);
                
                config.getNode("Update", "disabled").setValue(false);
                config.getNode("Update", "lastUpdated").setValue(0);
                config.getNode("Update", "daysBetweenUpdates").setValue(10);
                
                loader.save(config);
            }
            config = loader.load();

        } catch (IOException exception) {
            plugin.getLogger().error("The default configuration could not be loaded or created!");
        }

        write();
    }

    public final void write() {
        getIPv6DatabaseURL();
        getCityDatabaseURL();
        getCountryDatabaseURL();
        getLastUpdated();
        isUpdaterDisabled();
        getDaysUntilUpdate();
        try{
            loader.save(config);
        } catch (IOException exception) {
            plugin.getLogger().error("The default configuration could not be loaded or created!");
        }
    }
    
    private void save(){
        try{
            loader.save(config);
        }catch(IOException ex){
            plugin.getLogger().error("The default configuration could not be loaded or created!");
        }
    }
    
    public boolean isUpdaterDisabled() {
        return config.getNode("Update", "disabled").getBoolean(false);
    }

    public void setLastUpdated(long lastUpdated) {
        config.getNode("Update", "lastUpdated").setValue(lastUpdated);
        save();
    }

    public long getLastUpdated() {
        return config.getNode("Update", "lastUpdated").getLong(0);
    }
    
    public long getDaysUntilUpdate() {
        return config.getNode("Update", "daysBetweenUpdates").getLong(10);
    }
    
    public boolean shouldUpdate() {
        return (new Date().getTime() - getLastUpdated() > 86400000 * getDaysUntilUpdate());
    }
    public String getIPv6DatabaseURL() {
        return config.getNode("URL", "IPv6Database").getString("http://geolite.maxmind.com/download/geoip/database/GeoIPv6.dat.gz");
    }

    public String getCityDatabaseURL() {
        return config.getNode("URL", "CityDatabase").getString("http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz");
    }

    public String getCountryDatabaseURL() {
        return config.getNode("URL", "CountryDatabase").getString("http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz");
    }

    public String getIPv6DatabasePath() {
        return (plugin.getConfigDir().toString() + File.separator + config.getNode("Path", "IPv6Database").getString(this.IPV6DATABASEBATH)).replace("\\.\\","\\");
    }

    public String getCityDatabasePath() {
        return (plugin.getConfigDir().toString() + File.separator + config.getNode("Path", "cityDatabase").getString(this.CITYDATABASEPATH)).replace("\\.\\","\\");
    }

    public String getCountryDatabasePath() {
        return (plugin.getConfigDir().toString() + File.separator + config.getNode("Path", "countryDatabase").getString(this.COUNTRYDATABASEPATH)).replace("\\.\\","\\");
    }
}
