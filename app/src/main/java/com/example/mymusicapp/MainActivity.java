package com.example.mymusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //step-0 create instance and locate
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //step-0 create instance and locate
        listView=findViewById(R.id.listView);

        //step-1 apply user permission in android AndroidManifest.xml
        //step-2 prompt in app asking for permission(search for dexter android on web and find the dependecies that needed)
        //for this project ->       implementation 'com.karumi:dexter:6.2.3'
        //copy the below format from same github material
        //Dexter.withContext(activity)
        //       .withPermission(permission)
        //       .withListener(listener)
        //       .check();

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    //case 1 if permission given
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //Toast.makeText(MainActivity.this,"Permission Granted", Toast.LENGTH_SHORT).show();

                        //step-4 show songs in dashboard
                        ArrayList<File> mysongs=fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items=new String[mysongs.size()];

                        for (int i = 0; i < mysongs.size(); i++) {
                            items[i]=mysongs.get(i).getName().replace(".mp3","");
                        }
                        ArrayAdapter<String> ad=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                        listView.setAdapter(ad);

                        //step-5 playing a particular song using Intent concept
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, Playsong.class);
                                String currentSong = listView.getItemAtPosition(position).toString();
                                intent.putExtra("songList", mysongs);   //check
                                intent.putExtra("currentSong", currentSong);
                                intent.putExtra("position", position);
                                startActivity(intent);

                            }
                        });

                    }

                    //case 2 if permission rejected
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    //case 1 if permission skipped
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();    //this will ask for permission everytime app is opened
                    }
                })
                .check();
    }

    //step-3 real all mp3 files in the system
    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList=new ArrayList();
        File[] songs=file.listFiles();
        if(songs != null){
            for(File myfile:songs){
                if(!myfile.isHidden() && myfile.isDirectory()){
                    arrayList.addAll(fetchSongs(myfile));
                }
                else{
                    if(myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")){
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return arrayList;
    }
}


//step-6 set (android:parentActivityName=".MainActivity") for Playsong activity in Manifest -> to create a back button in app
//step 7 in activity_playsong.xml
// for logo -> add imageview
// for play/pause, previous, next-> new -> vector asset -> clip art -> search accordingly


//final step-adding app icon -> new -> image asset -> clip art -> search accordingly

//set android:requestLegacyExternalStorage="true" in application tag in manifiest