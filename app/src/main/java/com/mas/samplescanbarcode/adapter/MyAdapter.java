package com.mas.samplescanbarcode.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.util.Linkify;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mas.samplescanbarcode.R;
import com.mas.samplescanbarcode.model.ListItem;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    List<ListItem> listItems;
    Context context;

    public MyAdapter(List<ListItem> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem listItem = listItems.get(position);
        holder.tvId.setText(String.valueOf(listItem.get_id()));
        holder.tvCode.setText(listItem.getCode());
        holder.tvType.setText(listItem.getType());
        Linkify.addLinks(holder.tvCode, Linkify.ALL);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvId, tvCode, tvType;

        public TextView getTvId(){
            return tvId;
        }

        public ViewHolder(View itemView){
            super(itemView);

            tvId = (TextView)itemView.findViewById(R.id.tvId);
            tvCode = (TextView)itemView.findViewById(R.id.tvCode);
            tvType = (TextView)itemView.findViewById(R.id.tvType);
        }
    }
}
