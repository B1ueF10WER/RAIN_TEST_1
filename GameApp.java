import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
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
    Translate translate;
    Scale scale;
    Rotate rotate;
    Point2D p,v;
    double omega, theta;
    public GameObject(Group parent, Point2D p0, Point2D v0, double theta0,double omega0) {
        translate = new Translate();
        scale = new Scale();
        rotate = new Rotate();
        this.getTransforms().addAll(translate,scale,rotate);
        parent.getChildren().add(this);

        p = p0;
        v = v0;
        omega = omega0;
        theta = theta0;
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
        translate.setX(transX); // l r
        translate.setY(transY); // u d
    }
    void add(Node node) {this.getChildren().add(node);}
    public void update(double delta) {
        p = p.add(v.multiply(delta));
        theta = (theta + omega * delta) % (Math.PI * 2);

        getTransforms().clear();
        translate(p.getX(),p.getY());
        rotate(Math.toDegrees(theta));
        this.getTransforms().addAll(translate,scale,rotate);
    }
    /*
    static Point2D vecAngle(double angle, double mag) {
        return new Point2D(Math.cos(angle), Math.sin(angle)).multiply(mag);
    }

     */

}
class Pond extends Circle {
    public Pond( double sizeX, double sizeY) {
        setRadius(Math.random()*25*Math.PI);
        setCenterX(rand()*sizeX/2);
        setCenterY(rand()*sizeY/2);
        setStroke(Color.BLUE);
        setFill(Color.BLUE);
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
    Ellipse myhelicopter;
    double thrust = 160;
    Text text = new Text("100 %");
    public Helicopter(Group parent, Point2D p) {
        //xAxis = new Line(-125,0,125,0);
        super(parent, p, Point2D.ZERO, 0,0);
        myhelicopter = new Ellipse();
        add(myhelicopter);
        myhelicopter.setFill(Color.MAGENTA);
        myhelicopter.setStroke(Color.MAGENTA);
        myhelicopter.setRadiusX(70);
        myhelicopter.setRadiusY(15);
        //getTransforms().add(new Scale(30,30));
        myhelicopter.setCenterX(200);
        myhelicopter.setCenterY(300);
        update(0,0,0);

        text.setFont(Font.font(20));
        text.setFill(Color.WHITE);
        text.setX(200);
        text.setY(290);
        add(text);
    }
    public void update(double delta, double omega, double throttle) {
        if (throttle !=0) {
            //Point2D acc = vecAngle(theta, thrust * throttle);
            //v = v.add(acc.multiply((delta)));
            v = v.add((v.multiply(delta)));
        }
        else {
            v = v.multiply(1 - 0.2 * delta);
            this.omega = omega;
            super.update(delta);
        }
    }
    public void left() {
        System.out.println("left");
    }
    public void right() {
        System.out.println("right");
    }
    public void up() {
        System.out.println("up");
    }
    public void down() {
        System.out.println("down");
    }
}
class Game {
    Pane root = new Pane();
    Point2D size = new Point2D(400,600);
    Scene scene = new Scene(root,size.getX(),size.getY());

    Helipad helipad = new Helipad();
    Helicopter helicopter;
    Group gGame = new Group();

    Pond pond = new Pond(size.getX(),size.getY());
    Cloud clouds = new Cloud(size.getX(),size.getY());
    public Game() {
        root.setStyle("-fx-background-color: black;");
        helicopter = new Helicopter(gGame, size.multiply((0.5)));

        helipad.setX(size.getX()/2.5);
        helipad.setY(size.getY() - 120);
        helipad.inner.setRadius(40);
        helipad.inner.setCenterX((size.getX() +125)/2.5);
        helipad.inner.setCenterY(size.getY() - 70);


        root.getChildren().addAll(gGame,helipad,helipad.inner,
                helicopter, pond, clouds,
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
                switch(event.getCode()) {
                    case LEFT: newGame.helicopter.left(); break;
                    case RIGHT: newGame.helicopter.right(); break;
                    case UP: newGame.helicopter.up(); break;
                    case DOWN: newGame.helicopter.down(); break;
                    default:
                }
            }
        });
        newGame.scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                keysDown.remove(event.getCode());
            }});

        stage.show();
    }
    public static void main(String[] args) {launch(args);}
}
