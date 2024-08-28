package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager; // Ekle
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.net.Uri;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.excelai.myapplication.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView cevap;
    private EditText mesaj;
    private Button gonder;
    public static AdRequest adRequest=new AdRequest.Builder().build();
    Button geri;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Ekrana her dokunulduğunda klavyeyi gizle
        InputMethodManager imm = (InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {


            }

        });
        mesaj = findViewById(R.id.mesaj);
        gonder = findViewById(R.id.gonder);
        geri=findViewById(R.id.geri);
        mesaj.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mesaj, InputMethodManager.SHOW_IMPLICIT);


        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mesajText = mesaj.getText().toString().trim();

                if (mesajText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lütfen mesaj alanını boş bırakmayınız", Toast.LENGTH_LONG).show();
                    return;
                }


                try {
                    mesaj.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    setContentView(R.layout.video);
                    VideoView videoView=findViewById(R.id.videoView);
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video);
                    videoView.setVideoURI(uri);
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.setLooping(true); // Video loop
                            videoView.start(); // Start playing the video
                        }
                    });

                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.start(); // Start the video again when it's completed
                        }
                    });
                    String apilink = "https://api.openai.com/v1/chat/completions";
                    String apikey = "";

                    JSONObject object = new JSONObject();
                    JSONArray messagesArray = new JSONArray();
                    JSONObject messageObject = new JSONObject();
                    messageObject.put("role", "system");
                    messageObject.put("content", '"' + mesajText + '"' + " " + "bu tırnak içindeki metne senin vereceğin cevap eğer Excel ile alakalıysa sadece cevabı yaz,başka bir şey yazma. Değilse sadece 'üzgünüm ben sadece elektronik tablolar ile ilgili sorulara cevap verebilirim' yaz.");
                    messagesArray.put(messageObject);
                    object.put("messages", messagesArray);
                    object.put("model", "gpt-3.5-turbo");



                    // Klavyeyi kapat



                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apilink, object,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    videoView.stopPlayback();





                                    try {


                                        JSONArray choices = response.getJSONArray("choices");
                                        if (choices.length() > 0) {
                                            JSONObject firstChoice = choices.getJSONObject(0);
                                            JSONObject messageObject = firstChoice.getJSONObject("message");
                                            String content = messageObject.getString("content");

                                            Cevap.deger=content;
                                            Intent i=new Intent(getApplicationContext(),Cevap.class);
                                            startActivity(i);
                                        } else {
                                            Cevap.deger=("API yanıtında geçerli bir içerik bulunamadı.");
                                            Intent i=new Intent(getApplicationContext(),Cevap.class);
                                            startActivity(i);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        // JSON dönüşümünde özel bir hata oluştuğunda bunu belirtelim
                                        Log.e("JSON Hata", "JSON dönüşümünde bir hata oluştu: " + e.getMessage());
                                        Toast.makeText(getApplicationContext(), "JSON dönüşümünde bir hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        Cevap.deger=("JSON dönüşümünde bir hata oluştu: " + e.getMessage());
                                        Intent i=new Intent(getApplicationContext(),Cevap.class);
                                        startActivity(i);
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errorMessage;
                            videoView.stopPlayback();


                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    errorMessage = new String(error.networkResponse.data);
                                } catch (Exception e) {
                                    errorMessage = "Network hatası: Data okunamadı";
                                }
                            } else {
                                errorMessage = "Bilinmeyen bir hata oluştu";
                            }
                            // Hata mesajını cevap TextView'ına yazdır
                            Cevap.deger=errorMessage;
                            Intent i=new Intent(getApplicationContext(),Cevap.class);
                            startActivity(i);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/json");
                            headers.put("Authorization", "Bearer " + apikey);
                            return headers;
                        }
                    };


                    int socketTimeout = 100000;
                    request.setRetryPolicy(new DefaultRetryPolicy(socketTimeout,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    queue.add(request);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "İstek oluşturulurken bir hata oluştu", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
