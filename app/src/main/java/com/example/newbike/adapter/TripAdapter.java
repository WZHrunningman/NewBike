package com.example.newbike.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newbike.R;
import com.example.newbike.model.trip;

import java.util.List;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private Context context;
    private List<trip> list;

    public TripAdapter(Context context, List<trip> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvStart.setText(list.get(i).getStart_address());
        viewHolder.tvEnd.setText(list.get(i).getEnd_address());
        viewHolder.tvPayment.setText(list.get(i).getPayment() + "元");
        viewHolder.tv_time.setText(list.get(i).getTime());
        viewHolder.tv_date.setText(list.get(i).getUpdatedAt());
        viewHolder.tv_bike_num.setText(list.get(i).getBike_num());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStart,tvEnd,tvPayment,tv_date,tv_bike_num,tv_time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPayment = itemView.findViewById(R.id.tv_payment);
            tvStart = itemView.findViewById(R.id.tv_start);
            tvEnd = itemView.findViewById(R.id.tv_end);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_bike_num = itemView.findViewById(R.id.tv_bike_num);
        }
    }
}
