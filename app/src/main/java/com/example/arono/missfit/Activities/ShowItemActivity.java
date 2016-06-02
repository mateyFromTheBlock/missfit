package com.example.arono.missfit.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.arono.missfit.Drawer.BaseActivityWithNavigationDrawer;
import com.example.arono.missfit.PictureUtility;
import com.example.arono.missfit.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ShowItemActivity extends BaseActivityWithNavigationDrawer implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    private FrameLayout contentFrame;
    private LayoutInflater inflater;
    private TextView size;
    private TextView phone;
    private TextView brand;
    private TextView price;
    private TextView color;
    private TextView type;
    private TextView description;
    private Button btnCall;


    private SliderLayout slider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFrameView();
        initTitle();
        initTitle();

        slider = (SliderLayout)findViewById(R.id.slider);
        btnCall = (Button)findViewById(R.id.btnCall);

        HashMap<String,String> url_maps = new HashMap<>();
        setUpImages(url_maps);
        setUpTextInSlider(url_maps);
        setSlider();

        setAttributes();

        callBtnIntent();


    }

    private void setAttributes() {

        description = (TextView)findViewById(R.id.show_item_description);

        if(getIntent().getStringExtra("Description") != null)
            description.setText("Description: " + getIntent().getStringExtra("Description"));
        else
            description.setText("Description: Not Specified");

        size = (TextView)findViewById(R.id.show_item_size);

        if(getIntent().getStringExtra("size") != null)
            size.setText("Size: " + getIntent().getStringExtra("size"));
        else
            size.setText("Size: Not Specified");

        phone =  (TextView)findViewById(R.id.show_item_phone);

        if(getIntent().getStringExtra("phone") != null)
            phone.setText("Phone: " + getIntent().getStringExtra("phone"));
        else
            phone.setText("Phone: Not Specified");


        brand = (TextView)findViewById(R.id.show_item_brand);

        if(getIntent().getStringExtra("brand") != null)
            brand.setText("Brand: " + getIntent().getStringExtra("brand"));
        else
            brand.setText("Brand: Not Specified");

        price = (TextView)findViewById(R.id.show_item_price);

        if(getIntent().getStringExtra("price") != null)
            price.setText("Price: " + getIntent().getStringExtra("price"));
        else
            price.setText("Price: Not Specified");



        color = (TextView)findViewById(R.id.show_item_color);

        if(getIntent().getStringExtra("color") != null)
            color.setText("Color: " + getIntent().getStringExtra("color"));
        else
            color.setText("Color: Not Specified");

        type = (TextView)findViewById(R.id.show_item_type);


        if(getIntent().getStringExtra("type")!= null)
            type.setText("Type: " + getIntent().getStringExtra("type"));
        else
            type.setText("Type: Not Specified");


    }


    public void initFrameView(){
        contentFrame = getContentFrame();
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_show_item, null, false);
        contentFrame.addView(view);
    }

    public void initTitle(){
        Intent titleIntent = getIntent();
        String title =  titleIntent.getStringExtra("Description");
        getSupportActionBar().setTitle(title);
    }

    public void setUpImages(HashMap<String,String> url_maps){
        if (getIntent().getStringExtra("photoOne") != null)
            url_maps.put("#1",  getIntent().getStringExtra("photoOne") );
        if (getIntent().getStringExtra("photoTwo")  != null)
            url_maps.put("#2", getIntent().getStringExtra("photoTwo") );
        if (getIntent().getStringExtra("photoThree")  != null)
            url_maps.put("#3", getIntent().getStringExtra("photoThree") );
    }

    public void setUpTextInSlider(HashMap<String,String> url_maps){
        for(String name : url_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            slider.addSlider(textSliderView);
        }
    }

    public void setSlider(){
        slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        slider.setCustomAnimation(new DescriptionAnimation());
        slider.stopAutoCycle();
        slider.addOnPageChangeListener(this);

    }

    public void callBtnIntent(){
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText().toString()));
                startActivity(intent);
            }
        });
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        slider.stopAutoCycle();
        super.onStop();
    }
}
