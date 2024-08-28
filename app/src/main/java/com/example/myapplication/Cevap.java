package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.excelai.myapplication.R;
import com.google.android.gms.ads.AdView;

public class Cevap extends AppCompatActivity {
    public static String deger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cevap);
        AdView adView=findViewById(R.id.reklam);
        TextView cevap=findViewById(R.id.gelenmesaj);
        Button geridiger=findViewById(R.id.geridiger);
        Button tekrar=findViewById(R.id.tekrar);
        cevap.setText(deger);

        adView.loadAd(MainActivity.adRequest);
        tekrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
        geridiger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

    }
}
