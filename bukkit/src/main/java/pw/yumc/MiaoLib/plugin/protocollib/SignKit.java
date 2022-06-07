package pw.yumc.MiaoLib.plugin.protocollib;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import pw.yumc.MiaoLib.annotation.NotProguard;
import pw.yumc.MiaoLib.bukkit.Log;
import pw.yumc.MiaoLib.bukkit.P;
import pw.yumc.MiaoLib.bukkit.compatible.C;

/**
 * 木牌工具类
 *
 * @since 2016年7月7日 上午9:38:28
 * @author 喵♂呜
 */
public class SignKit extends ProtocolLibBase {
    /**
     * 打开木牌
     *
     * @param player
     *            玩家
     * @param lines
     *            木牌内容
     * @throws InvocationTargetException
     *             调用异常
     */
    public static void open(Player player, String[] lines) {
        FakeSign.create(player, lines).open();
    }

    /**
     * 初始化监听器
     */
    public static void init() {
        FakeSign.init();
        manager.addPacketListener(new SignUpdateListen());
    }

    /**
     * 木牌更新事件
     *
     * @since 2016年7月7日 上午9:59:07
     * @author 喵♂呜
     */
    public static class SignUpdateEvent extends Event implements Cancellable {
        private static HandlerList handlers = new HandlerList();
        private Player player;
        private String[] lines;
        private boolean cancel = false;

        public SignUpdateEvent(Player player, String[] lines) {
            this.player = player;
            this.lines = lines;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        /**
         * 木牌内容
         *
         * @return lines
         */
        public String[] getLines() {
            return lines;
        }

        /**
         * 触发玩家
         *
         * @return player
         */
        public Player getPlayer() {
            return player;
        }

        /*
         * @see org.bukkit.event.Cancellable#isCancelled()
         */
        @Override
        public boolean isCancelled() {
            return cancel;
        }

        /*
         * @see org.bukkit.event.Cancellable#setCancelled(boolean)
         */
        @Override
        public void setCancelled(boolean cancel) {
            this.cancel = cancel;
        }

    }

    /**
     * 木牌监听类
     *
     * @since 2016年7月7日 上午9:58:59
     * @author 喵♂呜
     */
    public static class SignUpdateListen extends PacketAdapter implements PacketListener {

        public SignUpdateListen() {
            super(P.instance, Client.UPDATE_SIGN);
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            Player player = event.getPlayer();
            PacketContainer packet = event.getPacket();
            List<String> lines = new ArrayList<>();
            try {
                WrappedChatComponent[] input1_8 = packet.getChatComponentArrays().read(0);
                for (WrappedChatComponent wrappedChatComponent : input1_8) {
                    lines.add(subString(wrappedChatComponent.getJson()));
                }
            } catch (FieldAccessException ex) {
                String[] input = packet.getStringArrays().getValues().get(0);
                lines.addAll(Arrays.asList(input));
            }
            SignUpdateEvent sue = new SignUpdateEvent(player, lines.toArray(new String[0]));
            Bukkit.getPluginManager().callEvent(sue);
            event.setCancelled(sue.isCancelled());
        }

        /**
         * 去除首尾
         * 
         * @param string
         *            字符串
         * @return 处理后的字符串
         */
        private String subString(String string) {
            return string.substring(1, string.length() - 1);
        }
    }

    /**
     * Created with IntelliJ IDEA
     *
     * @author 喵♂呜
     *         Created on 2017/7/5 10:20.
     */
    public abstract static class FakeSign {
        private static Class<? extends FakeSign> c = null;
        private static Constructor<? extends FakeSign> constructor;

        protected int x;
        protected int y;
        protected int z;
        protected String[] lines;
        protected Player player;

        public static void init() {
            String nms = C.getNMSVersion();
            switch (nms) {
            case "v1_7_R4":
                c = FakeSign_17.class;
                break;
            case "v1_8_R3":
                c = FakeSign_18.class;
                break;
            case "v1_9_R1":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
            default:
                c = FakeSign_110.class;
                break;
            }
            try {
                constructor = c.getConstructor(Player.class, String[].class);
            } catch (NoSuchMethodException e) {
                Log.w("创建虚拟木牌工具初始化失败 %s!", e.getMessage());
                Log.d(e);
            }
        }

        public static FakeSign create(Player player, String[] lines) {
            try {
                return constructor.newInstance(player, lines);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Log.w("创建虚拟木牌包失败 %s!", e.getMessage());
                Log.d(e);
            }
            return new FakeSign_110(player, lines);
        }

        @NotProguard
        public FakeSign(Player player, String[] lines) {
            setPlayer(player);
            setLines(lines);
        }

        /**
         * 打开木牌
         */
        public void open() {
            try {
                manager.sendServerPacket(player, getBlockChangePacket());
                manager.sendServerPacket(player, getUpdateSignPacket());
                manager.sendServerPacket(player, getOpenSignEntityPacket());
            } catch (InvocationTargetException e) {
                Log.w("木牌发包错误 %s!", e.getMessage());
                Log.d(e);
            }
        }

        // WriteLocation
        protected abstract void writeBlockLocation(PacketContainer packet);

        //Set
        protected PacketContainer getBlockChangePacket() {
            PacketContainer packet = manager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            writeBlockLocation(packet);
            return setBlockChangePacket(packet);
        }

        protected abstract PacketContainer setBlockChangePacket(PacketContainer packet);

        // Update
        protected PacketContainer getUpdateSignPacket() {
            PacketContainer packet = manager.createPacket(PacketType.Play.Server.UPDATE_SIGN);
            writeBlockLocation(packet);
            return setUpdateSignPacket(packet);
        }

        protected abstract PacketContainer setUpdateSignPacket(PacketContainer packet);

        // Edit
        protected PacketContainer getOpenSignEntityPacket() {
            PacketContainer packet = manager.createPacket(PacketType.Play.Server.OPEN_SIGN_ENTITY);
            writeBlockLocation(packet);
            return setOpenSignEntityPacket(packet);
        }

        protected abstract PacketContainer setOpenSignEntityPacket(PacketContainer packet);

        public String[] getLines() {
            return lines;
        }

        public void setLines(String[] lines) {
            this.lines = lines;
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player player) {
            this.player = player;
            Location loc = player.getLocation();
            this.x = loc.getBlockX();
            this.y = 0;
            this.z = loc.getBlockZ();
        }

        public static class FakeSign_17 extends FakeSign {
            @NotProguard
            public FakeSign_17(Player player, String[] lines) {
                super(player, lines);
            }

            @Override
            protected void writeBlockLocation(PacketContainer packet) {
                packet.getBlockPositionModifier().write(0, new BlockPosition(x, y, z));
            }

            @Override
            protected PacketContainer setBlockChangePacket(PacketContainer packet) {
                packet.getBlockData().write(0, WrappedBlockData.createData(Material.SIGN_POST));
                return packet;
            }

            @Override
            protected PacketContainer setUpdateSignPacket(PacketContainer packet) {
                packet.getStringArrays().write(0, lines);
                return packet;
            }

            @Override
            protected PacketContainer setOpenSignEntityPacket(PacketContainer packet) {
                return packet;
            }
        }

        public static class FakeSign_18 extends FakeSign {
            @NotProguard
            public FakeSign_18(Player player, String[] lines) {
                super(player, lines);
            }

            @Override
            protected void writeBlockLocation(PacketContainer packet) {
                packet.getBlockPositionModifier().write(0, new BlockPosition(x, y, z));
            }

            @Override
            protected PacketContainer setBlockChangePacket(PacketContainer packet) {
                packet.getBlockData().write(0, WrappedBlockData.createData(Material.SIGN_POST));
                return packet;
            }

            @Override
            protected PacketContainer setUpdateSignPacket(PacketContainer packet) {
                packet.getChatComponentArrays().write(0,
                        new WrappedChatComponent[] { WrappedChatComponent.fromText(lines[0]),
                                                     WrappedChatComponent.fromText(lines[1]),
                                                     WrappedChatComponent.fromText(lines[2]),
                                                     WrappedChatComponent.fromText(lines[3]) });
                return packet;
            }

            @Override
            protected PacketContainer setOpenSignEntityPacket(PacketContainer packet) {
                return packet;
            }
        }

        public static class FakeSign_110 extends FakeSign_18 {
            @NotProguard
            public FakeSign_110(Player player, String[] lines) {
                super(player, lines);
            }

            @Override
            protected PacketContainer setUpdateSignPacket(PacketContainer packet) {
                packet.getBlockPositionModifier().write(0, new BlockPosition(x, y, z));
                packet.getIntegers().write(0, 9);
                NbtCompound compound = NbtFactory.ofCompound("Sign");
                for (int i = 0; i < lines.length; i++) {
                    compound.put("Text" + (i + 1), "{\"text\":\"" + lines[i] + "\"}");
                }
                packet.getNbtModifier().write(0, compound);
                return packet;
            }

        }
    }
}
