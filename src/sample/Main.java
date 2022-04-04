package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.ServerSocket;

public class Main extends Application {

    static Stage ps;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root =loader.load();
        Controller c = loader.getController();
        ps=primaryStage;
        Stage window;
        primaryStage.setTitle("Pipe Game");
        Scene scene = new Scene(root,500,520);//???????????

        primaryStage.setResizable(false);
        c.pipeDisplayer.reDraw(500,500,c.theme);
        primaryStage.setScene(scene);
        c.pipeDisplayer.widthProperty().bind(c.border.widthProperty());
        c.pipeDisplayer.heightProperty().bind(c.border.heightProperty());
        c.pipeDisplayer.widthProperty().addListener(event -> c.pipeDisplayer.reDraw(c.border.getHeight()-50,c.border.getWidth(),c.theme));
        c.pipeDisplayer.heightProperty().addListener(event -> c.pipeDisplayer.reDraw(c.border.getHeight()-50,c.border.getWidth(),c.theme));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (c.myTimer!=null){
                    c.myTimer.cancel();
                }

            }
        });


    }
    public static void main(String[] args) {
        launch(args);
    }
}
