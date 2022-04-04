package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;

public class PipeDisplayer extends Canvas {

    char[][] pipeData;

    public void setPipeData(char[][] pipeData){
        this.pipeData = pipeData;
        //reDraw();
    }
    public GraphicsContext getGraphicContext(){
        return getGraphicsContext2D();
    }
    public void reDraw(double h1, double w1,int theme){

        if (pipeData!=null){

           // double W = getWidth();
          //  double H = getHeight();
            double W= w1;
            double H = h1;
            double w = W/pipeData[0].length;
            double h = H/pipeData.length;
            int blockSize = 75;

            Image img = null;

            // img = new Image(new FileInputStream("/img.jpg"));

            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0,0,1000,1000);
            img = new Image(getClass().getResourceAsStream("bg3.png"));
            gc.drawImage(img,0,0,this.getWidth(),this.getHeight());
            for (int i = 0; i<pipeData.length;i++){
                for (int j = 0 ; j <pipeData[i].length;j++){
                    if (pipeData[i][j]!=0){
                       /* if (img==null){
                            gc.fillRect(j*w,i*h,w,h);
                        }else{
                            gc.drawImage(img,j*w,i*h,w,h);
                        }*/
                        switch (pipeData[i][j]){
                            case '7':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/1.png"));
                                gc.drawImage(img,j*w,i*h,w,h);

                                break;
                            case '-':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/-.png"));
                                gc.drawImage(img,j*w,i*h,w,h);
                                break;
                            case 'g':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/E.png"));
                                gc.drawImage(img,j*w,i*h,w,h);
                                break;
                            case 'J':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/j.png"));
                                gc.drawImage(img,j*w,i*h,w,h);
                                break;
                            case 'L':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/L.png"));
                                gc.drawImage(img,j*w,i*h,w,h);
                                break;
                            case '|':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/i.png"));
                                gc.drawImage(img,j*w,i*h,w,h);

                                break;
                            case 's':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/s.png"));
                                gc.drawImage(img,j*w,i*h,w,h);
                                break;
                            case ' ':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/empty.png"));
                                gc.drawImage(img,j*w,i*h,w,h);
                                break;
                            case 'F':
                                img = new Image(getClass().getResourceAsStream("theme"+theme+"/r.png"));
                                gc.drawImage(img,j*w,i*h,w,h);
                                break;
                        }


                    }
                }
            }
        }
    }

}
