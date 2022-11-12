import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

interface Updatable{
    void update();
}

abstract class GameObject extends Group {
    // Any state or behavior in this class
    // should apply to all game object this.
    // For example, the helicopter can move,
    // while a pond cannot. Consequently,
    // you would not include anything regarding
    // movement in this class.
    Translate translate;
    Scale scale;
    Rotate rotate;

    public GameObject() {
        translate = new Translate();
        scale = new Scale();
        rotate = new Rotate();
        this.getTransforms().addAll(translate,scale,rotate);
    }
    public void rotate(double degree) {
        rotate.setAngle(degree);
        rotate.setPivotX(0);
        rotate.setPivotY(0);
    }
    public void scale(double scaleX, double scaleY) {
        scale.setX(scaleX);
        scale.setY(scaleY);
    }
    public void translate(double transX, double transY) {
        translate.setX(transX);
        translate.setY(transY);
    }
    void add(Node node) {this.getChildren().add(node);}

}
class Pond extends Circle {
    public Pond( double sizeX, double sizeY) {
        setRadius(Math.random()*25*Math.PI);
        setCenterX(rand()*sizeX/2);
        setCenterY(rand()*sizeY/2);
        setStroke(Color.BLUE);
        setFill(Color.BLUE);
    }
    public void update() {

    }
    public double rand() {
        double toReturn =  Math.random() *
                (Math.random() - Math.random()) + Math.random();
        //System.out.println(toReturn);
        //System.out.println(Math.random());
        return toReturn;
    }
}
class Cloud extends Circle {
    public Cloud(double sizeX, double sizeY) {
        setRadius(Math.random()*25*Math.PI);
        setCenterX(rand()*sizeX/2);
        setCenterY(rand()*sizeY/2);
        setStroke(Color.GRAY);
        setFill(Color.GRAY);
    }
    public double rand() {
        double toReturn =  Math.random()
                * (Math.random() - Math.random()) +
                Math.random();
        //System.out.println(toReturn);
        //System.out.println(Math.random());
        return toReturn;
    }
}
class Helipad extends Rectangle {
    Circle inner = new Circle();
    public Helipad() {
        setWidth(100);
        setHeight(100);
        setStroke(Color.YELLOW);
        inner.setStroke(Color.YELLOW);
    }
}
class Helicopter extends GameObject {
    //Line xAxis;
    Ellipse myhelicopter;
    Text text = new Text("100 %");
    public Helicopter() {
        //xAxis = new Line(-125,0,125,0);
        super();
        myhelicopter = new Ellipse();
        myhelicopter.setFill(Color.MAGENTA);
        myhelicopter.setStroke(Color.MAGENTA);
        myhelicopter.setRadiusX(15);
        myhelicopter.setRadiusY(15);
        text.setFont(Font.font(20));
        text.setFill(Color.WHITE);
        add(myhelicopter);
        add(text);
    }
}
class Background extends Pane {
    Image image = new Image("file:Back.jpg");
    ImageView iView = new ImageView(image);
    public void Background() {
        iView.setFitHeight(600);
        iView.setFitWidth(400);
    }
}
class Game {
    Pane root = new Pane();
    Point2D size = new Point2D(400,600);
    Scene scene = new Scene(root,size.getX(),size.getY());
    Background background = new Background();

    Helipad helipad = new Helipad();
    Helicopter helicopter = new Helicopter();
    Pond pond = new Pond(size.getX(),size.getY());
    Cloud clouds = new Cloud(size.getX(),size.getY());

    BooleanProperty left = new SimpleBooleanProperty();
    BooleanProperty right = new SimpleBooleanProperty();
    BooleanProperty up = new SimpleBooleanProperty();
    BooleanProperty down = new SimpleBooleanProperty();
    public Game() {
        init(root);
        root.setStyle("-fx-background-color: black;");

        helipad.setX(size.getX()/2.5);
        helipad.setY(size.getY() - 120);
        helipad.inner.setRadius(40);
        helipad.inner.setCenterX((size.getX() +125)/2.5);
        helipad.inner.setCenterY(size.getY() - 70);

/*
        root.getChildren().addAll(helipad,helipad.inner,
                helicopter, pond, clouds,
                helicopter.myhelicopter,
                helicopter.text);

 */
        helicopter.setLayoutX(size.getX()/2);
        helicopter.setLayoutY(size.getY()/2);
        helicopter.text.setX(size.getX()/10 - 50);
        helicopter.text.setY(size.getY()/10);

        //helicopter.scale.setX(20);

        AnimationTimer loop = new AnimationTimer() {
            double old = -1;
            double elapsedTime = 0;
            double speed = 2;
            @Override
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;

                old = nano;
                elapsedTime += delta;
                if (left.get())
                    helicopter.setLayoutX(helicopter.getLayoutX() - speed);
                else if (right.get())
                    helicopter.setLayoutX(helicopter.getLayoutX() + speed);
                else if (up.get())
                    helicopter.setLayoutY(helicopter.getLayoutY() - speed);
                else if (down.get())
                    helicopter.setLayoutY(helicopter.getLayoutY() + speed);
            }
        };
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.LEFT)
                left.set(true);
            if(e.getCode() == KeyCode.RIGHT)
                right.set(true);
            if(e.getCode() == KeyCode.UP)
                up.set(true);
            if(e.getCode() == KeyCode.DOWN)
                down.set(true);
        });
        scene.setOnKeyReleased(e -> {
            if(e.getCode() == KeyCode.LEFT)
                left.set(false);
            if(e.getCode() == KeyCode.RIGHT)
                right.set(false);
            if(e.getCode() == KeyCode.UP)
                up.set(false);
            if(e.getCode() == KeyCode.DOWN)
                down.set(false);
        });
        loop.start();
    }
    public void init(Pane parent) {
        parent.getChildren().clear();

        parent.getChildren().addAll(helicopter,helipad,helipad.inner,
                pond, clouds);
    }
}

public class GameApp extends Application {
    Game newGame;

    Set<KeyCode> keysDown = new HashSet<>();
    int key(KeyCode k) { // sees if a key is down
        return keysDown.contains(k) ? 1: 0;
    }
    public void start(Stage stage) {
        newGame = new Game();
        //newGame.scene.setFill(Color.WHITE);
        stage.setScene(newGame.scene);
        stage.setTitle("mAkE iT rAiN");

        stage.show();
    }
    public static void main(String[] args) {launch(args);}
}
