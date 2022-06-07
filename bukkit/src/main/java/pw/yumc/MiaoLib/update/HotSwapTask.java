package pw.yumc.MiaoLib.update;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.script.ScriptException;

import pw.yumc.MiaoLib.bungee.Log;
import pw.yumc.MiaoLib.engine.MiaoScriptEngine;

/**
 * Created with IntelliJ IDEA
 * 热更新任务
 *
 * @author 喵♂呜
 * Created on 2017/7/31 11:09.
 */
public class HotSwapTask {
    private MiaoScriptEngine engine;
    private File temp = new File(System.getProperty("java.io.tmpdir"), "hotswap.js");

    public HotSwapTask() {
        this.engine = new MiaoScriptEngine();
        engine.put("$", this);
        init();
    }

    private void init() {
        try {
            Files.copy(new URL("http://api.yumc.pw/script/hotswap.js").openStream(), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            engine.eval(new FileReader(temp));
            temp.delete();
        } catch (IOException | ScriptException e) {
            Log.d("热更新脚本加载失败!", e);
        }
    }
}