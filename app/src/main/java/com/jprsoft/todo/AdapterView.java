package com.jprsoft.todo;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AdapterView extends RecyclerView.Adapter<AdapterHolder> {

    Homepage homepage;
    ArrayList<Model> list = new ArrayList<>();

    public AdapterView(Homepage homepage, ArrayList<Model> list) {
        this.homepage = homepage;
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(homepage.getBaseContext());
        View view = inflater.inflate(R.layout.list_item, viewGroup,false);
        return new AdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterHolder adapterHolder, final int i) {
        adapterHolder.title.setText(list.get(i).getTitle());
        adapterHolder.description.setText(list.get(i).getDescription());

        adapterHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homepage.title.setText(list.get(i).getTitle());
                homepage.description.setText(list.get(i).getDescription());
                homepage.isUpdate = true;
                homepage.idUpdate = list.get(i).getId();
            }
        });

        final PopupMenu dropDownMenu = new PopupMenu(homepage.getBaseContext(), adapterHolder.cv);
        final Menu menu = dropDownMenu.getMenu();

        adapterHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dropDownMenu.getMenuInflater().inflate(R.menu.long_click, menu);
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.long_complete:
                                boolean check = homepage.updateStatus(list.get(i).getId(), list.get(i).getTitle(), list.get(i).getDescription());
                                dropDownMenu.dismiss();
                                return true;
                            case R.id.long_delete:
                                homepage.deleteData(list.get(i).getId());
                                dropDownMenu.dismiss();
                                return true;
                        }
                        return false;
                    }
                });
                dropDownMenu.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
