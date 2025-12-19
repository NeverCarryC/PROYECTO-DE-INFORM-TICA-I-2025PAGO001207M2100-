package controller;

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class HistorialController {

    @FXML
    private ImageView Squirtle3Img;

    @FXML
    private ImageView huo3Img;

    @FXML
    private ImageView miao3Img;
    
//    @FXML
//    public void initialize() {
//
//        // --- Squirtle3Img 动画 ---
//        TranslateTransition ttSquirtle = new TranslateTransition(Duration.seconds(0.3), Squirtle3Img);
//        ttSquirtle.setByY(-20);               // 向上 20 像素
//        ttSquirtle.setCycleCount(TranslateTransition.INDEFINITE);
//        ttSquirtle.setAutoReverse(true);      // 回落
//
//        RotateTransition rtSquirtle = new RotateTransition(Duration.seconds(0.3), Squirtle3Img);
//        rtSquirtle.setByAngle(-30);           // 逆时针旋转 15 度
//        rtSquirtle.setCycleCount(RotateTransition.INDEFINITE);
//        rtSquirtle.setAutoReverse(true);      // 回转原角度
//
//        ParallelTransition ptSquirtle = new ParallelTransition(ttSquirtle, rtSquirtle);
//
//        // --- huo3Img 动画 ---
//        TranslateTransition ttHuo = new TranslateTransition(Duration.seconds(0.3), huo3Img);
//        ttHuo.setByY(-20);
//        ttHuo.setCycleCount(TranslateTransition.INDEFINITE);
//        ttHuo.setAutoReverse(true);
//
//        RotateTransition rtHuo = new RotateTransition(Duration.seconds(0.3), huo3Img);
//        rtHuo.setByAngle(-30);
//        rtHuo.setCycleCount(RotateTransition.INDEFINITE);
//        rtHuo.setAutoReverse(true);
//
//        ParallelTransition ptHuo = new ParallelTransition(ttHuo, rtHuo);
//
//        // --- miao3Img 动画 ---
//        TranslateTransition ttMiao = new TranslateTransition(Duration.seconds(0.3), miao3Img);
//        ttMiao.setByY(-20);
//        ttMiao.setCycleCount(TranslateTransition.INDEFINITE);
//        ttMiao.setAutoReverse(true);
//
//        RotateTransition rtMiao = new RotateTransition(Duration.seconds(0.3), miao3Img);
//        rtMiao.setByAngle(-30);
//        rtMiao.setCycleCount(RotateTransition.INDEFINITE);
//        rtMiao.setAutoReverse(true);
//
//        ParallelTransition ptMiao = new ParallelTransition(ttMiao, rtMiao);
//
//        // --- 所有动画同时播放 ---
//        ParallelTransition all = new ParallelTransition(ptSquirtle, ptHuo, ptMiao);
//        all.play();
//    }
    @FXML
    public void initialize() {

        // --- Squirtle3Img 动画 ---
        TranslateTransition ttSquirtle = new TranslateTransition(Duration.seconds(0.3), Squirtle3Img);
        ttSquirtle.setByY(-20);
        ttSquirtle.setCycleCount(TranslateTransition.INDEFINITE);
        ttSquirtle.setAutoReverse(true);

        RotateTransition rtSquirtle = new RotateTransition(Duration.seconds(0.3), Squirtle3Img);
        rtSquirtle.setByAngle(-15);
        rtSquirtle.setCycleCount(RotateTransition.INDEFINITE);
        rtSquirtle.setAutoReverse(true);

        ParallelTransition ptSquirtle = new ParallelTransition(ttSquirtle, rtSquirtle);

        // --- huo3Img 动画 (延迟启动) ---
        TranslateTransition ttHuo = new TranslateTransition(Duration.seconds(0.3), huo3Img);
        ttHuo.setByY(-20);
        ttHuo.setCycleCount(TranslateTransition.INDEFINITE);
        ttHuo.setAutoReverse(true);
        ttHuo.setDelay(Duration.seconds(0.1)); // 延迟 0.1 秒启动

        RotateTransition rtHuo = new RotateTransition(Duration.seconds(0.3), huo3Img);
        rtHuo.setByAngle(-15);
        rtHuo.setCycleCount(RotateTransition.INDEFINITE);
        rtHuo.setAutoReverse(true);
        rtHuo.setDelay(Duration.seconds(0.1));

        ParallelTransition ptHuo = new ParallelTransition(ttHuo, rtHuo);

        // --- miao3Img 动画 (延迟更久) ---
        TranslateTransition ttMiao = new TranslateTransition(Duration.seconds(0.3), miao3Img);
        ttMiao.setByY(-20);
        ttMiao.setCycleCount(TranslateTransition.INDEFINITE);
        ttMiao.setAutoReverse(true);
        ttMiao.setDelay(Duration.seconds(0.2)); // 延迟 0.2 秒启动

        RotateTransition rtMiao = new RotateTransition(Duration.seconds(0.3), miao3Img);
        rtMiao.setByAngle(-15);
        rtMiao.setCycleCount(RotateTransition.INDEFINITE);
        rtMiao.setAutoReverse(true);
        rtMiao.setDelay(Duration.seconds(0.2));

        ParallelTransition ptMiao = new ParallelTransition(ttMiao, rtMiao);

        // --- 所有动画同时播放 ---
        ParallelTransition all = new ParallelTransition(ptSquirtle, ptHuo, ptMiao);
        all.play();
    }
}
