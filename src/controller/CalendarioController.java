package controller;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class CalendarioController {

    @FXML
    private ImageView Squirtle2Img;

    @FXML
    private ImageView huo2Img;

    @FXML
    private ImageView miao2Img;
    
    @FXML
    public void initialize() {

//        RotateTransition rt = new RotateTransition(Duration.seconds(2), SquirtleImg);
//        rt.setByAngle(360);                         // 旋转 360 度
//        rt.setCycleCount(RotateTransition.INDEFINITE); // 无限循环
//        rt.play();                                   // 开始动画

     
    	ScaleTransition ttSquirtle = new ScaleTransition(Duration.seconds(0.3), Squirtle2Img);
    	ttSquirtle.setFromX(1);
    	ttSquirtle.setFromY(1);
    	ttSquirtle.setToX(1.2);
    	ttSquirtle.setToY(1.2);
    	ttSquirtle.setCycleCount(ScaleTransition.INDEFINITE);
    	ttSquirtle.setAutoReverse(true);

    	
    	   
    	ScaleTransition ttHuo = new ScaleTransition(Duration.seconds(0.3), huo2Img);
    	ttHuo.setFromX(1);
    	ttHuo.setFromY(1);
    	ttHuo.setToX(1.2);
    	ttHuo.setToY(1.2);
    	ttHuo.setCycleCount(ScaleTransition.INDEFINITE);
    	ttHuo.setAutoReverse(true);
    	
    	
//        TranslateTransition ttHuo = new TranslateTransition(Duration.seconds(0.3), huo2Img);
//        ttHuo.setByY(-35);
//        ttHuo.setCycleCount(TranslateTransition.INDEFINITE);
//        ttHuo.setAutoReverse(true);

    	
    	ScaleTransition ttMiao = new ScaleTransition(Duration.seconds(0.3), miao2Img);
    	ttMiao.setFromX(1);
    	ttMiao.setFromY(1);
    	ttMiao.setToX(1.2);
    	ttMiao.setToY(1.2);
    	ttMiao.setCycleCount(ScaleTransition.INDEFINITE);
    	ttMiao.setAutoReverse(true);
    	
    	
//        TranslateTransition ttMiao = new TranslateTransition(Duration.seconds(0.3), miao2Img);
//        ttMiao.setByY(-50);
//        ttMiao.setCycleCount(TranslateTransition.INDEFINITE);
//        ttMiao.setAutoReverse(true);


        ParallelTransition parallel = new ParallelTransition(ttSquirtle, ttHuo, ttMiao);
        parallel.play();
    }

}
