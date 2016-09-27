package com.example.viner.erosion;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
public class ViewHolder extends RecyclerView.ViewHolder {
    // Your holder should contain a member variable
    // for any view that will be set as you render a row

    public ImageButton img;
    public ImageButton caimera_sign;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ViewHolder(final View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.
        super(itemView);

        caimera_sign = (ImageButton)itemView.findViewById(R.id.caimera_sign);
        img = (ImageButton)itemView.findViewById(R.id.imgBtn);

    }
}
