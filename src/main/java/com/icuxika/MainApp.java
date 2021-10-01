package com.icuxika;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;
import static com.icuxika.InfoKt.title;
import static com.icuxika.InfoKt.version;

public class MainApp extends GameApplication {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 游戏中对象，桶和雨滴
     */
    public enum Type {
        DROPLET, BUCKET
    }

    @Override
    protected void initSettings(GameSettings settings) {
        // 初始化游戏窗口设置
        settings.setTitle(title());
        settings.setVersion(version());
        settings.setWidth(800);
        settings.setHeight(600);
        // 显示FPS、内存、CPU使用
        settings.setProfilingEnabled(true);
    }

    @Override
    protected void initGame() {
        // 初始化桶
        spawnBucket();

        // 创建一个定时器，每隔一秒降下雨滴
        run(() -> {
            spawnDroplet();
            return null;
        }, Duration.seconds(1));

        // 加载背景音乐
        loopBGM("bgm.mp3");
    }

    @Override
    protected void initPhysics() {
        // 当检测到桶和雨滴有冲突时，移除雨滴并播放音效
        onCollisionBegin(Type.BUCKET, Type.DROPLET, (bucket, droplet) -> {
            droplet.removeFromWorld();
            play("drop.wav");
            return null;
        });
    }

    /**
     * @param tpf 1.0 / fps
     */
    @Override
    protected void onUpdate(double tpf) {
        // 每一帧使雨滴降落一定高度
        getGameWorld().getEntitiesByType(Type.DROPLET).forEach(droplet -> droplet.translateY(150 * tpf));
    }

    private void spawnBucket() {
        // 创建桶并可以检测碰撞
        Entity bucket = entityBuilder()
                .type(Type.BUCKET)
                .at(getAppWidth() / 2, getAppHeight() - 200)
                .viewWithBBox("bucket.png")
                .collidable()
                .buildAndAttach();

        // 将桶的横轴位置与鼠标相绑定
        bucket.xProperty().bind(getInput().mouseXWorldProperty());
    }

    private void spawnDroplet() {
        // 创建雨滴
        entityBuilder()
                .type(Type.DROPLET)
                .at(FXGLMath.random(0, getAppWidth() - 64), 0)
                .viewWithBBox("droplet.png")
                .collidable()
                .buildAndAttach();
    }

}
