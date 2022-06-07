package pw.yumc.MiaoLib.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.yaml.snakeyaml.DumperOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentConfig extends AbstractConfig {
    // 新增保留注释字段
    protected static String commentPrefixSymbol = "'注释 ";
    protected static String commentSuffixSymbol = "': 注释";

    protected static String fromRegex = "( *)(#.*)";
    protected static Pattern fromPattern = Pattern.compile(fromRegex);

    protected static String toRegex = "( *)(- )*" + "(" + commentPrefixSymbol + ")" + "(#.*)" + "(" + commentSuffixSymbol + ")";
    protected static Pattern toPattern = Pattern.compile(toRegex);

    protected static Pattern countSpacePattern = Pattern.compile("( *)(- )*(.*)");

    protected static int commentSplitWidth = 90;

    private static String[] split(String string, int partLength) {
        String[] array = new String[string.length() / partLength + 1];
        for (int i = 0; i < array.length; i++) {
            int beginIndex = i * partLength;
            int endIndex = beginIndex + partLength;
            if (endIndex > string.length()) {
                endIndex = string.length();
            }
            array[i] = string.substring(beginIndex, endIndex);
        }
        return array;
    }

    @Override
    public void loadFromString(String contents) throws InvalidConfigurationException {
        String[] parts = contents.split(newLine);
        List<String> lastComments = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            Matcher matcher = fromPattern.matcher(part);
            if (matcher.find()) {
                String originComment = matcher.group(2);
                String[] splitComments = split(originComment, commentSplitWidth);
                for (int i = 0; i < splitComments.length; i++) {
                    String comment = splitComments[i];
                    if (i == 0) {
                        comment = comment.substring(1);
                    }
                    comment = COMMENT_PREFIX + comment;
                    lastComments.add(comment.replaceAll("\\.", "．").replaceAll("'", "＇").replaceAll(":", "："));
                }
            } else {
                matcher = countSpacePattern.matcher(part);
                if (matcher.find() && !lastComments.isEmpty()) {
                    for (String comment : lastComments) {
                        builder.append(matcher.group(1));
                        builder.append(this.checkNull(matcher.group(2)));
                        builder.append(commentPrefixSymbol);
                        builder.append(comment);
                        builder.append(commentSuffixSymbol);
                        builder.append(newLine);
                    }
                    lastComments.clear();
                }
                builder.append(part);
                builder.append(newLine);
            }
        }
        super.loadFromString(builder.toString());
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
        String contents = header + dump;
        StringBuilder savcontent = new StringBuilder();
        String[] parts = contents.split(newLine);
        for (String part : parts) {
            Matcher matcher = toPattern.matcher(part);
            if (matcher.find() && matcher.groupCount() == 5) {
                part = this.checkNull(matcher.group(1)) + matcher.group(4);
            }
            savcontent.append(part.replaceAll("．", ".").replaceAll("＇", "'").replaceAll("：", ":"));
            savcontent.append(newLine);
        }
        data = savcontent.toString();
        return data;
    }

    /**
     * 检查字符串
     *
     * @param string
     *            检查字符串
     * @return 返回非null字符串
     */
    private String checkNull(String string) {
        return string == null ? "" : string;
    }
}