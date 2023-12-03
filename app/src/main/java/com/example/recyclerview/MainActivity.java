package com.example.recyclerview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recyclerview.contents.CapitalName;
import com.example.recyclerview.contents.ContinentName;
import com.example.recyclerview.contents.CountryName;
import com.example.recyclerview.contents.CurrencyName;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    public static final int ITEMS_PER_AD = 7;
    private final ArrayList<Object> mListItems = new ArrayList<>();
    private final CapitalName capitalName = new CapitalName();
    private final ContinentName continentName = new ContinentName();
    private final CountryName countryName = new CountryName();
    private final CurrencyName currencyName = new CurrencyName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAdMobAdsSDK();
        addCountryData();
        setUIRef();
        adMobBanner();
        loadBannerAds();
    }
    private void initAdMobAdsSDK() {
        MobileAds.initialize(this, initializationStatus -> {
        });
    }
    private void setUIRef() {
        //Reference of RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);

        //Linear Layout Manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);

        //Set Layout Manager to RecyclerView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Create adapter
        MyAdapter myRecyclerViewAdapter = new MyAdapter(mListItems, model -> Toast.makeText(MainActivity.this, model.getName(), Toast.LENGTH_SHORT).show());

        //Set adapter to RecyclerView
        mRecyclerView.setAdapter(myRecyclerViewAdapter);
    }
    private void addCountryData() {
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < countryName.country.length; j++) {
                mListItems.add(new CountryModel(countryName.country[j],capitalName.capital[j],currencyName.currency[j],continentName.continent[j]));
//            mListItems.add(new CountryModel("Country " + i, "Capital " + i, "dummy " + i, "dummy "+i));
            }
        }
    }
    private void adMobBanner() {
        for (int i = ITEMS_PER_AD; i <= mListItems.size(); i += ITEMS_PER_AD)
        {
            final AdView adView = new AdView(MainActivity.this);
            AdSize adSize = getAdSize();
            adView.setAdSize(adSize);
            adView.setAdUnitId(getResources().getString(R.string.banner_ads_id));
            mListItems.add(i, adView);
        }
        loadBannerAds();
    }
    private void loadBannerAds() {
        loadBannerAd(ITEMS_PER_AD);
    }
    private void loadBannerAd(final int index) {
        if (index >= mListItems.size())
        {
            return;
        }
        Object item = mListItems.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Excepted item at index "+ index+ "banner item");
        }

        final AdView adView = (AdView) item;
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                @SuppressLint("DefaultLocale")
                String error = String.format("domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                Log.e("MainActivity","The previous banner ad failed to load with error: "+ error+ ". Attempting to"
                                + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
                Toast.makeText(MainActivity.this, "Failed to load ads!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdLoaded() {
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }
    private AdSize getAdSize() {
        //Determine the screen width to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        //you can also pass your selected width here in dp
        int adWidth = (int) (widthPixels / density);

        //return the optimal size depends on your orientation (landscape or portrait)
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
    @Override
    protected void onResume() {
        for (Object item : mListItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        for (Object item : mListItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.pause();
            }
        }
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        for (Object item : mListItems) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.destroy();
            }
        }
        super.onDestroy();
    }
}