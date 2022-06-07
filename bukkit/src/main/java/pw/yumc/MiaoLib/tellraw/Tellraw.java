package pw.yumc.MiaoLib.tellraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pw.yumc.MiaoLib.bukkit.compatible.C;
import pw.yumc.MiaoLib.bukkit.Log;

/**
 * TellRaw简易处理类
 *
 * @since 2016年8月10日 下午7:10:08
 * @author 喵♂呜
 */
public class Tellraw implements Cloneable {
    private List<MessagePart> messageParts = new ArrayList<>();
    private String cache;

    static {
        if (Bukkit.getVersion().contains("Paper") || Bukkit.getVersion().contains("Torch")) {
            if (!C.init) {
                Log.console("§c========== §4警 告 §c==========");
                Log.console("§a 当前服务器为 §6Paper §a或 §6Torch ");
                Log.console("§c 异步命令会刷报错 §b不影响使用");
                Log.console("§d 如果介意请使用原版 Spigot");
                Log.console("§e YUMC构建站: http://ci.yumc.pw/job/Spigot/");
                Log.console("§c===========================");
            }
        }
    }

    public Tellraw(String text) {
        messageParts.add(new MessagePart(text));
    }

    /**
     * 创建Tellraw
     *
     * @return {@link Tellraw}
     */
    public static Tellraw create() {
        return create("");
    }

    /**
     * 创建Tellraw
     *
     * @param text
     *            文本
     * @return {@link Tellraw}
     */
    public static Tellraw create(String text) {
        return new Tellraw(text);
    }

    /**
     * 创建Tellraw
     *
     * @param text
     *            文本
     * @param objects
     *            参数
     * @return {@link Tellraw}
     */
    public static Tellraw create(String text, Object... objects) {
        return new Tellraw(String.format(text, objects));
    }

    /**
     * 发送Tellraw公告
     */
    public void broadcast() {
        for (Player player : C.Player.getOnlinePlayers()) {
            send(player);
        }
    }

    /**
     * 命令与提示
     *
     * @param command
     *            命令
     * @param tip
     *            提示
     * @return {@link Tellraw}
     */
    public Tellraw cmd_tip(String command, String... tip) {
        return command(command).tip(tip);
    }

    /**
     * 执行命令
     *
     * @param command
     *            命令
     * @return {@link Tellraw}
     */
    public Tellraw command(String command) {
        return onClick("run_command", command);
    }

    /**
     * 打开文件
     *
     * @param path
     *            文件路径
     * @return {@link Tellraw}
     */
    public Tellraw file(String path) {
        return onClick("open_file", path);
    }

    public Tellraw insertion(String data) {
        latest().insertionData = data;
        return this;
    }

    /**
     * 悬浮物品
     *
     * @param item
     *            {@link ItemStack}
     * @return {@link Tellraw}
     */
    public Tellraw item(ItemStack item) {
        return item(ItemSerialize.$(item));
    }

    /**
     * 悬浮物品
     *
     * @param json
     *            物品Json串
     * @return {@link Tellraw}
     */
    public Tellraw item(String json) {
        return onHover("show_item", json);
    }

    /**
     * 打开URL
     *
     * @param url
     *            地址
     * @return {@link Tellraw}
     */
    public Tellraw link(String url) {
        return onClick("open_url", url);
    }

    /**
     * 打开网址
     *
     * @param url
     *            网址
     * @return {@link Tellraw}
     */
    public Tellraw openurl(String url) {
        return onClick("open_url", url);
    }

    /**
     * 发送Tellraw
     *
     * @param sender
     *            命令发送者
     */
    public void send(final CommandSender sender) {
        final String json = toJsonString();
        if (sender instanceof Player && json.getBytes().length < 32000) {
            if (C.init) {
                C.sendJson((Player) sender, json, 0);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName() + " " + json);
            }
        } else {
            sender.sendMessage(toOldMessageFormat());
        }
    }

    /**
     * 命令建议与提示
     *
     * @param command
     *            建议命令
     * @param tip
     *            提示
     * @return {@link Tellraw}
     */
    public Tellraw sug_tip(String command, String... tip) {
        return suggest(command).tip(tip);
    }

    /**
     * 补全命令
     *
     * @param command
     *            命令
     * @return {@link Tellraw}
     */
    public Tellraw suggest(String command) {
        return onClick("suggest_command", command);
    }

    /**
     * 修改当前串文本
     *
     * @param text
     *            文本
     * @return {@link Tellraw}
     */
    public Tellraw text(String text) {
        latest().text = text;
        return this;
    }

    /**
     * 结束上一串消息 开始下一串数据
     *
     * @param text
     *            新的文本
     * @return {@link Tellraw}
     */
    public Tellraw then(String text) {
        return then(new MessagePart(text));
    }

    /**
     * 悬浮物品
     *
     * @param name
     *            物品名称
     * @param item
     *            {@link ItemStack};
     * @return {@link Tellraw}
     */
    public Tellraw then(String name, ItemStack item) {
        return then(name).item(ItemSerialize.$(item));
    }

    /**
     * 结束上一串消息 开始下一串数据
     *
     * @param text
     *            新的文本
     * @param objects
     *            参数
     * @return {@link Tellraw}
     */
    public Tellraw then(String text, Object... objects) {
        return then(new MessagePart(String.format(text, objects)));
    }

    /**
     * 悬浮消息
     *
     * @param texts
     *            文本列
     * @return {@link Tellraw}
     */
    public Tellraw tip(List<String> texts) {
        if (texts.isEmpty()) { return this; }
        StringBuilder text = new StringBuilder();
        texts.forEach(t -> text.append(t).append("\n"));
        return tip(text.toString().substring(0, text.length() - 1));
    }

    /**
     * 悬浮消息
     *
     * @param text
     *            文本
     * @return {@link Tellraw}
     */
    public Tellraw tip(String text) {
        return onHover("show_text", text);
    }

    /**
     * 悬浮消息
     *
     * @param texts
     *            文本列
     * @return {@link Tellraw}
     */
    public Tellraw tip(String... texts) {
        return tip(Arrays.asList(texts));
    }

    /**
     * 转换成Json串
     *
     * @return Json串
     */
    public String toJsonString() {
        if (cache == null) {
            StringBuilder msg = new StringBuilder();
            msg.append("[\"\"");
            for (MessagePart messagePart : messageParts) {
                msg.append(",");
                messagePart.writeJson(msg);
            }
            msg.append("]");
            cache = msg.toString();
            Log.d(cache);
        }
        return cache;
    }

    public Tellraw setMessageParts(List<MessagePart> messageParts) {
        this.messageParts = new ArrayList<>(messageParts);
        return this;
    }

    @Override
    public Tellraw clone() throws CloneNotSupportedException {
        return ((Tellraw) super.clone()).setMessageParts(messageParts);
    }

    /**
     * 将此消息转换为具有有限格式的人可读字符串。
     * 此方法用于发送此消息给没有JSON格式支持客户端。
     * <p>
     * 序列化每个消息部分（每个部分都需要分别序列化）：
     * <ol>
     * <li>消息串的颜色.</li>
     * <li>消息串的样式.</li>
     * <li>消息串的文本.</li>
     * </ol>
     * 这个方法会丢失点击操作和悬浮操作 所以仅用于最后的手段
     * <p>
     * 颜色和格式可以从返回的字符串中删除 通过{@link ChatColor#stripColor(String)}.
     *
     * @return 发送给老版本客户端以及控制台。
     */
    public String toOldMessageFormat() {
        StringBuilder result = new StringBuilder();
        messageParts.forEach(part -> result.append(part.text));
        return result.toString();
    }

    /**
     * 获得最后一个操作串
     *
     * @return 最后一个操作的消息串
     */
    private MessagePart latest() {
        return messageParts.get(messageParts.size() - 1);
    }

    /**
     * 添加点击操作
     *
     * @param name
     *            点击名称
     * @param data
     *            点击操作
     * @return {@link Tellraw}
     */
    private Tellraw onClick(String name, String data) {
        MessagePart latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        return this;
    }

    /**
     * 添加显示操作
     *
     * @param name
     *            悬浮显示
     * @param data
     *            显示内容
     * @return {@link Tellraw}
     */
    private Tellraw onHover(String name, String data) {
        MessagePart latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        return this;
    }

    /**
     * 结束上一串消息 开始下一串数据
     *
     * @param part
     *            下一段内容
     * @return {@link Tellraw}
     */
    private Tellraw then(MessagePart part) {
        MessagePart last = latest();
        if (!last.hasText()) {
            last.text = part.text;
        } else {
            messageParts.add(part);
        }
        cache = null;
        return this;
    }
}
