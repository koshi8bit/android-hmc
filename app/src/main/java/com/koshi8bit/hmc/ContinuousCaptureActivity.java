package com.koshi8bit.hmc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.media.MediaPlayer;
import android.view.View;
import android.os.Vibrator;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class ContinuousCaptureActivity extends Activity {
    private DecoratedBarcodeView barcodeView;
    private String lastText;
    private ImageView imageView;
    private ArrayList<String> list;
    private boolean only_receipt_format = true;
    private boolean with_sound = true;
    MediaPlayer myMediaPlayer;
    Vibrator v;


    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String res = result.getText();
            if(res == null || res.equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            if (only_receipt_format)
            {
                if (!res.matches("^t=\\d+T\\d+&s=\\d+.\\d+&fn=\\d+&i=\\d+&fp=\\d+&n=\\d$")){
                    return;
                }
            }

            lastText = res;
            barcodeView.setStatusText(res);

//            beepManager.playBeepSoundAndVibrate();
//            myMediaPlayer.stop();
            if (with_sound) {
                myMediaPlayer.start();
            }
            v.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
            list.add(res);

            //Added preview of scanned barcode
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Log.d("3", "4");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("AAAAAAAAA", "onCreate");
        super.onCreate(savedInstanceState);
        myMediaPlayer = MediaPlayer.create(ContinuousCaptureActivity.this,
                R.raw.mario_coin_sound);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        Intent indent = getIntent();
        only_receipt_format = indent.getBooleanExtra("only_receipt_format", true);
        with_sound = indent.getBooleanExtra("with_sound", true);

        setContentView(R.layout.continuous_scan);

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats;
        if (only_receipt_format)
        {
            formats = Collections.singletonList(BarcodeFormat.QR_CODE);
        }
        else
        {
            formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39, BarcodeFormat.DATA_MATRIX);
        }
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        imageView = findViewById(R.id.barcodePreview);
        list = new ArrayList<>();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void ready(View view) {
        ready_all();
    }

    private void ready_all()
    {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_TEXT, String.join("\n", list));
        setResult(RESULT_OK, intent);
        finish();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Toast.makeText(this, String.format(Locale.US, "keyCode is %d", keyCode), Toast.LENGTH_LONG).show();
        if ((keyCode == 24) || (keyCode == 25)) {
            return false;
        }
        boolean res = barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
        if (res) {
            ready_all();
        }
        return res;
    }
}
