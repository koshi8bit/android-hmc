package com.koshi8bit.hmc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class ContinuousCaptureActivity extends Activity {
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    private ImageView imageView;
    private ArrayList<String> list;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String res = result.getText();
            if(res == null || res.equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = res;
            barcodeView.setStatusText(res);

            beepManager.playBeepSoundAndVibrate();
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

        setContentView(R.layout.continuous_scan);

        barcodeView = findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        imageView = findViewById(R.id.barcodePreview);
        list = new ArrayList<>();

        beepManager = new BeepManager(this);
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
        boolean res = barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
        if (res) {
            ready_all();
        }
        return res;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // check that it is the SecondActivity with an OK result
//        if (requestCode == RESULT_OK) {
//            String returnString = data.getStringExtra(Intent.EXTRA_TEXT);
//        }
//
//    }
}
