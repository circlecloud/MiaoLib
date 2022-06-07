package pw.yumc.MiaoLib.mail;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.activation.DataContentHandler;
import javax.activation.DataContentHandlerFactory;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.net.HostAndPort;

public class XMail {

    private static boolean autoRegister = true;

    private static HashMap<String, DataContentHandler> handlers = new HashMap<>();
    private static DataContentHandlerFactory defaultDataContentHandlerFactory;
    static {
        // todo
        // activation.jar
        // CommandMap.getDefaultCommandMap中取出已通过addMailcap注册的handler合集
        // 通过合集遍历type和class打入map, 以取代手工注册
        handlers.put("text/html", new com.sun.mail.handlers.text_html());
        handlers.put("text/xml", new com.sun.mail.handlers.text_html());
        handlers.put("text/plain", new com.sun.mail.handlers.text_plain());
        handlers.put("multipart/mixed", new com.sun.mail.handlers.multipart_mixed());
        handlers.put("multipart/*", new com.sun.mail.handlers.multipart_mixed());
        handlers.put("message/rfc822", new com.sun.mail.handlers.message_rfc822());
        handlers.put("image/gif", new com.sun.mail.handlers.image_gif());
        handlers.put("image/jpeg", new com.sun.mail.handlers.image_jpeg());
        defaultDataContentHandlerFactory = type -> {
            DataContentHandler handler = handlers.get(type);
            if (handler != null) { return handler; }
            System.out.println("*************  Unknown Type: " + type + "  *************");
            return null;
        };
    }

    public static void addDataContentHandler(String type, DataContentHandler handler) {
        handlers.put(type, handler);
    }

    public static boolean isAutoRegister() {
        return autoRegister;
    }

    public static boolean registerDefaultDataContentHandlerFactory() {
        try {
            DataHandler.setDataContentHandlerFactory(defaultDataContentHandlerFactory);
            return true;
        } catch (Exception ignored) {
        }
        return false;
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
     * @param needAuth
     *            是否需要验证
     * @return 是否发送成功
     */
    public static boolean send(HostAndPort smtp, String from, String fromName, String to, String copyto, String subject, String content, String[] filename, final String username, final String password, boolean needAuth) {
        try {
            if (isAutoRegister()) {
                unregisterDefaultDataContentHandlerFactory();
                registerDefaultDataContentHandlerFactory();
            }
            Properties props = new Properties();
            props.put("mail.smtp.host", smtp.getHostText());
            props.put("mail.smtp.port", smtp.getPort());
            props.put("mail.smtp.socketFactory.port", smtp.getPort());
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", needAuth);
            Session mailSession = Session.getInstance(props, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            if (fromName == null) {
                fromName = from;
            }
            MimeMessage mimeMsg = new MimeMessage(mailSession);
            mimeMsg.setSubject(subject);

            MimeMultipart mp = new MimeMultipart();

            if (content != null) {
                try {
                    BodyPart bp = new MimeBodyPart();
                    bp.setContent("" + content, "text/html;charset=GBK");
                    mp.addBodyPart(bp);
                } catch (Exception e) {
                    System.err.println("设置邮件正文时发生错误！" + e);
                    return false;
                }
            }

            if (filename != null) {
                for (String file : filename) {
                    try {
                        BodyPart bp = new MimeBodyPart();
                        FileDataSource fileds = new FileDataSource(file);
                        bp.setDataHandler(new DataHandler(fileds));
                        bp.setFileName(fileds.getName());
                        mp.addBodyPart(bp);
                    } catch (Exception e) {
                        System.err.println("增加邮件附件：" + file + "发生错误！" + e);
                    }
                }
            }

            mimeMsg.setContent(mp);

            try {
                // 设置发信人
                try {
                    mimeMsg.setFrom(new InternetAddress(from, fromName));
                } catch (Exception e) {
                    mimeMsg.setFrom(new InternetAddress(from));
                }
            } catch (Exception e) {
                System.err.println("设置发信人发生错误！");
                e.printStackTrace();
                return false;
            }

            try {
                mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            } catch (Exception e) {
                System.err.println("设置接收人发生错误！");
                e.printStackTrace();
                return false;
            }

            if (copyto != null) {
                try {
                    // 设置抄送人
                    try {
                        mimeMsg.setFrom(new InternetAddress(from, fromName));
                    } catch (Exception e) {
                        mimeMsg.setFrom(new InternetAddress(from));
                    }
                } catch (Exception e) {
                    System.err.println("设置抄送人发生错误！");
                    e.printStackTrace();
                }
            }

            mimeMsg.setSentDate(new Date());

            mimeMsg.saveChanges();

            Transport.send(mimeMsg);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (isAutoRegister()) {
                unregisterDefaultDataContentHandlerFactory();
            }
        }
        return false;
    }

    public static void setAutoRegister(boolean autoRegister) {
        XMail.autoRegister = autoRegister;
    }

    public static Object unregisterDefaultDataContentHandlerFactory() {
        try {
            Field field = DataHandler.class.getDeclaredField("factory");
            field.setAccessible(true);
            Object object = field.get(null);
            field.set(null, null);
            return object;
        } catch (Exception ignored) {
        }
        return null;
    }
}
