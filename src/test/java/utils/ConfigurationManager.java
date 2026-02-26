package utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Gerenciador de configurações para acesso às propriedades de teste baseadas no
 * ambiente.
 */
public class ConfigurationManager {

    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        String env = System.getProperty("env");
        if (env == null || env.isEmpty()) {
            env = "hom"; // default para homologação
        }

        String propertiesFileName = "config-" + env + ".properties";
        try (InputStream inputStream = ConfigurationManager.class.getClassLoader()
                .getResourceAsStream(propertiesFileName)) {

            if (inputStream == null) {
                throw new RuntimeException("Arquivo de propriedades não encontrado: " + propertiesFileName);
            }
            properties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar as configurações do arquivo " + propertiesFileName, e);
        }
    }

    /**
     * Recupera o valor de uma propriedade.
     * 
     * @param key Chave da propriedade.
     * @return Valor da propriedade, ou RuntimeException se não existir.
     */
    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Propriedade não encontrada: " + key);
        }
        return value;
    }
}
