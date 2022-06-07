package pw.yumc.MiaoLib.commands.interfaces;

import pw.yumc.MiaoLib.commands.info.CommandInfo;

/**
 * 帮助页生成器
 *
 * @author 喵♂呜
 * @since 2016年11月16日 上午12:39:26
 */
public interface HelpGenerator {
    /**
     * @return 帮助标题
     */
    String title();

    /**
     * 格式化命令信息
     *
     * @param label 命令
     * @param ci    命令信息
     * @return 格式化后的字串
     */
    String body(String label, CommandInfo ci);

    /**
     * 格式化帮助尾部
     *
     * @param label         命令标签
     * @param HELPPAGECOUNT 帮助页数
     * @return 帮助尾部
     */
    String foot(String label, int HELPPAGECOUNT);

    /**
     * 格式化未找到
     *
     * @param label         命令标签
     * @param HELPPAGECOUNT 帮助页数
     * @return 未找到提示
     */
    String notFound(String label, int HELPPAGECOUNT);
}
