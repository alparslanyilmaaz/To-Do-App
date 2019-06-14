package com.jprsoft.todo;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class AdapterHolder extends RecyclerView.ViewHolder {

    TextView title, description;
    CardView cv;

    public AdapterHolder(@NonNull View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.item_title);
        description = (TextView) itemView.findViewById(R.id.item_description);
        cv = (CardView) itemView.findViewById(R.id.total);
    }
}
