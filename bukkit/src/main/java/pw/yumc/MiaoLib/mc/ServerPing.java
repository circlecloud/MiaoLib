package pw.yumc.MiaoLib.mc;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import lombok.Data;
import lombok.SneakyThrows;
import pw.yumc.MiaoLib.annotation.NotProguard;
import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.engine.MiaoScriptEngine;

/**
 * Minecraft服务器数据获取类
 *
 * @author 喵♂呜
 * @since 2017/1/26 0026
 */
@Data
@NotProguard
public class ServerPing {
    private static byte PACKET_HANDSHAKE = 0x00, PACKET_STATUSREQUEST = 0x00, PACKET_PING = 0x01;
    private static int PROTOCOL_VERSION = 4;
    private static int STATUS_HANDSHAKE = 1;
    private static MiaoScriptEngine engine = new MiaoScriptEngine();

    static {
        try {
            engine.eval("function parse(json) {\n" +
                        "    var color = [];\n" +
                        "    color['black'] = '0';\n" +
                        "    color['dark_blue'] = '1';\n" +
                        "    color['dark_green'] = '2';\n" +
                        "    color['dark_aqua'] = '3';\n" +
                        "    color['dark_red'] = '4';\n" +
                        "    color['dark_purple'] = '5';\n" +
                        "    color['gold'] = '6';\n" +
                        "    color['gray'] = '7';\n" +
                        "    color['dark_gray'] = '8';\n" +
                        "    color['blue'] = '9';\n" +
                        "    color['green'] = 'a';\n" +
                        "    color['aqua'] = 'b';\n" +
                        "    color['red'] = 'c';\n" +
                        "    color['light_purple'] = 'd';\n" +
                        "    color['yellow'] = 'e';\n" +
                        "    color['white'] = 'f';\n" +
                        "    var obj = JSON.parse(json);\n" +
                        "    // noinspection JSUnresolvedVariable\n" +
                        "    var motd = obj.description.text;\n" +
                        "    // noinspection JSUnresolvedVariable\n" +
                        "    if (obj.description.extra) {\n" +
                        "        // noinspection JSUnresolvedVariable\n" +
                        "        obj.description.extra.forEach(function (part) {\n" +
                        "            // noinspection JSUnresolvedVariable\n" +
                        "            if (part.obfuscated) {\n" +
                        "                motd += \"§k\";\n" +
                        "            }\n" +
                        "            if (part.bold) {\n" +
                        "                motd += \"§l\";\n" +
                        "            }\n" +
                        "            // noinspection JSUnresolvedVariable\n" +
                        "            if (part.strikethrough) {\n" +
                        "                motd += \"§m\";\n" +
                        "            }\n" +
                        "            // noinspection JSUnresolvedVariable\n" +
                        "            if (part.underline) {\n" +
                        "                motd += \"§n\";\n" +
                        "            }\n" +
                        "            // noinspection JSUnresolvedVariable\n" +
                        "            if (part.italic) {\n" +
                        "                motd += \"§o\";\n" +
                        "            }\n" +
                        "            if (part.reset) {\n" +
                        "                motd += \"§r\";\n" +
                        "            }\n" +
                        "            if (part.color) {\n" +
                        "                motd += '§' + color[part.color];\n" +
                        "            }\n" +
                        "            motd += part.text;\n" +
                        "        })\n" +
                        "    }\n" +
                        "    obj.version_name = obj.version.name;\n" +
                        "    obj.version_protocol = obj.version.protocol;\n" +
                        "    obj.players_max = obj.players.max;\n" +
                        "    obj.players_online = obj.players.online;\n" +
                        "    obj.description = motd;\n" +
                        "    return obj;\n" +
                        "}");
        } catch (ScriptException e) {
            Log.w("警告! MOTD 解析脚本初始化失败!");
        }
    }

    private String address = "localhost";
    private int port = 25565;
    private int timeout = 1500;

    private String versionName = "初始化中...";
    private int versionProtocol = -1;
    private int playersOnline = -1;
    private int playersMax = -1;
    private String motd = "初始化中...";

    /**
     * Minecraft服务器数据获取类
     *
     * @param address
     *         服务器地址 默认端口25565
     */
    public ServerPing(String address) {
        this(address, 25565);
    }

    /**
     * Minecraft服务器数据获取类
     *
     * @param address
     *         服务器地址
     * @param port
     *         服务器端口
     */
    public ServerPing(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * 获取服务器数据
     *
     * @return 是否获取成功
     */
    @SneakyThrows
    public boolean fetchData() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(getAddress(), getPort()), getTimeout());

            final DataInputStream in = new DataInputStream(socket.getInputStream());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            //> Handshake
            ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
            DataOutputStream handshake = new DataOutputStream(handshake_bytes);

            handshake.writeByte(PACKET_HANDSHAKE);
            writeVarInt(handshake, PROTOCOL_VERSION);
            writeVarInt(handshake, getAddress().length());
            handshake.writeBytes(getAddress());
            handshake.writeShort(getPort());
            writeVarInt(handshake, STATUS_HANDSHAKE);

            writeVarInt(out, handshake_bytes.size());
            out.write(handshake_bytes.toByteArray());

            //> Status request
            out.writeByte(0x01); // Size of packet
            out.writeByte(PACKET_STATUSREQUEST);

            //< Status response
            readVarInt(in); // Size
            int id = readVarInt(in);
            int length = readVarInt(in);
            byte[] data = new byte[length];
            in.readFully(data);
            String json = new String(data, "UTF-8");
            ScriptObjectMirror mirror = (ScriptObjectMirror) engine.invokeFunction("parse", json);
            versionName = (String) mirror.get("version_name");
            versionProtocol = (int) mirror.get("version_protocol");
            playersMax = (int) mirror.get("players_max");
            playersOnline = (int) mirror.get("players_online");
            motd = (String) mirror.get("description");

            // Close
            handshake.close();
            handshake_bytes.close();
            out.close();
            in.close();
        } catch (IOException exception) {
            versionName = "§c获取失败!";
            motd = "§c获取失败!";
            return false;
        }
        return true;
    }

    /**
     * @author thinkofdeath
     * See: https://gist.github.com/thinkofdeath/e975ddee04e9c87faf22
     */
    public int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5)
                throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128)
                break;
        }
        return i;
    }

    /**
     * @author thinkofdeath
     * See: https://gist.github.com/thinkofdeath/e975ddee04e9c87faf22
     */
    public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }
}
