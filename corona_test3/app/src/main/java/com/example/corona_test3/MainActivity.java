package com.example.corona_test3;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    PieChart pieChart;
    int[] colorArray = new int[] {Color.parseColor("#003399"), Color.parseColor("#FF3333")};
    private ImageButton alarm, menu;
    private DrawerLayout drawerLayout;
    private TextView text1;
    private ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
    private static final String TAG = "corona_helper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieChart =(PieChart)findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        yValues.add(new PieEntry(100f,"SAFE"));

        PieDataSet dataSet = new PieDataSet(yValues,"/ COVID-19");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(Color.parseColor("#003399"));

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);

        menu = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        long now=System.currentTimeMillis();
        Date mDate=new Date(now);
        SimpleDateFormat simpleDate=new SimpleDateFormat("hh:mm");
        String getTime=simpleDate.format(mDate);
        if(getTime=="12:00"||getTime=="00:00")
            Receive();

        alarm=findViewById(R.id.alarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);

                ad.setTitle("확진자 판정을 받으셨습니까?");       // 제목 설정
                ad.setMessage("코로나 확진자 판정 여부");   // 내용 설정
                ad.setIcon(R.drawable.alarm);

                // 확인 버튼 설정
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        text1 = findViewById(R.id.text1);
                        text1.setText("확률은 100% 입니다.");

                        yValues.remove(0);
                        yValues.add(new PieEntry(100f,"WARNING"));

                        PieDataSet dataSet = new PieDataSet(yValues,"/ COVID-19");
                        dataSet.setSliceSpace(3f);
                        dataSet.setSelectionShift(5f);
                        dataSet.setColors(Color.parseColor("#FF3333"));

                        PieData data = new PieData((dataSet));
                        data.setValueTextSize(10f);
                        data.setValueTextColor(Color.WHITE);

                        pieChart.setData(data);
                        pieChart.invalidate();

                        Send();

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                // 취소 버튼 설정
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                ad.show();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Response.Listener<String> responseListener = new Response.Listener<String>() { // php 접속 응답 확인
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    Log.d(TAG,"RESPONSE+"+success);
                    if(success){
                        Receive();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        CheckActivity checkActivity=new CheckActivity(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(checkActivity);

    }
    private void Receive(){
        Log.d(TAG,"RECEIVE");
        //차트
    }

    private void Send(){

        int count=3;
        String []Latitude={"35.8399111","35.83884688","38.8390426"};
        String []Longitude={"128.7509877","128.7512056","130.73664802"};
        String []Time={"2020:11:8 21:05", "2020:11:5 22:45","2020:11:16 16:4"};

        Response.Listener<String> responseListener = new Response.Listener<String>() { // php 접속 응답 확인
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String success2=jsonObject.getString("success2");
                    String time=jsonObject.getString("time");

                    if(success){
                        Log.d(TAG,"success2:"+success2);
                        Log.d(TAG,"successive:"+ time);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        for(int i=0;i<3;i++)
        {
            // 서버로 Volley를 이용해서 요청
            AddressRequest addressRequest = new AddressRequest(count,Latitude[i], Longitude[i], Time[i], responseListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(addressRequest);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home){

        }

        else if (id == R.id.nav_path){
            Intent intent = new Intent(MainActivity.this, MyPathActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_news){

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}