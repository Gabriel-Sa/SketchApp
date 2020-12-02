package com.example.jraw_test_2;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;

import android.graphics.BitmapFactory;

import java.io.*;
import java.nio.channels.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class Classifier {

    public static final String [] classes = {"bear", "bee", "bird", "cat", "cow",
            "dog", "dolphin", "duck", "elephant", "fish", "frog", "horse",
            "mouse", "penguin", "rabbit", "sheep", "snake", "whale"};
    private final String filename = "model.tflite";

    private Interpreter tf;
    private final Interpreter.Options tfOptions = new Interpreter.Options();
    private TensorBuffer probabilityBuffer;
    private Activity activity;

    Classifier(Activity activity) {
        this.activity = activity;
    }

    public class ModelOutput {
        String identifier;
        float probability;

        ModelOutput(String identifier, float probability) {
            this.probability = probability;
            this.identifier = identifier;
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(filename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public ModelOutput classify(Bitmap bitmap) throws IOException {

        if(bitmap == null) {
            Log.d("Classifier Error", "bitmap is NULL");
            return null;
        }

        tf = new Interpreter(loadModelFile(activity), tfOptions);

        TensorImage image = loadImageFromBitmap(bitmap);
        probabilityBuffer =
                TensorBuffer.createFixedSize(new int[]{1, Integer.BYTES * classes.length}, DataType.UINT8);
        tf.run(image.getBuffer(), probabilityBuffer.getBuffer());
        //We will print the output
        int maxProbabilityIndex = 0;
        for(int i = 0; i < classes.length; i++) {
            if(probabilityBuffer.getFloatArray()[i] > probabilityBuffer.getFloatArray()[maxProbabilityIndex])
                maxProbabilityIndex = i;
        }

        return new ModelOutput(classes[maxProbabilityIndex], probabilityBuffer.getFloatArray()[maxProbabilityIndex] / 255);

    }

    //Image transformations and preperation here
    private TensorImage loadImageFromBitmap(Bitmap bitmap) {

        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(255, 255, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .build();

        TensorImage tImage = new TensorImage(DataType.FLOAT32);
        tImage.load(bitmap);
        tImage = imageProcessor.process(tImage);

        return tImage;
    }



}
