package com.amlfinalproject.tim.aml_number_recognizer;

import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.amlfinalproject.tim.aml_number_recognizer.view.DrawClass;

import org.tensorflow.lite.Interpreter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Interpreter tflite;
    DrawClass dClass;

    private Classifier myClassifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Generate for buttons
        findViewById(R.id.Clear).setOnClickListener(this);
        findViewById(R.id.Classify_Button).setOnClickListener(this);

        myClassifier = new Classifier(this);

        dClass = findViewById(R.id.drawClassXML);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.Clear:
                dClass.clear();
                break;

            case R.id.Classify_Button:
                classifyButton();
                break;
        }
    }

    private void classifyButton(){

        TextView predictionView = findViewById(R.id.Prediction_Text);

        Bitmap bitmap = dClass.getBitmap();

        Bitmap downSizedBitMap = Bitmap.createScaledBitmap(bitmap, 28,28,false);
        ImageView imageTest = findViewById(R.id.imageView);
        imageTest.setImageBitmap(downSizedBitMap);

        int digit = myClassifier.detectDrawing(downSizedBitMap);


        predictionView.setText("Passed the Detection");
        if(digit>=0){
            predictionView.setText(String.valueOf(digit));
        } else {
            predictionView.setText("Could not find that Number :(");
        }

    }


}
