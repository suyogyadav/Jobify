package com.kernel.jobify;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    DatabaseReference databaseRef;
    List<Object> jobslist;
    List<Object> adlist;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        MobileAds.initialize(this,"ca-app-pub-3335585827854611~4200408157");

        ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();


        if (info!= null && info.isConnected()) {
            setContentView(R.layout.activity_main_2);

            Toolbar toolbar = findViewById(R.id.newstoolbar);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            jobslist = new ArrayList<>();

            retrivedata(getIntent().getStringExtra("CAT"));
        }
        else
        {
            setContentView(R.layout.blnt);
            Toolbar toolbar = findViewById(R.id.blnttoolbar);
            toolbar.setNavigationIcon(R.drawable.ic_action_back);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }



    public void retrivedata(String CAT)
    {
        databaseRef = FirebaseDatabase.getInstance().getReference(CAT);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                jobslist.clear(); /// remember remove this.

                if(dataSnapshot.getChildrenCount()==0)
                {
                    Toast.makeText(context,"Sorry No Jobs Avilable At Time",Toast.LENGTH_SHORT).show();
                }
                else{
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        JobData jobData = dataSnapshot1.getValue(JobData.class);
                        jobslist.add(jobData);

                        Log.i("ABCD", "" + dataSnapshot1.getKey());
                        Log.i("ABCD", "" + jobData.getJobTitle());
                    }

                    Collections.reverse(jobslist);
                    jobslist = getbannerad(jobslist);
                    jobslist = loadads(jobslist);
                    RecyclerView res = findViewById(R.id.rscview);
                    res.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    res.setAdapter(new NewsAdapter(jobslist,getBaseContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public List<Object> getbannerad(List<Object> jobslist)
    {
        for (int i =1;i<jobslist.size();i=i+3)
        {
            final AdView adView = new AdView(this);
            adView.setAdSize(new AdSize(300,250));
            adView.setAdUnitId("ca-app-pub-3335585827854611/5421974575");
            Log.i("ABCD","AD IS GETTING PLACED"+i);
            jobslist.add(i,adView);
        }
        return jobslist;
    }


    public List<Object> loadads(List<Object> jobslist)
    {
        for (int i=0;i<jobslist.size();i++)
        {
            Object item = jobslist.get(i);

            if (item instanceof AdView)
            {
                final AdView adView = (AdView) item;
                adView.loadAd(new AdRequest.Builder().build());
            }
        }
        return jobslist;
    }


}

