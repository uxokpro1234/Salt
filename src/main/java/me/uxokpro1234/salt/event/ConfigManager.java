package me.uxokpro1234.salt.event;

import me.uxokpro1234.salt.Salt;
import me.uxokpro1234.salt.module.Module;
import me.uxokpro1234.salt.module.setting.Setting;
import com.google.gson.*;

import java.io.*;

public class ConfigManager {

    private final File configDir;

    public ConfigManager() {
        configDir = new File("examplemod/configs");
        if (!configDir.exists()) configDir.mkdirs();
    }

    public void save(String name) {
        try {
            File file = new File(configDir, name + ".json");
            JsonObject root = new JsonObject();

            for (Module module : Salt.INSTANCE.moduleManager.getModules()) {
                JsonObject modObj = new JsonObject();

                modObj.addProperty("enabled", module.isEnabled());
                modObj.addProperty("bind", module.getBind().getKey());

                JsonObject settingsObj = new JsonObject();
                for (Setting<?> setting : module.getSettings()) {
                    settingsObj.add(setting.getName(), serializeSetting(setting));
                }

                modObj.add("settings", settingsObj);
                root.add(module.getName(), modObj);
            }

            writeFile(file, new GsonBuilder().setPrettyPrinting().create().toJson(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String name) {
        try {
            File file = new File(configDir, name + ".json");
            if (!file.exists()) return;

            JsonObject root = new JsonParser().parse(new FileReader(file)).getAsJsonObject();

            for (Module module : Salt.INSTANCE.moduleManager.getModules()) {
                if (!root.has(module.getName())) continue;

                JsonObject modObj = root.getAsJsonObject(module.getName());

                module.setEnabled(modObj.get("enabled").getAsBoolean());
                module.getBind().setKey(modObj.get("bind").getAsInt());

                JsonObject settingsObj = modObj.getAsJsonObject("settings");
                for (Setting<?> setting : module.getSettings()) {
                    if (!settingsObj.has(setting.getName())) continue;
                    deserializeSetting(setting, settingsObj.get(setting.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonElement serializeSetting(Setting<?> setting) {
        if (setting.isEnum()) {
            return new JsonPrimitive(((Enum<?>) setting.getValue()).name());
        }
        return new JsonPrimitive(setting.getValue().toString());
    }

    @SuppressWarnings("unchecked")
    private void deserializeSetting(Setting<?> setting, JsonElement element) {
        try {
            if (setting.isBoolean()) {
                ((Setting<Boolean>) setting).setValue(element.getAsBoolean());
            } else if (setting.getValue() instanceof Integer) {
                ((Setting<Integer>) setting).setValue(element.getAsInt());
            } else if (setting.getValue() instanceof Float) {
                ((Setting<Float>) setting).setValue(element.getAsFloat());
            } else if (setting.getValue() instanceof Double) {
                ((Setting<Double>) setting).setValue(element.getAsDouble());
            } else if (setting.isEnum()) {
                setting.setEnumValue(element.getAsString());
            }
        } catch (Exception ignored) {}
    }

    private void writeFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}