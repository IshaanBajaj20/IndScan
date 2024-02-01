package com.turno.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.dragselectrecyclerview.BuildConfig;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import org.matomo.sdk.extra.TrackHelper;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.turno.myapplication.Utils.addImageToGallery;

public class Scandash extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/scanSample/";
    ImageView scannedImageView;
    Button button,b123;
    private boolean mBugRotate;
    private SharedPreferences mSharedPref;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static final String TAG = "MyApplication";
    CircleImageView photo;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout button123,button124,button125;
    LinearLayout ll1,ll2,ll3,ll4;

    public static ArrayList<File> fileList = new ArrayList<>();
    PDFAdapter obj_adapter;
    public static int REQUEST_PERMISSION = 1;
    boolean boolean_permission;
    File dir;
    ListView lv_pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scandash);
        scannedImageView = (ImageView) findViewById(R.id.scannedImage);
        if (Build.VERSION.SDK_INT>=21){
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

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


        photo = findViewById(R.id.photo);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        drawerLayout = findViewById(R.id.drawer_layout);
        button123 = findViewById(R.id.button123);
        button124 = findViewById(R.id.button124);
        button125 = findViewById(R.id.button125);
        button=findViewById(R.id.test1);
        b123 = findViewById(R.id.b123);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        ll4 = findViewById(R.id.ll4);

        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), createPDF.class);
                startActivity(intent);
            }
        });

        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        ll4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "IndScan is the best scanner in the market to scan your documents and convert them to PDF along with other features which make handling your professional documents easy.";
                String shareSub = "Download IndScan from the Google Play Store now.";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share via"));
            }
        });

        b123.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), More.class);
                startActivity(intent);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),GalleryGridActivity.class);
                startActivity(intent);
            }
        });




        button124.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ImagetoText.class);
                startActivity(intent);
            }
        });

        button125.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),QRScan.class);
                startActivity(intent);
            }
        });


        navigationDrawer();


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){

            Uri personPhoto = signInAccount.getPhotoUrl();

            Glide.with(this).load(String.valueOf(personPhoto)).into(photo);


        }

    }

    private void permission_fn() {

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(Scandash.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            }else {
                ActivityCompat.requestPermissions(Scandash.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    boolean twice;

    @Override
    public void onBackPressed() {
        Log.d(TAG, "click");

        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (twice==true){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }
        twice = true;
        Log.d(TAG, "twice: " + twice);

        Toast.makeText(Scandash.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                twice=false;
                Log.d(TAG, "twice: " + twice);
            }
        },2000);


    }

    private static final int OPEN_THING = 99;

    public void openGallery(View v) {
        int preference = ScanConstants.OPEN_MEDIA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, OPEN_THING);
    }

    public void openCamera(View v) {
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, OPEN_THING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == OPEN_THING) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    getContentResolver().delete(uri, null, null);
                    scannedImageView.setImageBitmap(bitmap);

                    BitmapDrawable drawable = (BitmapDrawable) scannedImageView.getDrawable();
                    Bitmap image = drawable.getBitmap();
                    File path = Environment.getExternalStorageDirectory();
                    File dir = new File(path+"/IndScan");
                    dir.mkdirs();
                    String fileName = "DOC-"
                            + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
                    File file = new File(dir, fileName+".jpg");

                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        image.compress(Bitmap.CompressFormat.JPEG,100,out);
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }





    public void saveDocument(ScannedDocument scannedDocument) {

        Mat doc = (scannedDocument.processed != null) ? scannedDocument.processed : scannedDocument.original;

        Intent intent = getIntent();
        String fileName;
        boolean isIntent = false;
        Uri fileUri = null;

        String imgSuffix = ".jpg";
        if (mSharedPref.getBoolean("save_png", false)) {
            imgSuffix = ".png";
        }

        if (intent.getAction().equals("android.media.action.IMAGE_CAPTURE")) {
            fileUri = ((Uri) intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT));
            Log.d(TAG, "intent uri: " + fileUri.toString());
            try {
                fileName = File.createTempFile("onsFile", imgSuffix, this.getCacheDir()).getPath();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            isIntent = true;
        } else {
            String folderName = mSharedPref.getString("storage_folder", "OpenNoteScanner");
            File folder = new File(Environment.getExternalStorageDirectory().toString()
                    , "/" + folderName);
            if (!folder.exists()) {
                folder.mkdirs();
                Log.d(TAG, "wrote: created folder " + folder.getPath());
            }

            fileName = createFileName(imgSuffix, folderName);
        }
        Mat endDoc = new Mat(Double.valueOf(doc.size().width).intValue(),
                Double.valueOf(doc.size().height).intValue(), CvType.CV_8UC4);

        Core.flip(doc.t(), endDoc, 1);

        Imgcodecs.imwrite(fileName, endDoc);
        endDoc.release();

        try {
            ExifInterface exif = new ExifInterface(fileName);
            exif.setAttribute("UserComment", "Generated using Open Note Scanner");
            String nowFormatted = mDateFormat.format(new Date().getTime());
            exif.setAttribute(ExifInterface.TAG_DATETIME, nowFormatted);
            exif.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED, nowFormatted);
            exif.setAttribute("Software", "OpenNoteScanner " + BuildConfig.VERSION_NAME + " https://goo.gl/2JwEPq");
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isIntent) {
            InputStream inputStream = null;
            OutputStream realOutputStream = null;
            try {
                inputStream = new FileInputStream(fileName);
                realOutputStream = this.getContentResolver().openOutputStream(fileUri);
                // Transfer bytes from in to out
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    realOutputStream.write(buffer, 0, len);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } finally {
                try {
                    inputStream.close();
                    realOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        Log.d(TAG, "wrote: " + fileName);

        if (isIntent) {
            new File(fileName).delete();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            addImageToGallery(fileName, this);
        }

    }

    private String createFileName(String imgSuffix, String folderName) {
        String fileName;
        fileName = Environment.getExternalStorageDirectory().toString()
                + "/" + folderName + "/";
        fileName += "DOC-"
                + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())
                + imgSuffix;
        return fileName;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_qr:
                startActivity(new Intent(getApplicationContext(), QRScan.class));
                break;

            case R.id.nav_search:
                startActivity(new Intent(getApplicationContext(), About.class));
                break;

            case  R.id.nav_settings:
                startActivity(new Intent(getApplicationContext(), ImagetoText.class));
                break;

            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                break;

            case R.id.nav_create:
                startActivity(new Intent(getApplicationContext(), createPDF.class));
                break;

            case R.id.nav_settings2:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

            case R.id.nav_share:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "IndScan is the best scanner in the market to scan your documents and convert them to PDF along with other features which make handling your professional documents easy.";
                String shareSub = "Download IndScan from the Google Play Store now.";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share via"));
                break;

        }
        return true;
    }

}
