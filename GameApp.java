import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
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
    public void update(){
        for(Node n : getChildren()){
            if(n instanceof Updatable)
                ((Updatable)n).update();
        }
    }
}
class Pond extends Circle {
    public Pond() {
        //setRadius(40);
        //setCenterX(300);
        //setCenterY(240);
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
    public Cloud() {
        //setRadius(rand());
        //setCenterX(100);
        //setCenterY(140);
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
        myhelicopter = new Ellipse();
        myhelicopter.setFill(Color.MAGENTA);
        myhelicopter.setStroke(Color.MAGENTA);
        myhelicopter.setRadiusX(15);
        myhelicopter.setRadiusY(15);
        myhelicopter.setCenterX(200);
        myhelicopter.setCenterY(300);
        text.setFont(Font.font(20));
        text.setFill(Color.WHITE);
        text.setX(200);
        text.setY(290);
        add(myhelicopter);
        add(text);
    }
}

class PondAndCloud extends GameObject{
    Pond pond = new Pond();
    Cloud clouds = new Cloud();
    public void setInfo(double sizeX, double sizeY) {
        clouds.setRadius(Math.random()*25*Math.PI);
        clouds.setCenterX(clouds.rand()*sizeX/2);
        clouds.setCenterY(clouds.rand()*sizeY/2);

        pond.setRadius(Math.random()*25*Math.PI);
        pond.setCenterX(pond.rand()*sizeX/2);
        pond.setCenterY(pond.rand()*sizeY/2);
    }
    public PondAndCloud() {
        add(pond);
        add(clouds);

    }
}
class Game {
    Pane root = new Pane();
    Point2D size = new Point2D(400,600);
    Scene scene = new Scene(root,size.getX(),size.getY());

    PondAndCloud pc = new PondAndCloud();
    Helipad helipad = new Helipad();
    Helicopter helicopter = new Helicopter();
    public Game() {
        root.setStyle("-fx-background-color: black;");

        helipad.setX(size.getX()/2.5);
        helipad.setY(size.getY() - 120);
        helipad.inner.setRadius(40);
        helipad.inner.setCenterX((size.getX() +125)/2.5);
        helipad.inner.setCenterY(size.getY() - 70);

        pc.setInfo(size.getX(),size.getY());

        root.getChildren().addAll(pc,
                helipad,helipad.inner, helicopter,
                helicopter.myhelicopter,
                helicopter.text);
        helicopter.scale.setX(20);

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

        newGame.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                keysDown.add(event.getCode());
            }
        });
        newGame.scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                keysDown.remove(event.getCode());
            }
        });

        stage.show();
    }
    public static void main(String[] args) {launch(args);}
}
