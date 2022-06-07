package pw.yumc.MiaoLib.mc;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import pw.yumc.MiaoLib.annotation.NotProguard;

/**
 * Minecraft服务器数据获取类
 *
 * @author 喵♂呜
 * @since 2017/1/26 0026
 */
@NotProguard
public class ServerInfo {
    private String address = "localhost";
    private int port = 25565;
    private int timeout = 1500;
    private int pingVersion = -1;
    private int protocolVersion = -1;
    private String gameVersion = "初始化中...";
    private String motd = "初始化中...";
    private int playersOnline = -1;
    private int maxPlayers = -1;

    /**
     * Minecraft服务器数据获取类
     *
     * @param address
     *         服务器地址 默认端口25565
     */
    public ServerInfo(String address) {
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
    public ServerInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * 获取服务器数据
     *
     * @return 是否获取成功
     */
    public boolean fetchData() {
        try (Socket socket = new Socket()) {
            OutputStream outputStream;
            DataOutputStream dataOutputStream;
            InputStream inputStream;
            InputStreamReader inputStreamReader;

            socket.setSoTimeout(this.timeout);

            socket.connect(new InetSocketAddress(this.getAddress(), this.getPort()), this.getTimeout());

            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);

            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-16BE"));

            dataOutputStream.write(new byte[]{(byte) 0xFE, (byte) 0x01});

            int packetId = inputStream.read();

            if (packetId == -1) { throw new IOException("Premature end of stream."); }

            if (packetId != 0xFF) { throw new IOException("Invalid packet ID (" + packetId + ")."); }

            int length = inputStreamReader.read();

            if (length == -1) { throw new IOException("Premature end of stream."); }

            if (length == 0) { throw new IOException("Invalid string length."); }

            char[] chars = new char[length];

            if (inputStreamReader.read(chars, 0, length) != length) { throw new IOException("Premature end of stream."); }

            String string = new String(chars);

            if (string.startsWith("§")) {
                String[] data = string.split("\0");
                this.pingVersion = Integer.parseInt(data[0].substring(1));
                this.protocolVersion = Integer.parseInt(data[1]);
                this.gameVersion = data[2];
                this.motd = data[3];
                this.playersOnline = Integer.parseInt(data[4]);
                this.maxPlayers = Integer.parseInt(data[5]);
            } else {
                String[] data = string.split("§");
                this.motd = data[0];
                this.playersOnline = Integer.parseInt(data[1]);
                this.maxPlayers = Integer.parseInt(data[2]);
            }

            dataOutputStream.close();
            outputStream.close();

            inputStreamReader.close();
            inputStream.close();

            socket.close();
        } catch (IOException exception) {
            gameVersion = "获取失败!";
            motd = "获取失败!";
            return false;
        }
        return true;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public int getPingVersion() {
        return this.pingVersion;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getGameVersion() {
        return this.gameVersion;
    }

    public String getMotd() {
        return this.motd;
    }

    public int getPlayersOnline() {
        return this.playersOnline;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

}
