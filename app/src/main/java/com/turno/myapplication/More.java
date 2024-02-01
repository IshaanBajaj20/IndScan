package com.turno.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class More extends AppCompatActivity {


    ImageView hi;
    ListView lv_pdf;
    public static ArrayList<File> fileList = new ArrayList<>();
    PDFAdapter obj_adapter;
    public static int REQUEST_PERMISSION = 1;
    boolean boolean_permission;
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        lv_pdf = (ListView)findViewById(R.id.listView_pdf);

        dir = new File(Environment.getExternalStorageDirectory()+"/"+"IndScan");


        permission_fn();

        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ViewPDFFiles.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        hi = findViewById(R.id.hi);

        hi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                More.super.onBackPressed();
            }
        });




    }

    private void permission_fn() {

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(More.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            }else {
                ActivityCompat.requestPermissions(More.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }
        } else {
            boolean_permission = true;
            getfile(dir);
            obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
            lv_pdf.setAdapter(obj_adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                boolean_permission = true;
                getfile(dir);
                obj_adapter = new PDFAdapter(getApplicationContext(), fileList);
                lv_pdf.setAdapter(obj_adapter);

            }
            else  {
                Toast.makeText(this, "Please allow the permissions to access the files", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ArrayList<File> getfile(File dir){

        File listFile[] = dir.listFiles();
        if (listFile!=null && listFile.length > 0){
            for (int i=0; i<listFile.length; i++){
                if (listFile[i].isDirectory()){

                    getfile(listFile[i]);
                }
                else {
                    boolean booleanpdf = false;
                    if (listFile[i].getName().endsWith(".pdf")){
                        for (int j=0; j<fileList.size(); j++){
                            if (fileList.get(j).getName().equals(listFile[i].getName())){
                                booleanpdf = true;
                            }
                            else {

                            }
                        }
                        if (booleanpdf){
                            booleanpdf = false;
                        }
                        else {
                            fileList.add(listFile[i]);
                        }
                    }
                }
            }
        }
        return fileList;
    }
}