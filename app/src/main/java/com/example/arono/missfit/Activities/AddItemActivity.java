package com.example.arono.missfit.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.example.arono.missfit.DataServerManagement.DataManager;
import com.example.arono.missfit.Drawer.BaseActivityWithNavigationDrawer;
import com.example.arono.missfit.Item;
import com.example.arono.missfit.PictureUtility;
import com.example.arono.missfit.R;

import java.io.File;
import java.io.IOException;
import java.util.Random;


public class AddItemActivity extends BaseActivityWithNavigationDrawer implements View.OnClickListener{


    private static int RESULT_LOAD_IMAGE = 1;
    private static int CAPTURE_PHOTO = 2;
    private static int IMAGE_VIEW_SIZE = 3;

    private ImageView imageViews[],colorImageView;
    private CheckBox checkBoxIvFirst,checkBoxIvSecond,checkBoxIvThird;
    private Spinner sizeSpinner,typeSpinner,colorSpinner;
    private EditText etPrice,etDescription,etBrand;
    private EditText etPhoneNumber;
    private PictureUtility pictureUtility;
    private int num = 0,imageChanged[];
    private DataManager dataManager;
    private BackendlessUser user;
    private Button btnCancel,btnSave,btnCamera;
    private String phone;
    private Bitmap[] bitmaps;
    private Uri outputFileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFrameView();
        initTitle();
        initialize();

        checkBoxEnable();
        cancelActivity();
        setSpinnerListener();
        saveItem();
        takePhoto();



    }

    private void initFrameView() {
        FrameLayout contentFrame = getContentFrame();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_add_item, null, false);
        contentFrame.addView(view);
    }


    private void initTitle() {
        Intent titleIntent = getIntent();
        String title =  titleIntent.getStringExtra("title");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public void initialize(){
        dataManager = new DataManager();
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCamera = (Button) findViewById(R.id.btnCamera);

        checkBoxIvFirst = (CheckBox) findViewById(R.id.checkBoxIvFirst);
        checkBoxIvSecond = (CheckBox) findViewById(R.id.checkBoxIvSecond);
        checkBoxIvThird = (CheckBox) findViewById(R.id.checkBoxIvThird);
        checkBoxIvSecond.setEnabled(false);
        checkBoxIvThird.setEnabled(false);
        etPhoneNumber = new EditText(this);


        imageViews = new ImageView[IMAGE_VIEW_SIZE];
        imageChanged = new int[IMAGE_VIEW_SIZE];

        imageViews[0] = (ImageView)findViewById(R.id.ivFirstPic);
        imageViews[1] = (ImageView)findViewById(R.id.ivSecPic);
        imageViews[2] = (ImageView)findViewById(R.id.ivThirdtPic);
        imageViews[1].setVisibility(View.INVISIBLE);
        imageViews[2].setVisibility(View.INVISIBLE);
        colorImageView = (ImageView) findViewById(R.id.colorImageView);

        sizeSpinner = (Spinner) findViewById(R.id.sizeSpinner);
        typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        colorSpinner = (Spinner)findViewById(R.id.colorSpinner);

        etPrice = (EditText) findViewById(R.id.etPrice);
        etDescription = (EditText) findViewById(R.id.etDescription);
        etBrand = (EditText)findViewById(R.id.etBrand);

        for(int i = 0 ; i < IMAGE_VIEW_SIZE ; i++){
            imageChanged[i] = 0;
            imageViews[i].setImageResource(R.drawable.add3);
            imageViews[i].setOnClickListener(this);
        }
        bitmaps = new Bitmap[3];
        pictureUtility = new PictureUtility(this);
    }

    public void cancelActivity(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void saveItem(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = typeSpinner.getSelectedItem().toString();
                Item.Size size = Item.stringToSize(sizeSpinner.getSelectedItem().toString());
                String color = colorSpinner.getSelectedItem().toString();
                String description = etDescription.getText().toString();
                String checkPrice = etPrice.getText().toString();
                String brand = etBrand.getText().toString();
                user = Backendless.UserService.CurrentUser();

                int sizeOfImages = checkHowMuchImagesTheUserUpload();
                boolean[] checkFlags = new boolean[4];

                checkingVariableNotEmpty(checkFlags, description, checkPrice, sizeOfImages, brand);

                if (checkFlags[0] && checkFlags[1] && checkFlags[2] && checkFlags[3]) {
                    Float price = Float.parseFloat(checkPrice);
                    if (price > 0) {
                        Item item = new Item();
                        item.setItem(description, price, type, user, size, color, brand);

                        String[] photos = new String[sizeOfImages];
                        num = getRandomId();

                        for (int i = 0; i < sizeOfImages; i++) {
                            photos[i] = dataManager.uploadPictureToTheServer(bitmaps[i], num, description + (i + 1));
                        }

                        if (sizeOfImages >= 1) {
                            item.setPhotoOne(photos[0]);
                            if (sizeOfImages >= 2) {
                                item.setPhotoTwo(photos[1]);
                                if (sizeOfImages == 3)
                                    item.setPhotoThird(photos[2]);
                            }
                        }


                        dataManager.uploadToServer(item, getApplicationContext());

                        phone = (String) user.getProperty("phone");

                        if (phone == null) {
                            updatePhonePropertyAlert();
                        } else
                            uploadAnotherItemAlertDialog();


                    } else {
                        Toast.makeText(getApplication().getApplicationContext(), "Price must be greater then zero", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (!checkFlags[0] && !checkFlags[1] && !checkFlags[2]) {
                        Toast.makeText(getApplication().getApplicationContext(), "must fill All Field", Toast.LENGTH_SHORT).show();
                    } else if (!checkFlags[0]) {
                        Toast.makeText(getApplication().getApplicationContext(), "must set Description", Toast.LENGTH_SHORT).show();
                    } else if (!checkFlags[1]) {
                        Toast.makeText(getApplication().getApplicationContext(), "must set Price", Toast.LENGTH_SHORT).show();
                    } else if (!checkFlags[2]) {
                        Toast.makeText(getApplication().getApplicationContext(), "at least 1 image", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getApplication().getApplicationContext(), "must set Brand", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void takePhoto(){
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/missFit/";
                File newDir = new File(dir);
                newDir.mkdirs();
                int x = getRandomId();
                String file = dir + x + ".jpg";
                File newFile = new File(file);
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    Log.e("Error", e.getMessage());
                }
                Intent cameraIntent = new Intent();
                cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                outputFileUri = Uri.fromFile(newFile);
                SharedPreferences sharedPreferences = getSharedPreferences("URI", MODE_PRIVATE);
                SharedPreferences.Editor spEdit = sharedPreferences.edit();
                spEdit.putString("uri", outputFileUri.getPath());
                spEdit.apply();

                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, CAPTURE_PHOTO);
                }
                Log.e("Error", outputFileUri.getPath());

            }
        });
    }

    public void updatePhonePropertyAlert(){
        AlertDialog.Builder phoneNumberAlert = new AlertDialog.Builder(this);
        etPhoneNumber.setHintTextColor(Color.BLACK);
        etPhoneNumber.setTextColor(Color.BLACK);
        etPhoneNumber.setHint("Enter phone number");
        phoneNumberAlert.setView(etPhoneNumber);

        phoneNumberAlert.setTitle("Enter phone number").setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String phoneNumber = etPhoneNumber.getText().toString();
                user.setProperty("phone", phoneNumber);

                Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Log.e("ErrorPhoneNumber", "Work");
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e("Error", fault.getMessage());
                    }
                });
                uploadAnotherItemAlertDialog();
            }
        });
        AlertDialog alertDialog = phoneNumberAlert.create();
        alertDialog.show();
    }

    public void uploadAnotherItemAlertDialog(){
        AlertDialog.Builder finishUploadAlert = new AlertDialog.Builder(this);
        finishUploadAlert.setTitle("Finish Upload").setMessage("Do you want to upload another Item?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < IMAGE_VIEW_SIZE; j++) {
                    imageViews[j].setImageResource(R.drawable.add3);
                    imageChanged[j] = 0;
                }
                imageViews[1].setVisibility(View.INVISIBLE);
                imageViews[2].setVisibility(View.INVISIBLE);
                etDescription.setText("");
                etPrice.setText("");
                etBrand.setText("");
                checkBoxIvFirst.setChecked(false);
                checkBoxIvSecond.setChecked(false);
                checkBoxIvThird.setChecked(false);


            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alertDialog1 = finishUploadAlert.create();
        alertDialog1.show();
    }

    public void setSpinnerListener(){
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkingColor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    public void checkingColor(){
        String color = colorSpinner.getSelectedItem().toString();
        switch(color){
            case "Black":colorImageView.setBackgroundColor(Color.BLACK);
                break;
            case "Blue":colorImageView.setBackgroundColor(Color.BLUE);
                break;
            case "Green":colorImageView.setBackgroundColor(Color.GREEN);
                break;
            case "Yellow":colorImageView.setBackgroundColor(Color.YELLOW);
                break;
            case "Red":colorImageView.setBackgroundColor(Color.RED);
                break;
            case "White":colorImageView.setBackgroundColor(Color.WHITE);
                break;
            case "Gray":colorImageView.setBackgroundColor(Color.GRAY);
                break;
        }

    }
    public int getRandomId(){
        Random random = new Random();
        return random.nextInt(10000);
    }

    public void checkingVariableNotEmpty(boolean checkFlags[], String description, String price, int sizeOfImages, String brand){

        if(description.compareTo("") == 0){
            checkFlags[0] = false;
        }else
            checkFlags[0] = true;
        if(price.compareTo("") == 0){
            checkFlags[1] = false;
        }else
            checkFlags[1] = true;
        if(sizeOfImages == 0){
            checkFlags[2] = false;
        }else
            checkFlags[2] = true;
        if(brand.compareTo("") == 0){
            checkFlags[3] = false;
        }else
            checkFlags[3] = true;
    }

    public int checkHowMuchImagesTheUserUpload(){
        int size = 0;
        for(int i = 0  ; i < IMAGE_VIEW_SIZE ; i++){
            if(imageChanged[i] == 1) {
                size++;
            }
        }
        return size;
    }


    public void checkBoxEnable() {
        checkBoxIvFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxIvFirst.isChecked()){
                    checkBoxIvFirst.setChecked(true);
                    checkBoxIvSecond.setChecked(false);
                    checkBoxIvThird.setChecked(false);
                }else {
                    checkBoxIvFirst.setChecked(false);
                }

            }
        });
        checkBoxIvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxIvSecond.isChecked()) {
                    checkBoxIvSecond.setChecked(true);
                    checkBoxIvThird.setChecked(false);
                    checkBoxIvFirst.setChecked(false);
                } else {
                    checkBoxIvSecond.setChecked(false);
                }
            }
        });
        checkBoxIvThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxIvThird.isChecked()) {
                    checkBoxIvThird.setChecked(true);
                    checkBoxIvSecond.setChecked(false);
                    checkBoxIvFirst.setChecked(false);
                } else {
                    checkBoxIvThird.setChecked(false);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.rotateId){
            Bitmap bitmapRotate;
            if (checkBoxIvFirst.isChecked()) {
                bitmapRotate = ((BitmapDrawable) imageViews[0].getDrawable()).getBitmap();
                imageViews[0].setImageBitmap(pictureUtility.rotateImage(bitmapRotate, PictureUtility.ORIENTATION_ROTATE_90));
            }
            if (checkBoxIvSecond.isChecked()) {
                bitmapRotate = ((BitmapDrawable) imageViews[1].getDrawable()).getBitmap();
                imageViews[1].setImageBitmap(pictureUtility.rotateImage(bitmapRotate, PictureUtility.ORIENTATION_ROTATE_90));
            }
            if (checkBoxIvThird.isChecked()) {
                bitmapRotate = ((BitmapDrawable) imageViews[2].getDrawable()).getBitmap();
                imageViews[2].setImageBitmap(pictureUtility.rotateImage(bitmapRotate, PictureUtility.ORIENTATION_ROTATE_90));
            }
        }
        if(id == R.id.deleteId){
            if (checkBoxIvFirst.isChecked()) {
                imageViews[0].setImageResource(R.drawable.add3);

            }
            if (checkBoxIvSecond.isChecked()) {
                imageViews[1].setImageResource(R.drawable.add3);
                checkBoxIvSecond.setEnabled(false);

            }
            if (checkBoxIvThird.isChecked()) {
                imageViews[2].setImageResource(R.drawable.add3);
                checkBoxIvThird.setEnabled(false);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        switch(id){
            case R.id.ivFirstPic:   num = 1;
                break;
            case R.id.ivSecPic:     num = 2;
                break;
            case R.id.ivThirdtPic:  num = 3;
                break;
        }
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            getImageFromGallery(data);
        }
        else if(requestCode == CAPTURE_PHOTO && resultCode == RESULT_OK){
            getImageFromCamera();
        }
    }

    public void getImageFromGallery(Intent data){
        Bitmap bitmap = pictureUtility.getPictureFromGallery(data);
        Bitmap temp = bitmap;
        switch(num){
            case 1:
                bitmaps[num-1] = bitmap;
                imageViews[num-1].setImageBitmap(pictureUtility.cropCenter(temp));
                imageChanged[num - 1] = 1;
                imageViews[num].setVisibility(View.VISIBLE);
                break;
            case 2: bitmaps[num-1] = bitmap;
                imageViews[num-1].setImageBitmap(pictureUtility.cropCenter(temp));
                imageChanged[num-1] = 1;
                imageViews[num].setVisibility(View.VISIBLE);
                checkBoxIvSecond.setEnabled(true);
                break;
            case 3: bitmaps[num-1] = bitmap;
                imageViews[num-1].setImageBitmap(pictureUtility.cropCenter(temp));
                imageChanged[num-1] = 1;
                checkBoxIvThird.setEnabled(true);
                break;
        }
        num = 0;
    }

    public void getImageFromCamera(){
        SharedPreferences sharedPreferences = getSharedPreferences("URI", MODE_PRIVATE);
        String s = sharedPreferences.getString("uri", null);

        File f = new File(s);
        Uri uri = Uri.fromFile(f);
        int angle = pictureUtility.checkOrientation(uri.getPath());
        Bitmap bitmap = pictureUtility.shrinkBitmap(uri.getPath(), 500, 500);
        Bitmap sr = pictureUtility.rotateImage(bitmap, angle);
        if(imageChanged[0] == 0) {
            bitmaps[0] = sr;
            imageViews[0].setImageBitmap(pictureUtility.cropCenter(sr));
            imageViews[1].setVisibility(View.VISIBLE);
            checkBoxIvSecond.setEnabled(true);
            imageChanged[0] = 1;
        }
        else if(imageChanged[1] == 0) {
            bitmaps[1] = sr;
            imageViews[1].setImageBitmap(pictureUtility.cropCenter(sr));
            imageViews[2].setVisibility(View.VISIBLE);
            checkBoxIvThird.setEnabled(true);
            imageChanged[1] = 1;
        }
        else{
            bitmaps[2] = sr;
            imageViews[2].setImageBitmap(pictureUtility.cropCenter(sr));
            imageChanged[2] = 1;
        }
    }
}


