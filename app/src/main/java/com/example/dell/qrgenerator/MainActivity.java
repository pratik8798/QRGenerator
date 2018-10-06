package com.example.dell.qrgenerator;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public final static int QRcodeWidth=500;
    private static final String IMAGE_DIRECTORY="/QRcode";

    Bitmap bitmap;
    private EditText etqr;
    private ImageView iv;
    private Button btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv=(ImageView)findViewById(R.id.iv);
        btn=(Button)findViewById(R.id.btn);
        etqr=(EditText)findViewById(R.id.etqr);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etqr.getText().toString().trim().length()==0)
                {
                    Toast.makeText(MainActivity.this,"Enter String",Toast.LENGTH_SHORT).show();

                }
                else
                {
                    try {
                        bitmap=TextToImageEncode(etqr.getText().toString());
                        iv.setImageBitmap(bitmap);
                        //String path=saveImageInternal(bitmap);
                        //Toast.makeText(MainActivity.this,"QR Code saved to ->"+path,Toast.LENGTH_SHORT).show();
                        saveImage(bitmap,etqr.getText().toString());
                    }
                    catch(WriterException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String saveImageInternal(Bitmap bitmap)
    {
        ContextWrapper cw=new ContextWrapper(getApplicationContext());
        File directory=cw.getDir("/storage/self/primary/OCRQ",Context.MODE_WORLD_WRITEABLE);
        if(!directory.exists())
        {
            Log.d("QRCO",""+directory.mkdirs());
            directory.mkdirs();
        }

        File mypath=new File(directory,Calendar.getInstance().getTimeInMillis()+".jpg");
        FileOutputStream fos=null;

        try {
            mypath.createNewFile();

            fos=new FileOutputStream(mypath);
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG,100,bytes);
            fos.write(bytes.toByteArray());


        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }
    private void saveImage(Bitmap finalBitmap, String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        //File file = new File("/storage/self/primary/Pictures/OCRQ", fname);
        File file = new File(myDir, fname);

        //if (file.exists()) file.delete();
        try {
            file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"couldnt create file",Toast.LENGTH_SHORT).show();

        }
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"couldnt create"+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();

        }
        Toast.makeText(MainActivity.this,"QR Code saved to ->"+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();

    }

    private void internal(Bitmap bitmap)
    {
        try {
            final FileOutputStream fos = openFileOutput("new1.jpg", Context.MODE_WORLD_READABLE);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,fos);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    private String saveImage(Bitmap bitmap) {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,90,bytes);
        File wallpaperDirectory =new File(Environment.getDataDirectory()+IMAGE_DIRECTORY);

        if(!wallpaperDirectory.exists())
        {
            Log.d("dirr",""+wallpaperDirectory.mkdirs());
            wallpaperDirectory.mkdirs();
        }
        try
        {
            File f=new File(wallpaperDirectory,Calendar.getInstance().getTimeInMillis()+".jpg");
            f.createNewFile();
            FileOutputStream fo=new FileOutputStream(f);

            fo.write(bytes.toByteArray());

            MediaScannerConnection.scanFile(this,new String[]{f.getPath()},new String[]{"image/jpeg"},null);

            fo.close();
            Log.d("TAG","File Saved->"+f.getAbsolutePath());

            return f.getAbsolutePath();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
