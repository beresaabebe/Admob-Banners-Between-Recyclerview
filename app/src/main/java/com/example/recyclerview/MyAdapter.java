package com.example.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_COUNTRY = 0;
    private static final int ITEM_BANNER_AD = 1;
    private final ArrayList<Object> mList;
    private final MyRecyclerViewItemClickListener clickListener;

    public MyAdapter(ArrayList<Object> mList, MyRecyclerViewItemClickListener clickListener) {
        this.mList = mList;
        this.clickListener = clickListener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case ITEM_BANNER_AD:
                View bannerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_ad_row, parent, false);
                return new AdviewHolder(bannerView);
            case ITEM_TYPE_COUNTRY:
            default:
                View countyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.county_row, parent, false);
                final CountryHolder countryHolder = new CountryHolder(countyView);
                countryHolder.itemView.setOnClickListener(view -> {
                    clickListener.onItemClicked((CountryModel) mList.get(countryHolder.getLayoutPosition()));
                });
                return countryHolder;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_COUNTRY:
                if (mList.get(position) instanceof CountryModel) {
                    CountryHolder countryHolder = (CountryHolder) holder;
                    CountryModel model = (CountryModel) mList.get(position);
                    countryHolder.name.setText(model.getName());
                    String capital = "Capital: "+model.getCapital();
                    countryHolder.capital.setText(capital);
                    String currency = "Currency: "+model.getCurrency();
                    countryHolder.currency.setText(currency);
                    String continent = "Continent: "+model.getContinent();
                    countryHolder.continent.setText(continent);
                }
                break;
            case ITEM_BANNER_AD:
            default:
                if (mList.get(position) instanceof AdView) {
                    AdviewHolder adviewHolder = (AdviewHolder) holder;
                    AdView adView = (AdView) mList.get(position);
                    ViewGroup adCardView = (ViewGroup) adviewHolder.itemView;
                    if (adCardView.getChildCount() > 0) {
                        adCardView.removeAllViews();
                    }
                    if (adView.getParent() != null) {
                        ((ViewGroup)adView.getParent()).removeView(adView);
                    }
                    adCardView.addView(adView);
                }
        }
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || mList.get(position) instanceof CountryModel)
        {
            return ITEM_TYPE_COUNTRY;
        } else
        {
            return (position % MainActivity.ITEMS_PER_AD == 0) ? ITEM_BANNER_AD : ITEM_TYPE_COUNTRY;
        }
    }
    static class CountryHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView capital;
        private final TextView currency;
        private final TextView continent;

        public CountryHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.countryName);
            capital = itemView.findViewById(R.id.capital);
            currency = itemView.findViewById(R.id.currency);
            continent = itemView.findViewById(R.id.continent);

        }
    }
    static class AdviewHolder extends RecyclerView.ViewHolder {
        public AdviewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public interface MyRecyclerViewItemClickListener {
        void onItemClicked(CountryModel model);
    }
}
