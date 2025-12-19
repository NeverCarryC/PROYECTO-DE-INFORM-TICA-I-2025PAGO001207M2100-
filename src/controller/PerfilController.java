package controller;


import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.animation.ParallelTransition;


public class PerfilController {

    @FXML
    private ImageView SquirtleImg;
    @FXML
    private ImageView huo1Img;

    @FXML
    private ImageView miao1Img;

    @FXML
    public void initialize() {

//        RotateTransition rt = new RotateTransition(Duration.seconds(2), SquirtleImg);
//        rt.setByAngle(360);                         // 旋转 360 度
//        rt.setCycleCount(RotateTransition.INDEFINITE); // 无限循环
//        rt.play();                                   // 开始动画

     
        TranslateTransition ttSquirtle = new TranslateTransition(Duration.seconds(0.2), SquirtleImg);
        ttSquirtle.setByY(-30);
        ttSquirtle.setCycleCount(TranslateTransition.INDEFINITE);
        ttSquirtle.setAutoReverse(true);
        ttSquirtle.setDelay(Duration.seconds(0.1));
        
        TranslateTransition ttHuo = new TranslateTransition(Duration.seconds(0.2), huo1Img);
        ttHuo.setByY(-30);
        ttHuo.setCycleCount(TranslateTransition.INDEFINITE);
        ttHuo.setAutoReverse(true);
        ttHuo.setDelay(Duration.seconds(0.3));
        
        TranslateTransition ttMiao = new TranslateTransition(Duration.seconds(0.2), miao1Img);
        ttMiao.setByY(-30);
        ttMiao.setCycleCount(TranslateTransition.INDEFINITE);
        ttMiao.setAutoReverse(true);
        ttMiao.setDelay(Duration.seconds(0.2));

        ParallelTransition parallel = new ParallelTransition(ttSquirtle, ttHuo, ttMiao);
        parallel.play();
    }
}
