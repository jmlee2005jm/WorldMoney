package com.example.worldmoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MoneyAdapter extends BaseAdapter {
    ArrayList<IncomeItem> mItems;
    Context mContext;
    LayoutInflater inflater;

    public MoneyAdapter(Context context, ArrayList<IncomeItem> items){
        this.mItems = new ArrayList<>();
        this.mItems.addAll(items);
        this.mContext = context;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItem(ArrayList<IncomeItem> items) {
        if(mItems != null){
            mItems.clear();
            mItems.addAll(items);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder{
        TextView amountTV, descTV;
        ImageView itemIconIV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_income,parent,false);
            holder = new Holder();

            holder.amountTV = convertView.findViewById(R.id.amountTV);
            holder.descTV = convertView.findViewById(R.id.descTV);
            holder.itemIconIV = convertView.findViewById(R.id.itemIconIV);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        IncomeItem item = mItems.get(position);

        if(item != null){
            holder.amountTV.setText(item.getAmount()+"원");
            holder.itemIconIV.setImageResource(item.getImageRes());
            holder.descTV.setText(item.getDesc());
            if(item.getPositive()) {
                holder.amountTV.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorIncomeMoney));
            } else {
                holder.amountTV.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorSpentMoney));
            }
        }

        return convertView;
    }
}
