package com.templatemela.camscanner.adapter;

//adapter for recycle view
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.templatemela.camscanner.R;
import com.templatemela.camscanner.activity.MainActivity;
import com.templatemela.camscanner.models.DBModel;

import java.util.ArrayList;

public class AllGroupAdapter extends RecyclerView.Adapter<AllGroupAdapter.ViewHolder> {

    public Activity activity;
    public ArrayList<DBModel> arrayList;
    private String current_mode;

    //constructor
    public AllGroupAdapter(Activity activity2, ArrayList<DBModel> arrayList2, String str) {
        this.activity = activity2;
        this.arrayList = arrayList2;
        this.current_mode = str;
    }

    //this will update the data of adapter and notify the adpater that the data have been changed
    public void filterList(ArrayList<DBModel> arrayList2) {
        this.arrayList = arrayList2;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (current_mode.equals("Grid")) {
            view = LayoutInflater.from(activity).inflate(R.layout.group_item_grid, viewGroup, false);
        } else {
            view = LayoutInflater.from(activity).inflate(R.layout.group_item_list, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {

        //if no first image
        if (arrayList.get(i).getGroup_first_img() == null) {
            viewHolder.iv_group_folder_img.setImageResource(R.drawable.ic_folder_fill);
            viewHolder.iv_group_folder_img.setVisibility(View.VISIBLE);
            viewHolder.iv_group_first_img.setVisibility(View.GONE);
        } else if (arrayList.get(i).getGroup_first_img().isEmpty()) {
            viewHolder.iv_group_folder_img.setImageResource(R.drawable.ic_folder_fill);
            viewHolder.iv_group_folder_img.setVisibility(View.VISIBLE);
            viewHolder.iv_group_first_img.setVisibility(View.GONE);
        } else {
            viewHolder.iv_group_folder_img.setVisibility(View.GONE);
            viewHolder.iv_group_first_img.setVisibility(View.VISIBLE);

            //google library to load a image to a view
            Glide.with(activity)
                    .load(arrayList.get(i).getGroup_first_img())
                    .into(viewHolder.iv_group_first_img);

        }
        viewHolder.tv_group_name.setText(arrayList.get(i).getGroup_name());
        viewHolder.tv_group_date.setText(arrayList.get(i).getGroup_date());

        //add a onclick listner to the relative layout(of the list item) so that when clicked they are taken to the detailed view of the group
        viewHolder.rl_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) activity).clickOnListItem(((DBModel) arrayList.get(i)).getGroup_name());
            }
        });

        //onclick listener on the more button
        viewHolder.iv_group_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) activity).clickOnListMore(arrayList.get(i),((DBModel) arrayList.get(i)).getGroup_name(), ((DBModel) arrayList.get(i)).getGroup_date());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    //inner class
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_group_first_img;
        ImageView iv_group_more;
        ImageView iv_group_folder_img;
        RelativeLayout rl_group;
        TextView tv_group_date;
        TextView tv_group_name;

        public ViewHolder(View view) {
            super(view);
            rl_group = (RelativeLayout) view.findViewById(R.id.rl_group);
            iv_group_first_img = (ImageView) view.findViewById(R.id.iv_group_first_img);
            tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
            tv_group_date = (TextView) view.findViewById(R.id.tv_group_date);
            iv_group_more = (ImageView) view.findViewById(R.id.iv_group_more);
            iv_group_folder_img = (ImageView) view.findViewById(R.id.iv_group_folder_img);
        }
    }
}
