import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import java.util.LinkedList;
import java.util.List;
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
    private int finalSeed;

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
    void setSeed(int seed) {finalSeed+=seed;}
    int getSeed() {return finalSeed;}
    static double rand(double min, double max) {
        return Math.random() * (max - min) + min;
    }

}
class Pond extends GameObject {
    Circle aPond = new Circle();
    Label percentage = new Label();
    public Pond( double sizeX, double sizeY) {
        aPond.setRadius(Math.random()*25*Math.PI);
        aPond.setCenterX(rand()*sizeX/2);
        aPond.setCenterY(rand()*sizeY/2);
        aPond.setStroke(Color.BLUE);
        aPond.setFill(Color.BLUE);

        AnimationTimer loop = new AnimationTimer() {
            double old = -1;
            double elapsedTime = 0;
            double pondP = 0;
            @Override
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;
                elapsedTime += delta;

                percentage.setTranslateX(aPond.getCenterX());
                percentage.setTranslateY(aPond.getCenterY());
                percentage.setTextFill(Color.WHITE);
                percentage.setText(String.format(
                        "F: %.2f", pondP));
            }
        };
        loop.start();
        add(aPond);
        add(percentage);
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
class Cloud extends GameObject {
    Circle aCloud = new Circle();
    Label percentage = new Label();
    private int finalSeed;
    public Cloud(double sizeX, double sizeY, int seed, Label thisPercent) {
        percentage = thisPercent;
        aCloud.setRadius(Math.random()*25*Math.PI);
        aCloud.setCenterX(rand()*sizeX/2);
        aCloud.setCenterY(rand()*sizeY/2);
        aCloud.setStroke(Color.GRAY);
        aCloud.setFill(Color.GRAY);
        seed = 28;

        finalSeed = seed;
        setSeed(finalSeed);
        AnimationTimer loop = new AnimationTimer() {
            double old = -1;
            double elapsedTime = 0;
            double cloudP = 0;
            @Override
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;
                elapsedTime += delta;

                percentage.setTranslateX(aCloud.getCenterX());
                percentage.setTranslateY(aCloud.getCenterY());
                percentage.setTextFill(Color.WHITE);
                percentage.setText(String.format(
                        "F: %.2f", finalSeed - elapsedTime));
                aCloud.setScaleX(aCloud.getScaleX() - 0.0005);
                aCloud.setScaleY(aCloud.getScaleY() - 0.0005);
            }
        };
        loop.start();
        add(aCloud);
        add(percentage);
    }
    public double rand() {
        double toReturn =  Math.random()
                * (Math.random() - Math.random()) +
                Math.random();
        return toReturn;
    }
    static Cloud make(){
        double xPos = rand(20,30);
        double yPos = rand(10,19);
        return new Cloud(xPos,yPos, (int)rand(10,50), new Label());
    }
}
class Helipad extends Rectangle {
    Circle inner = new Circle();
    public Helipad(int sizeX, int sizeY) {
        setWidth(100);
        setHeight(100);
        setStroke(Color.YELLOW);
        inner.setStroke(Color.YELLOW);

        setX(sizeX/2.5);
        setY(sizeY - 120);
        inner.setRadius(40);
        inner.setCenterX((sizeX +125)/2.5);
        inner.setCenterY(sizeY - 70);
    }
}
class Helicopter extends GameObject {
    //Line xAxis;
    Ellipse myhelicopter;
    Rectangle head = new Rectangle();
    Label text = new Label();
    public Helicopter(double posX, double posY) {
        //xAxis = new Line(-125,0,125,0);
        super();
        myhelicopter = new Ellipse();
        myhelicopter.setFill(Color.MAGENTA);
        myhelicopter.setStroke(Color.MAGENTA);
        myhelicopter.setRadiusX(15);
        myhelicopter.setRadiusY(15);

        myhelicopter.setLayoutX(posX);
        myhelicopter.setLayoutY(posY);

        text.setFont(Font.font(20));
        text.setTranslateX(posX - 10);
        text.setTranslateY(posY - 10);
        text.setTextFill(Color.MAGENTA);

        head.setWidth(10);
        head.setHeight(25);
        head.setX(posX - 8);
        head.setY(posY - 39);

        head.setStroke(Color.MAGENTA);
        head.setFill(Color.MAGENTA);

        add(myhelicopter);
        add(text);
        add(head);
    }
    public void play(double speed, BooleanProperty left,
                     BooleanProperty right, BooleanProperty up,
                     BooleanProperty down, BooleanProperty space) {
        if (left.get()) {
            setLayoutX(getLayoutX() - speed);
            setTranslateX(getTranslateX() - speed);
            if (up.get()) {
                setTranslateY(getTranslateY() - speed);
                rotate.setAngle(rotate.getAngle()-1);
            }
            if (down.get()) {
                setTranslateY(getTranslateY() + speed);
                rotate.setAngle(rotate.getAngle()-1);
            }
        }
        else if (right.get()) {
            setLayoutX(getLayoutX() + speed);
            setTranslateX(getTranslateX() + speed);
            if (up.get()) {
                setTranslateY(getTranslateY() - speed);
                rotate.setAngle(rotate.getAngle()+1);
            }
            if (down.get()) {
                setTranslateY(getTranslateY() + speed);
                rotate.setAngle(rotate.getAngle()+1);
            }
        }
        else if (up.get())
            setLayoutY(getLayoutY() - speed);
        else if (down.get())
            setLayoutY(getLayoutY() + speed);
        else if (space.get()){
            //clouds.setSeed(1);
            System.out.println("Space");
        }
    }
}
class Background extends Pane {
    Image image = new Image("file:brownBackground.png");
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
    Helipad helipad = new Helipad((int)size.getX(), (int)size.getY());
    Helicopter helicopter = new Helicopter(helipad.getX(),helipad.getY());
    Pond pond = new Pond(size.getX(),size.getY());
    Cloud clouds = new Cloud(size.getX(),size.getY(), 0,
            new Label());
    Set<KeyCode> keysDown = new HashSet<>();
    int key(KeyCode k) { // sees if a key is down
        return keysDown.contains(k) ? 1: 0;
    }

    BooleanProperty left = new SimpleBooleanProperty();
    BooleanProperty right = new SimpleBooleanProperty();
    BooleanProperty up = new SimpleBooleanProperty();
    BooleanProperty down = new SimpleBooleanProperty();
    BooleanProperty space = new SimpleBooleanProperty();
    public Game() {
        init(root);
        //root.setStyle("-fx-background-color: black;");
        //root.getChildren().add(background.iView);

        Group gCloud = new Group();
        List<Cloud> clouds = new LinkedList<>();
        AnimationTimer loop = new AnimationTimer() {
            double old = -1;
            double elapsedTime = 0;
            double speed = 2;
            int cloudCount = 5;
            @Override
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;
                elapsedTime += delta*500;

                helicopter.text.setText(String.format(
                        "F: %.2f", 25000 - elapsedTime));

                helicopter.play(speed,left,right,up,down,space);
                //---
                /*
                if (left.get() && up.get() || right.get() && up.get()) {
                    helicopter.setTranslateY(helicopter.getTranslateY() - speed);
                    //helicopter.rotate.setAngle(helicopter.rotate.getAngle()+1);
                }

                 */
                /*
                if (left.get() && down.get() || right.get() && down.get()) {
                    helicopter.setTranslateY(helicopter.getTranslateY() + speed);
                }
                */
                while (clouds.size() < cloudCount) {
                    clouds.add(Cloud.make());
                }

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
            if(e.getCode() == KeyCode.SPACE)
                space.set(true);
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
            if(e.getCode() == KeyCode.SPACE)
                space.set(false);
        });
        loop.start();
    }
    public void init(Pane parent) {
        parent.getChildren().clear();

        parent.getChildren().addAll(helicopter,helipad,helipad.inner,
                pond, clouds,background,background.iView);
    }
}

public class GameApp extends Application {
    Game newGame;
    public void start(Stage stage) {
        newGame = new Game();
        stage.setScene(newGame.scene);
        stage.setTitle("mAkE iT rAiN");
        stage.show();
    }
    public static void main(String[] args) {launch(args);}
}
