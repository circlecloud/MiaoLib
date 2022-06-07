package pw.yumc.MiaoLib.mail;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import java.util.Properties;

/**
 * 简单邮件发送器，可单发，群发。
 *
 * @author MZULE
 *
 */
public class SimpleMailSender {
    /**
     * 发送邮件的props文件
     */
    private transient Properties props = System.getProperties();

    /**
     * 邮件服务器登录验证
     */
    private transient MailAuthenticator authenticator;

    /**
     * 邮箱session
     */
    private transient Session session;

    /**
     * 初始化邮件发送器
     *
     * @param username
     *            发送邮件的用户名(地址)，并以此解析SMTP服务器地址
     * @param password
     *            发送邮件的密码
     */
    public SimpleMailSender(String username, String password) {
        // 通过邮箱地址解析出smtp服务器，对大多数邮箱都管用
        String smtpHostName = "smtp." + username.split("@")[1];
        init(smtpHostName, username, password);
    }

    /**
     * 初始化邮件发送器
     *
     * @param smtpHostName
     *            SMTP邮件服务器地址
     * @param username
     *            发送邮件的用户名(地址)
     * @param password
     *            发送邮件的密码
     */
    public SimpleMailSender(String smtpHostName, String username, String password) {
        init(username, password, smtpHostName);
    }

    /**
     * 群发邮件
     *
     * @param mail
     *            邮件对象
     * @param recipients
     *            收件人们
     * @throws AddressException
     *             地址异常
     * @throws MessagingException
     *             消息异常
     */
    public void send(SimpleMail mail, String... recipients) throws MessagingException {
        // 创建mime类型邮件
        MimeMessage message = new MimeMessage(session);
        // 设置发信人
        if (mail.getFrom() != null) {
            message.setFrom(new InternetAddress(mail.getFrom()));
        } else {
            message.setFrom(new InternetAddress(authenticator.getUsername()));
        }
        // 设置收件人们
        int num = recipients.length;
        InternetAddress[] addresses = new InternetAddress[num];
        for (int i = 0; i < num; i++) {
            addresses[i] = new InternetAddress(recipients[i]);
        }
        message.setRecipients(RecipientType.TO, addresses);
        // 设置主题
        message.setSubject(mail.getSubject());
        // 设置邮件内容
        message.setContent(mail.getContent().toString(), "text/html;charset=utf-8");
        if (mail.getSentDate() != null) {
            message.setSentDate(mail.getSentDate());
        }
        if (mail.getSender() != null) {
            message.setSender(mail.getSender());
        }
        // 发送
        Transport.send(message);
    }

    /**
     * 群发邮件
     *
     * @param subject
     *            邮件主题
     * @param content
     *            邮件内容
     * @param recipients
     *            收件人邮箱地址
     * @throws AddressException
     *             地址异常
     * @throws MessagingException
     *             消息异常
     */
    public void send(String subject, Object content, String... recipients) throws MessagingException {
        this.send(new SimpleMail(subject, content), recipients);
    }

    /**
     * 初始化
     *
     * @param username
     *            发送邮件的用户名(地址)
     * @param password
     *            密码
     * @param smtpHostName
     *            SMTP主机地址
     */
    private void init(String smtpHostName, String username, String password) {
        // 初始化props
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", smtpHostName);
        // 验证
        authenticator = new MailAuthenticator(username, password);
        // 创建session
        session = Session.getInstance(props, authenticator);
    }

}
