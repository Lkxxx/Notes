package com.lk.notes.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lk.notes.ConvertTime;
import com.lk.notes.ImageProcessing;
import com.lk.notes.NotesDao;
import com.lk.notes.NotesInfo;
import com.lk.notes.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lk on 05/17.
 */
public class NotesAdapter extends BaseAdapter {
    private List<NotesInfo> mNotesInfos;
    private Context context;
    private NotesDao dao;
    private LayoutInflater inflater;
    private DisplayImageOptions options;
    int color;

    public NotesAdapter(Context context, List<NotesInfo> mNotesInfos, NotesDao dao, int i) {
        inflater = LayoutInflater.from(context);
        this.mNotesInfos = mNotesInfos;
        this.context = context;
        this.dao = dao;
        this.color = i;
        options = new DisplayImageOptions.Builder()

                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();
    }


    @Override
    public int getCount() {
        return mNotesInfos.size();
    }

    @Override
    public Object getItem(int i) {

        return mNotesInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        final ViewHolder holder;
        String id = mNotesInfos.get(i).getId();
        final File file = new File(Environment.getExternalStorageDirectory() + "/Notes/image/" + id);
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_notes, viewGroup, false);
            holder = new ViewHolder();
            holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
            holder.tv_text = (TextView) view.findViewById(R.id.tv_text);
            holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
            holder.imageView = (ImageView) view.findViewById(R.id.imageView);
            holder.tv_time.setTextColor(color);
            holder.tv_text.setVisibility(View.VISIBLE);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tv_text.setVisibility(View.VISIBLE);
        holder.tv_text.setText(mNotesInfos.get(i).getText());
        holder.tv_title.setText(mNotesInfos.get(i).getTitle());
        holder.tv_time.setText(ConvertTime.convertTime(mNotesInfos.get(i).getTime(), id));
        if (mNotesInfos.get(i).getText().isEmpty()) {
            holder.tv_text.setVisibility(View.GONE);
        }

        if (file.exists()) {
            holder.imageView.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            imageLoader.displayImage("file://" + Environment.getExternalStorageDirectory() + "/Notes/image/" + id,
                    holder.imageView, options, new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.imageView.setImageBitmap( new ImageProcessing().listImage(loadedImage));
                        }
                    });

        }else {
            holder.imageView.setVisibility(View.GONE);
        }

        return view;
    }

    static  class ViewHolder {
        TextView tv_title;
        TextView tv_text;
        ImageView imageView;
        TextView tv_time;

    }
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

}
