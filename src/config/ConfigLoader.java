package config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ConfigLoader {
    private int port;
    private String serverName;
    private String host;
    private final Set<String> bannedPhrases = new HashSet<>();
    private String bannedPhrasesString;

    public void loadConfig(String configFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "port": {
                        port = Integer.parseInt(value);
                        break;
                    }
                    case "name": {
                        serverName = value;
                        break;
                    }
                    case "ip": {
                        host = value;
                        break;
                    }
                    case "banned_phrases": {
                        bannedPhrasesString = value;
                        System.out.println("bannedPhrasesString: " + bannedPhrasesString);
                        String[] phrases = value.split(",\\s*");
                        bannedPhrases.addAll(Set.of(phrases));
                        break;
                    }
                }
            }

            //default values
            if (port == 0) {
                port = 8080;
            }
            if (serverName == null) {
                serverName = "DefaultServer";
            }
            if (host == null) {
                host = "localhost";
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + configFilePath);
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        }
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getServerName() {
        return serverName;
    }

    public Set<String> getBannedPhrases() {
        return bannedPhrases;
    }
}
