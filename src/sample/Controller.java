package sample;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable {

    final private JFileChooser saveFileChooser = new JFileChooser();



    public GridPane border;
    public Label timeLabel;
    public Menu MusicMenu,FileMenu,ThemeMenu;
    public MenuItem SolveMenu;
    public MenuItem SaveMenu;
    AudioClip click;
    int theme = 3,clicks = 0,secondsPassed=0,r=0,c=0;
    MediaPlayer a;
    Timer myTimer;
    char[][] pipeData = null;
    boolean firstGame = true;
    TimelineBuilder builder = null;
    Timeline tl;
    ImageView imageView;

    @FXML
    PipeDisplayer pipeDisplayer;
    Socket socket ;
    PrintWriter outToServer;
    DataOutputStream outToServer2;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        timeLabel.setText("  Seconds Passed: "+secondsPassed+"   Clicks: "+clicks);
        click = new AudioClip(this.getClass().getResource("click1.mp3").toString());

        //prepare background music
        URL resource = getClass().getResource("music2.mp3");
        a =new MediaPlayer(new Media(resource.toString()));
        a.setOnEndOfMedia(new Runnable() {
            public void run() {
                a.seek(Duration.ZERO);
                a.play();
            }
        });
        a.setVolume(0.5);




        //prepare dancing animation
        Image img0 = new Image(getClass().getResourceAsStream("pipe0.png"));
        Image img1 = new Image(getClass().getResourceAsStream("pipe1.png"));
        Image img2 = new Image(getClass().getResourceAsStream("pipe2.png"));
        tl= builder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(
                        new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>(){
                            @Override
                            public void handle(ActionEvent t) {
                                pipeDisplayer.getGraphicContext().drawImage(img1,40,120,400,400);
                            }
                        }),
                        new KeyFrame(Duration.millis(200), new EventHandler<ActionEvent>(){
                            @Override
                            public void handle(ActionEvent t) {
                                pipeDisplayer.getGraphicContext().drawImage(img2,40,120,400,400);
                            }
                        })
                ).build();

        //Prepare Welcome speech audio
        AudioClip speech2;
        speech2 = new AudioClip(this.getClass().getResource("audio.wav").toString());
        AudioClip speech1;
        speech1 = new AudioClip(this.getClass().getResource("audio2.wav").toString());

        //Animations
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(3),pipeDisplayer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);
        fadeIn.play();
        FadeTransition fadeIn2 = new FadeTransition(Duration.seconds(2),pipeDisplayer);
        fadeIn2.setFromValue(0);
        fadeIn2.setToValue(1);
        fadeIn2.setCycleCount(1);
         pipeDisplayer.getGraphicContext().setFont(new Font("Comic Sans MS", 50));
         pipeDisplayer.getGraphicContext().strokeText("Welcome to",100,100);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2),pipeDisplayer);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);
        speech1.play();
        fadeIn.setOnFinished((event -> {
            fadeOut.play();


        }));
        fadeOut.setOnFinished(event1 -> {
            pipeDisplayer.getGraphicContext().clearRect(0,0,500,500);
            pipeDisplayer.getGraphicContext().drawImage(img0,40,120,400,400);
            speech2.play();
            pipeDisplayer.getGraphicContext().strokeText("Pipe Game",125,100);
            fadeIn2.play();
        });
        fadeIn2.setOnFinished(event -> {
            a.play();
            tl.play();
            MusicMenu.setDisable(false);
            FileMenu.setDisable(false);

        });




    }
    public void canvasPressed(MouseEvent mouseEvent) {
        if (pipeDisplayer.pipeData!=null){
        int w = pipeData[0].length;
        int h = pipeData.length;
        clicks++;
        timeLabel.setText("  Seconds Passed: "+secondsPassed+"   Clicks: "+clicks);
        click.play();

        Image img;
        GraphicsContext gc = pipeDisplayer.getGraphicsContext2D();
        double W = gc.getCanvas().getWidth();
        double H = gc.getCanvas().getHeight()-50;
        double w2 = W/pipeData[0].length;
        double h2 = H/pipeData.length;

            for (int j =0; j < w ; j++){

                for (int i=0; i<h; i++){
                    if (mouseEvent.getX()>j*w2&&mouseEvent.getX()<w2*(j+1)&&mouseEvent.getY()>i*h2 &&mouseEvent.getY()<h2*(i+1) ){
                        System.out.println(i+","+j);
                        switch (pipeData[i][j]){
                            case '7':

                                gc.clearRect(j*w2,i*h2,w2,h2);

                                pipeData[i][j]='J';
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/j.png"));
                              //  gc.drawImage(img,j*w2,i*h2,w2,h2);
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                                break;
                            case '-':
                                gc.clearRect(j*w2,i*h2,w2,h2);

                                pipeData[i][j]='|';
                              //  img = new Image(getClass().getResourceAsStream("bg1.png"));
                              //  gc.drawImage(img,0,0,border.getWidth(),border.getHeight()-50);
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/i.png"));
                               pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                              //  gc.drawImage(img,j*w2,i*h2,w2,h2);

                                break;
                            case 'J':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/L.png"));
                                gc.clearRect(j*w2,i*h2,w2,h2);
                              //  gc.drawImage(img,j*w2,i*h2,w2,h2);
                                pipeData[i][j]='L';
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                                break;
                            case 'L':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/r.png"));
                                gc.clearRect(j*w2,i*h2,w2,h2);
                                pipeData[i][j]='F';
                            //    gc.drawImage(img,j*w2,i*h2,w2,h2);
                               pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                                break;
                            case '|':
                                gc.clearRect(j*w2,i*h2,w2,h2);
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/-.png"));
                                pipeData[i][j]='-';
                              //  gc.drawImage(img,j*w2,i*h2,w2,h2);
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                                break;
                            case 'F':

                                gc.clearRect(j*w2,i*h2,w2,h2);
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/1.png"));
                                pipeData[i][j]='7';
                            //    gc.drawImage(img,j*w2,i*h2,w2,h2);
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                                break;
                        }

                    }
                }
            }
        }


    }
    public void createNew(ActionEvent actionEvent) {
        tl.stop();
        pipeData= new char[][]{{'s', '|', '-', 'L'}, {'F', 'J', '-', 'J'}, {' ', 'g', ' ', ' '}};
        pipeDisplayer.setPipeData(pipeData);
        pipeDisplayer.reDraw(pipeDisplayer.getHeight()-50,pipeDisplayer.getWidth(),theme);
        secondsPassed=0;
        clicks=0;

        //resetTimer();
        count();



    }
    void anim(){
        RotateTransition rt = new RotateTransition(Duration.millis(100), pipeDisplayer);
        rt.setByAngle(720);
        rt.setAutoReverse(true);

        rt.play();

    }
    public void themeRed(ActionEvent actionEvent) {
        theme = 2;
        anim();
        pipeDisplayer.reDraw(pipeDisplayer.getHeight()-50,pipeDisplayer.getWidth(),theme);
    }
    public void themeBlack(ActionEvent actionEvent) {
        theme = 1;
        anim();
        pipeDisplayer.reDraw(pipeDisplayer.getHeight()-50,pipeDisplayer.getWidth(),theme);
    }
    public void stop(ActionEvent actionEvent) {
        a.stop();
    }
    public void play(ActionEvent actionEvent) {
        a.play();
    }
    public void countTime(){
        tl.stop();
        myTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        secondsPassed++;
                        timeLabel.setText("  Seconds Passed: "+secondsPassed+"   Clicks: "+clicks);
                      //  System.out.println(secondsPassed);
                    }
                });

            }
        };

        myTimer.scheduleAtFixedRate(task,1000,1000);

    }
    public void openFile(ActionEvent actionEvent) {
        tl.stop();
        pipeDisplayer.getGraphicContext().clearRect(0,0,1000,1000);
        FileChooser fc = new FileChooser();
        fc.setTitle("Open game file");
        fc.setInitialDirectory(new File("./resources"));
        File chosen = fc.showOpenDialog(null);
        c=0;
        r=0;
        if (chosen!=null){
            try (BufferedReader reader = new BufferedReader(new FileReader(chosen))) {

                String line;
                boolean first =true;
                while ((line = reader.readLine()) != null){
                    if (first){
                        c=line.length();
                        first=false;
                    }
                    System.out.println(line);
                    r++;

                }
                r--;
                pipeData = new char[r][c];
                try (BufferedReader reader2 = new BufferedReader(new FileReader(chosen))){
                    int j =0;
                /*    while ((line = reader2.readLine()) != null){
                        for (int i=0; i< c;i++){
                            pipeData[j][i]=line.charAt(i);
                        }
                        j++;
                    }*/
                    for (int x=0;x<r;x++){
                        line=reader2.readLine();
                        for (int i=0; i< c;i++){
                            pipeData[j][i]=line.charAt(i);
                        }
                        j++;
                    }
                    line=reader2.readLine();
                   for (int y=0;y<line.length();y++){
                       if (line.charAt(y)==','){
                           String time = line.substring(0,y);
                           secondsPassed = Integer.parseInt(time);
                           String numClicks = line.substring(y+1,line.length());
                           clicks = Integer.parseInt(numClicks);
                       }
                   }
                    pipeDisplayer.setPipeData(pipeData);
                    pipeDisplayer.reDraw(pipeDisplayer.getHeight()-50,pipeDisplayer.getWidth(),theme);
                    count();


                }catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        SaveMenu.setDisable(false);

    }
    public void count(){
        Main.ps.setResizable(true);
        ThemeMenu.setDisable(false);
        SolveMenu.setDisable(false);
        if (firstGame){
            countTime();
            firstGame=false;
        }else{
            //secondsPassed=0;
            //clicks=0;
            timeLabel.setText("  Seconds Passed: "+secondsPassed+"   Clicks: "+clicks);
        }
    }
    public void about(ActionEvent actionEvent) throws IOException {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
      //  dialog.initOwner();
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("\n                 Created by: \n" +
                "                Avihu Kochero\n" +
                "                  Yelena Sher"));
        Scene dialogScene = new Scene(dialogVbox, 200, 100);
        dialog.setTitle("About");
        dialog.setScene(dialogScene);
        dialog.setResizable(false);
        dialog.show();
    }
    public void mouseMoved(MouseEvent mouseEvent) {
    //    System.out.println(mouseEvent.getX()+" "+mouseEvent.getY());
        if (pipeData!=null){
            int w = pipeData[0].length;//4
            int h = pipeData.length;//3
            GraphicsContext gc = pipeDisplayer.getGraphicsContext2D();
            double H = gc.getCanvas().getHeight()-50;
            double W = gc.getCanvas().getWidth();
            double w2 = W/pipeData[0].length;
            double h2 = H/pipeData.length;
            gc.setStroke(Color.RED);
            gc.setLineWidth(5);

            for (int j =0; j < w ; j++) {

                for (int i = 0; i < h; i++) {
                    if (mouseEvent.getX() > j * w2 && mouseEvent.getX() < w2 * (j + 1) && mouseEvent.getY() > i * h2 && mouseEvent.getY() < h2 * (i + 1)) {
                       // System.out.println(i+","+j);
                        switch (pipeData[i][j]){
                            case '7':
                             //   img = new Image(getClass().getResourceAsStream("theme"+2+"/1.png"));
                                gc.clearRect(0,0,10000,10000);
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                               // gc.drawImage(img,j*w2,i*h2,w2,h2);

                                gc.strokeRect(j*w2,i*h2,w2,h2);
                                break;
                            case '-':
                           //     img = new Image(getClass().getResourceAsStream("theme"+2+"/-.png"));
                                gc.clearRect(0,0,10000,10000);
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                            //    gc.drawImage(img,j*w2,i*h2,w2,h2);
                                gc.strokeRect(j*w2,i*h2,w2,h2);
                                break;
                            case 'J':
                           //     img = new Image(getClass().getResourceAsStream("theme"+2+"/j.png"));
                                gc.clearRect(0,0,10000,10000);
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                           //     gc.drawImage(img,j*w2,i*h2,w2,h2);
                                gc.strokeRect(j*w2,i*h2,w2,h2);
                                break;
                            case 'L':
                            //    img = new Image(getClass().getResourceAsStream("theme"+2+"/L.png"));
                                gc.clearRect(0,0,10000,10000);
                               pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                            //    gc.drawImage(img,j*w2,i*h2,w2,h2);
                                gc.strokeRect(j*w2,i*h2,w2,h2);
                                break;
                            case '|':
                           //     img = new Image(getClass().getResourceAsStream("theme"+2+"/i.png"));
                                gc.clearRect(0,0,10000,10000);
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                           //     gc.drawImage(img,j*w2,i*h2,w2,h2);
                                gc.strokeRect(j*w2,i*h2,w2,h2);

                                break;

                            case 'F':
                           //     img = new Image(getClass().getResourceAsStream("theme"+2+"/r.png"));
                                gc.clearRect(0,0,10000,10000);
                                pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
                            //    gc.drawImage(img,j*w2,i*h2,w2,h2);

                                gc.strokeRect(j*w2,i*h2,w2,h2);

                                break;
                        }

                    }
                }
            }
        }


    }

    public void solve(ActionEvent actionEvent) {
        try {
            socket= new Socket("localhost",6400);
           outToServer = new PrintWriter(socket.getOutputStream());
         //   outToServer2 = new DataOutputStream(socket.getOutputStream());
         //  outToServer2.writeUTF("dsadasdsa");
        //    outToServer2.writeBytes("fadsfsad");
         //   outToServer.println("Hello");
         //   outToServer.flush();

            //    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            final Stage dialog2 = new Stage();
            dialog2.initModality(Modality.APPLICATION_MODAL);
            //  dialog.initOwner();
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("\n      Server is offline"));
            Scene dialogScene = new Scene(dialogVbox, 120, 50);
            dialog2.setTitle("Error");
            dialog2.setScene(dialogScene);
            dialog2.setResizable(false);
            dialog2.show();
        }
try{
    for (int j=0;j<pipeData.length;j++){
        String line="";
        for (int i=0;i<pipeData[0].length;i++){
            line+=pipeData[j][i];
        }
        outToServer.println(line);

    }
    outToServer.println("done");
    outToServer.flush();
} catch (NullPointerException e) {}



        BufferedReader in = null;
        String line;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (!(line = in.readLine()).equals("done"))
            {

                int i = Integer.parseInt(line.split(",")[0]);
                int j = Integer.parseInt(line.split(",")[1]);
                int times = Integer.parseInt(line.split(",")[2]);
                for (int x=0;x<times;x++){
                    changeTile(i,j);
                }
               System.out.println(i+" "+j+" "+times);
            }
            pipeDisplayer.reDraw(border.getHeight()-50,border.getWidth(),theme);
            anim();
            in.close();
            outToServer.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void changeTile(int i,int j){
        switch (pipeData[i][j]){
            case 'J':
                pipeData[i][j]='L';
                break;
            case '|':
                pipeData[i][j]='-';
                break;
            case 'L':
                pipeData[i][j]='F';
                break;
            case 'F':
                pipeData[i][j]='7';
                break;
            case '7':
                pipeData[i][j]='J';
                break;
            case '-':
                pipeData[i][j]='|';
                break;
        }

    }

    public void saveState(ActionEvent actionEvent)
    {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose location To Save Report");
        chooser.setInitialDirectory(new File("./resources/saves"));
        chooser.initialFileNameProperty().set("stage");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files","*.txt"));
        File selectedFile = null;

            selectedFile = chooser.showSaveDialog(null);


        File file = new File(String.valueOf(selectedFile));
        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int i = 0;i<pipeData.length;i++){
            for(int j=0;j<pipeData[0].length;j++){
                outFile.print(pipeData[i][j]);
            }
            outFile.println();
        }
        outFile.println(secondsPassed+","+clicks);
        outFile.close();

    }

    public void themeRoad(ActionEvent actionEvent) {
        theme = 3;
        anim();
        pipeDisplayer.reDraw(pipeDisplayer.getHeight()-50,pipeDisplayer.getWidth(),theme);
    }

    public void themeCandy(ActionEvent actionEvent) {
        theme = 4;
        anim();
        pipeDisplayer.reDraw(pipeDisplayer.getHeight()-50,pipeDisplayer.getWidth(),theme);
    }

    public void closeProgram(ActionEvent actionEvent) {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            actionEvent.consume();
            Platform.exit();
            System.exit(0);

        }
    }



}
