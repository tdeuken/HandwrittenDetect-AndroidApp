package com.amlfinalproject.tim.aml_number_recognizer;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;


import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class Classifier {

    Interpreter tflite;

    float confidence;
    int label;

    //Input Byte Buffer
    private ByteBuffer inputBuffer = null;
    //Output array
    private float[][] finalOutput = null;
    // Specify the output size
    private static final int nLength = 10;

    // Input Size
    private static final int dimBatch_Size = 1;
    private static final int dimImgSize_X = 28;
    private static final int dimImgSize_Y = 28;
    private static final int dimPixelSize = 1;

    private static final int byteSizeOfFloat = 4;

    public Classifier(Activity activity) {
        try{
            tflite = new Interpreter(loadModelFile(activity));
            inputBuffer = ByteBuffer.allocateDirect(byteSizeOfFloat*dimImgSize_X*dimImgSize_Y*dimBatch_Size*dimPixelSize);
            inputBuffer.order(ByteOrder.nativeOrder());
            finalOutput = new float[dimBatch_Size][nLength];
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {

        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("K-CNN0.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }

    public int detectDrawing(Bitmap bitmap){
        preprocess(bitmap);
        runInference();
        int pNum = getPredict();
        return pNum;
    }

    private void preprocess(Bitmap bitmap){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int [width*height];

        bitmap.getPixels(pixels,0,width,0,0,width,height);

        if(bitmap == null || inputBuffer == null){
            return;
        }

        inputBuffer.rewind();

        for(int i = 0; i< pixels.length; i++){
            int pixel = pixels[i];

            int channel = pixel & 0xff;
            inputBuffer.putFloat(0xff-channel);
        }

    }

    protected void runInference(){
        tflite.run(inputBuffer, finalOutput);
        System.out.print(finalOutput);
    }

    private int getPredict(){

        float oldConfidenceScore=0;
        int label = -1;

        for(int i =0; i<finalOutput[0].length; ++i){

            float currentConfidenceScore = finalOutput[0][i];
            if(currentConfidenceScore > .1f && oldConfidenceScore < currentConfidenceScore){

                oldConfidenceScore = currentConfidenceScore;
                label = i;
            }

        }
        return label;
    }

}
