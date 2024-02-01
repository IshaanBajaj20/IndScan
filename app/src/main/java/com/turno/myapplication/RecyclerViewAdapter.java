package com.turno.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private OnNoteListener mOnNoteListener;

    RecyclerViewAdapter(Context mContext, OnNoteListener onNoteListener){
        this.mContext = mContext;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list,parent,false);
        return new FileLayoutHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FileLayoutHolder)holder).videoTitle.setText(Constant.allMediaList.get(position).getName());
        //we will load thumbnail using glid library

    }

    @Override
    public int getItemCount() {
        return Constant.allMediaList.size();
    }

    class FileLayoutHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView thumbnail;
        TextView videoTitle;
        ImageView ic_more_btn;
        OnNoteListener onNoteListener;

        public FileLayoutHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoTitle = itemView.findViewById(R.id.videotitle);
            ic_more_btn = itemView.findViewById(R.id.ic_more_btn);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());

        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }

}
