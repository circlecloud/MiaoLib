package pw.yumc.MiaoLib.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import com.google.common.io.Files;

import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.bukkit.P;
import pw.yumc.MiaoLib.config.yaml.BukkitConstructor;
import pw.yumc.MiaoLib.config.yaml.BukkitRepresenter;

/**
 * 抽象配置文件
 *
 * @since 2016年3月12日 下午4:46:45
 * @author 喵♂呜
 */
public abstract class AbstractConfig extends YamlConfiguration {
    private static String CONTENT_NOT_BE_NULL = "内容不能为 null";

    protected static Charset UTF_8 = Charset.forName("UTF-8");

    protected static String FILE_NOT_BE_NULL = "文件不能为 NULL";
    protected static String CREATE_NEW_CONFIG = "配置: 创建新的文件 %s ...";
    protected static String newLine = "\n";

    protected static Plugin plugin = P.instance;

    protected DumperOptions yamlOptions = new DumperOptions();
    protected Representer yamlRepresenter = BukkitRepresenter.DEFAULT;
    protected Yaml yamlz = new Yaml(BukkitConstructor.DEFAULT, yamlRepresenter, yamlOptions);

    /**
     * 配置文件内容MAP
     */
    protected Map contentsMap;

    /**
     * 配置内容字符串
     */
    protected String data;

    /**
     * @return 获得配置内容
     */
    public Map getContentMap() {
        return contentsMap;
    }

    @Override
    public void load(File file) throws IOException, InvalidConfigurationException {
        Validate.notNull(file, FILE_NOT_BE_NULL);
        FileInputStream stream = new FileInputStream(file);
        load(new InputStreamReader(stream, UTF_8));
    }

    @Override
    public void load(Reader reader) throws IOException, InvalidConfigurationException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append(newLine);
            }
        }
        loadFromString(builder.toString());
    }

    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        Validate.notNull(contents, CONTENT_NOT_BE_NULL);
        try {
            contentsMap = (Map) yamlz.load(contents);
        } catch (YAMLException | ClassCastException e) {
            throw new InvalidConfigurationException(e);
        }
        String header = parseHeader(contents);
        if (header.length() > 0) {
            options().header(header);
        }
        if (contentsMap != null) {
            convertMapsToSections(contentsMap, this);
        }
    }

    @Override
    public void save(File file) throws IOException {
        Validate.notNull(file, FILE_NOT_BE_NULL);
        Files.createParentDirs(file);
        if (!file.exists()) {
            file.createNewFile();
            Log.i(CREATE_NEW_CONFIG, file.toPath());
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), UTF_8)) {
            writer.write(data);
        }
    }

    @Override
    public String saveToString() {
        yamlOptions.setIndent(options().indent());
        yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        String header = buildHeader();
        String dump = yamlz.dump(getValues(false));
        if (dump.equals(BLANK_CONFIG)) {
            dump = "";
        }
        data = header + dump;
        return data;
    }
}
