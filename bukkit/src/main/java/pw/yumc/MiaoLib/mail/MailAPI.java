package pw.yumc.MiaoLib.mail;

import com.google.common.net.HostAndPort;

/**
 *
 * @since 2016年4月9日 下午5:23:09
 * @author 喵♂呜
 */
public class MailAPI {

    public static void main(String[] args) {
        send(HostAndPort.fromParts("smtp.exmail.qq.com", 465), "root@yumc.pw", "admin@yumc.pw", "Java邮件测试", "邮件测试", "root@yumc.pw", "Email2Send");
    }

    /**
     * 快速发信
     *
     * @param smtp
     *            SMTP 服务器
     * @param from
     *            发件人
     * @param to
     *            收件人
     * @param subject
     *            主题
     * @param content
     *            内容
     * @return 是否发送成功
     */
    public static boolean send(HostAndPort smtp, String from, String to, String subject, String content) {
        return send(smtp, from, to, subject, content, null, null);
    }

    /**
     * 快速发信
     *
     * @param smtp
     *            SMTP 服务器
     * @param from
     *            发件人
     * @param to
     *            收件人
     * @param subject
     *            主题
     * @param content
     *            内容
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return 是否发送成功
     */
    public static boolean send(HostAndPort smtp, String from, String to, String subject, String content, String username, String password) {
        return send(smtp, from, null, to, subject, content, username, password);
    }

    /**
     * 快速发信
     *
     * @param smtp
     *            SMTP 服务器
     * @param from
     *            发件人
     * @param fromName
     *            发件人名称
     * @param to
     *            收件人
     * @param subject
     *            主题
     * @param content
     *            内容
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return 是否发送成功
     */
    public static boolean send(HostAndPort smtp, String from, String fromName, String to, String subject, String content, String username, String password) {
        return send(smtp, from, fromName, to, null, subject, content, username, password);
    }

    /**
     * 快速发信
     *
     * @param smtp
     *            SMTP 服务器
     * @param from
     *            发件人
     * @param fromName
     *            发件人名称
     * @param to
     *            收件人
     * @param copyto
     *            抄送
     * @param subject
     *            主题
     * @param content
     *            内容
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return 是否发送成功
     */
    public static boolean send(HostAndPort smtp, String from, String fromName, String to, String copyto, String subject, String content, String username, String password) {
        return send(smtp, from, fromName, to, copyto, subject, content, null, username, password);
    }

    /**
     * 快速发信
     *
     * @param smtp
     *            SMTP 服务器
     * @param from
     *            发件人
     * @param fromName
     *            发件人名称
     * @param to
     *            收件人
     * @param copyto
     *            抄送
     * @param subject
     *            主题
     * @param content
     *            内容
     * @param filename
     *            文件名称
     * @param username
     *            用户名
     * @param password
     *            密码
     * @return 是否发送成功
     */
    public static boolean send(HostAndPort smtp, String from, String fromName, String to, String copyto, String subject, String content, String[] filename, String username, String password) {
        return XMail.send(smtp, from, fromName, to, copyto, subject, content, filename, username, password, true);
    }
}
