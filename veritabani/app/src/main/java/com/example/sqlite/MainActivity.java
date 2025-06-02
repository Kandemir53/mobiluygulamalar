package com.example.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText ad, soyad, yas, sehir;
    TextView bilgiler;
    Button kaydet, goster, sil, guncelle;

    private Veritabani vt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vt = new Veritabani(this);

        ad = findViewById(R.id.editTextAd);
        soyad = findViewById(R.id.editTextSoyad);
        yas = findViewById(R.id.editTextYas);
        sehir = findViewById(R.id.editTextSehir);
        bilgiler = findViewById(R.id.textViewBilgiler);

        kaydet = findViewById(R.id.buttonEkle);
        goster = findViewById(R.id.buttonGoster);
        sil = findViewById(R.id.buttonSil);
        guncelle = findViewById(R.id.buttonGuncelle);

        kaydet.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt Ekle butonuna basıldı.");
            kayitEkle(ad.getText().toString(), soyad.getText().toString(),
                    yas.getText().toString(), sehir.getText().toString());
        });

        goster.setOnClickListener(view -> {
            Log.d(TAG, "Kayıtları Göster butonuna basıldı.");
            Cursor cursor = kayitGetir();
            kayitGoster(cursor);
        });

        sil.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt Sil butonuna basıldı.");
            kayitSil(ad.getText().toString());
        });

        guncelle.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt Güncelle butonuna basıldı.");
            kayitGuncelle(ad.getText().toString(), soyad.getText().toString(),
                    yas.getText().toString(), sehir.getText().toString());
        });
    }

    private void kayitEkle(String adi, String soyadi, String yasi, String sehri) {
        try {
            SQLiteDatabase db = vt.getWritableDatabase();
            ContentValues veriler = new ContentValues();
            veriler.put("ad", adi);
            veriler.put("soyad", soyadi);
            veriler.put("yas", Integer.parseInt(yasi));
            veriler.put("sehir", sehri);
            db.insertOrThrow("OgrenciBilgi", null, veriler);
            db.close();
            Log.d(TAG, "Kayıt başarıyla eklendi.");
        } catch (Exception e) {
            Log.e(TAG, "Kayıt eklenirken hata oluştu: " + e.getMessage());
        }
    }

    private void kayitSil(String adi) {
        try {
            SQLiteDatabase db = vt.getWritableDatabase();
            int rows = db.delete("OgrenciBilgi", "ad = ?", new String[]{adi});
            db.close();
            Log.d(TAG, "Kayıt silindi. Silinen satır sayısı: " + rows);
        } catch (Exception e) {
            Log.e(TAG, "Kayıt silinirken hata oluştu: " + e.getMessage());
        }
    }

    private void kayitGuncelle(String adi, String soyadi, String yasi, String sehri) {
        try {
            SQLiteDatabase db = vt.getWritableDatabase();
            ContentValues guncelle = new ContentValues();
            guncelle.put("soyad", soyadi);
            guncelle.put("yas", Integer.parseInt(yasi));
            guncelle.put("sehir", sehri);
            int rows = db.update("OgrenciBilgi", guncelle, "ad = ?", new String[]{adi});
            db.close();
            Log.d(TAG, "Kayıt güncellendi. Güncellenen satır sayısı: " + rows);
        } catch (Exception e) {
            Log.e(TAG, "Kayıt güncellenirken hata oluştu: " + e.getMessage());
        }
    }

    private Cursor kayitGetir() {
        SQLiteDatabase db = vt.getReadableDatabase();
        String[] sutunlar = {"ad", "soyad", "yas", "sehir"};
        return db.query("OgrenciBilgi", sutunlar, null, null, null, null, null);
    }

    private void kayitGoster(Cursor cursor) {
        StringBuilder builder = new StringBuilder();
        try {
            while (cursor.moveToNext()) {
                String ad = cursor.getString(cursor.getColumnIndexOrThrow("ad"));
                String soyad = cursor.getString(cursor.getColumnIndexOrThrow("soyad"));
                String yas = cursor.getString(cursor.getColumnIndexOrThrow("yas"));
                String sehir = cursor.getString(cursor.getColumnIndexOrThrow("sehir"));

                builder.append("Ad: ").append(ad).append("\n")
                        .append("Soyad: ").append(soyad).append("\n")
                        .append("Yaş: ").append(yas).append("\n")
                        .append("Şehir: ").append(sehir).append("\n")
                        .append("----\n");
            }
            bilgiler.setText(builder.toString());
        } catch (Exception e) {
            Log.e(TAG, "Kayıtlar gösterilirken hata oluştu: " + e.getMessage());
        } finally {
            cursor.close();
        }
    }
}
