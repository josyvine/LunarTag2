package com.lunartag.app.ui.gallery;

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
import com.lunartag.app.R;
import com.lunartag.app.model.Photo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    private final Context context;
    private final List<Photo> photoList;
    private final SimpleDateFormat timeFormat;

    public GalleryAdapter(Context context, List<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
        // Formatter for displaying time in AM/PM format
        this.timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_thumbnail, parent, false);
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo currentPhoto = photoList.get(position);

        // Set the timestamp and status text
        holder.timestampTextView.setText(timeFormat.format(currentPhoto.getAssignedTimestamp()));
        holder.statusTextView.setText(currentPhoto.getStatus());

        // Use Glide to load the image file into the ImageView
        File imageFile = new File(currentPhoto.getFilePath());
        if (imageFile.exists()) {
            Glide.with(context)
                    .load(Uri.fromFile(imageFile))
                    .into(holder.thumbnailImageView);
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    /**
     * The ViewHolder class holds references to the UI views for a single list item.
     */
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        final ImageView thumbnailImageView;
        final TextView timestampTextView;
        final TextView statusTextView;

        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.image_thumbnail);
            timestampTextView = itemView.findViewById(R.id.text_thumbnail_timestamp);
            statusTextView = itemView.findViewById(R.id.text_thumbnail_status);
        }
    }
    }
