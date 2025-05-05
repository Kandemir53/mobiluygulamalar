package com.example.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;

    Button btnTakePhoto, btnRecordVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnRecordVideo = findViewById(R.id.btnRecordVideo);

        // Resim Çekme Butonu
        btnTakePhoto.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "Kamera mevcut değil", Toast.LENGTH_SHORT).show();
            }
        });

        // Video Çekme Butonu
        btnRecordVideo.setOnClickListener(view -> {
            Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (recordVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(recordVideoIntent, REQUEST_VIDEO_CAPTURE);
            } else {
                Toast.makeText(this, "Kamera mevcut değil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Activity sonuçları (Resim ve Video çekme)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Resim çekildiyse, resmi işleyebiliriz
                Uri imageUri = data.getData();
                Toast.makeText(this, "Resim başarıyla çekildi: " + imageUri, Toast.LENGTH_LONG).show();
            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                // Video çekildiyse, videoyu işleyebiliriz
                Uri videoUri = data.getData();
                Toast.makeText(this, "Video başarıyla kaydedildi: " + videoUri, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "İşlem iptal edildi", Toast.LENGTH_SHORT).show();
        }
    }
}
