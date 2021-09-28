package com.test.cinemometar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler db;
    EditText searchEditTxt;
    Button searchBtn;
    TextView body;
    String searchTxt;
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);

        searchEditTxt = findViewById(R.id.searchEditTxt);
        body = findViewById(R.id.body);
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTxt = searchEditTxt.getText().toString().toUpperCase(Locale.ROOT);
                if(!searchTxt.isEmpty()){
                    try{
                        String raw = db.getStation(searchTxt).getData();
                        String decoded = db.getStation(searchTxt).getDecoded();
                        String bodyHtml = "<strong>Raw</strong><br>" + raw + "<br><br><strong>Decoded</strong><br>" + decoded;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            body.setText(Html.fromHtml(bodyHtml, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            body.setText(Html.fromHtml(bodyHtml));
                        }
                    }catch (Exception e){
                        body.setText("Information not found! Please check station name!");
                    }


                }else{
                    Toast.makeText(MainActivity.this, "Station name required!", Toast.LENGTH_LONG).show();
                    searchEditTxt.setFocusable(true);
                }
            }
        });

    }

    public void getFiles() throws Exception {
        Request request = new Request.Builder()
                .url("https://tgftp.nws.noaa.gov/data/observations/metar/stations/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Pattern patternGermany = Pattern.compile(">ED([^>]*).TXT");

                Matcher matcherFiles = patternGermany.matcher(response.body().string());

                while (matcherFiles.find()) {

                    try {
                        updateStation("https://tgftp.nws.noaa.gov/data/observations/metar/stations/ED"+matcherFiles.group(1)+".TXT", "ED"+matcherFiles.group(1));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    public void updateStation(String link, String station_id) throws Exception {
        Request request = new Request.Builder()
                .url(link)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try {

                    if (db.checkStation(station_id)){
                        db.updateStation(new Station(db.getStation(station_id).getID(), station_id, station_id, response.body().string(), "test"));
                        System.out.print("if: "+station_id);
                    }else{
                        db.addStation(new Station(station_id, station_id, response.body().string(), ""));
                        System.out.print("else: "+station_id);
                    }
                    updateDecodedStation("https://tgftp.nws.noaa.gov/data/observations/metar/decoded/"+station_id+".TXT", station_id);
                } catch (Exception e){
                    System.out.print("Err: "+e.getMessage());
                }
            }
        });
    }
    public void updateDecodedStation(String link, String station_id) throws Exception {
        Request request = new Request.Builder()
                .url(link)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                System.out.println( "enc: "+station_id);
                System.out.println( "encTT: "+db.checkStation(station_id));

                try {

                        db.updateStation(new Station(db.getStation(station_id).getID(), station_id, station_id, "none", response.body().string()));

                } catch (Exception e){
                    System.out.print("Err: "+e.getMessage());
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        try {
            addStations();
            getFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStations() {
        db.addStation(new Station("EEEE", "EEEE", "dummy", "dummy"));
    }
}