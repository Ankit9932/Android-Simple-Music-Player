package com.example.mymusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Playsong extends AppCompatActivity {
    //click ctrl+o to get this method
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    //create instance
    TextView songName;
    ImageView previous,play,next;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    ArrayList<File> songs;
    int position;
    String TextContent; //check2
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);

        //locate instance
        songName=findViewById(R.id.songName);
        previous=findViewById(R.id.previous);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        seekBar=findViewById(R.id.seekBar);

        //create intent
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        songs=(ArrayList) bundle.getParcelableArrayList("songList");
        TextContent=intent.getStringExtra("currentSong");
        songName.setText(TextContent);
        songName.setSelected(true); //this line is added after implementing marquee tags in its XML code of this textView (check xml code)
        position=intent.getIntExtra("position",0);
        Uri uri= Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        //this is to move seekbar according to timeline using thread concept in java
        //first create thread in create instance then perform below
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek=new Thread(){
            //again override using ctrl+o
            @Override
            public void run() {
                int currentposition=0;
                try{
                    while(currentposition< mediaPlayer.getDuration()){
                        currentposition= mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();


        //adding buttons functionalities

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(position!=0){
                    position=position-1;
                }
                else{
                    position=songs.size()-1;    //this will play last song in the list
                }
                Uri uri= Uri.parse(songs.get(position).toString()); //this is to update seekbar
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);    //this will set middle button as pause initially
                seekBar.setMax(mediaPlayer.getDuration());

                TextContent=songs.get(position).getName().toString();   //this is to show previous song name
                songName.setText(TextContent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(position!=songs.size()-1){
                    position=position+1;
                }
                else{
                    position=0;    //this will play first song in the list
                }
                Uri uri= Uri.parse(songs.get(position).toString()); //this is to update seekbar
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);    //this will set middle button as pause initially
                seekBar.setMax(mediaPlayer.getDuration());

                TextContent=songs.get(position).getName().toString();   //this is to show next song name
                songName.setText(TextContent);
            }
        });

    }
}