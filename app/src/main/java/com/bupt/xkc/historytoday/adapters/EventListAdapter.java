package com.bupt.xkc.historytoday.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bupt.xkc.historytoday.R;
import com.bupt.xkc.historytoday.models.ListModel;

import java.util.ArrayList;

/**
 * Created by xkc on 4/8/16.
 */
public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder>{
    private Context context;
    private ArrayList<ListModel> listModels;
    private LayoutInflater inflater;

    private OnItemClickListener listener;

    public EventListAdapter(Context context, ArrayList<ListModel> listModels){
        this.context = context;
        this.listModels = listModels;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_history_list,parent,false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ListModel event = listModels.get(position);
        holder.title_tv.setText(event.getTitle());
        holder.date_tv.setText(event.getDate());

        if (listener != null){
            holder.event_cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(holder.event_cv,position);
                    listener.onItemLongClick(holder.event_cv,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title_tv;
        private TextView date_tv;

        private CardView event_cv;

        public ViewHolder(View itemView) {
            super(itemView);
            title_tv = (TextView) itemView.findViewById(R.id.title);
            date_tv = (TextView) itemView.findViewById(R.id.date);
            event_cv = (CardView) itemView.findViewById(R.id.event_cv);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


}
