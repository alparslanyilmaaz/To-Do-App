package com.jprsoft.todo;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class Homepage extends AppCompatActivity {

    FirebaseFirestore db;

    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    public MaterialEditText title, description;
    private BottomNavigationView bottomNav;

    ArrayList<Model> arrayList;
    AdapterView adapter;
    AlertDialog alertDialog;

    public String idUpdate = "";
    public boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        title = (MaterialEditText) findViewById(R.id.title);
        description = (MaterialEditText) findViewById(R.id.description);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        bottomNav = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNav.setOnNavigationItemSelectedListener(navlistener);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isUpdate){
                    setData(title.getText().toString(), description.getText().toString());
                    title.setText("");
                    description.setText("");
                }
                else{
                    isUpdate = !isUpdate;
                    updateData(title.getText().toString(), description.getText().toString());
                    title.setText("");
                    description.setText("");
                }
            }
        });
        listItem = (RecyclerView) findViewById(R.id.lisTodo);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>();
        loadUncompletedData();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navlistener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selected = null;

            switch (menuItem.getItemId()){
                case R.id.nav_comp:
                    loadCompletedData();
                    break;
                case R.id.nav_all_data:
                    loadData();
                    break;
                case R.id.nav_uncomp:
                    loadUncompletedData();
                    break;
            }
            return true;
        }
    };

    public void deleteData(String id){
        db.collection("ToDoList").document(String.valueOf(id)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Homepage.this, "Data deleted.", Toast.LENGTH_SHORT).show();
                loadData();
            }
        });
    }

    private void updateData(String tit, String desc) {
        db.collection("ToDoList").document(idUpdate)
                .update("title", tit, "description", desc, "status", "uncompleted")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Homepage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setData(final String title, String desc) {

        String id = UUID.randomUUID().toString();
        Map<String, Object> todo = new HashMap<>();

        todo.put("id", id);
        todo.put("title", title);
        todo.put("description", desc);
        todo.put("status", "uncompleted");

        db.collection("ToDoList").document(id)
                .set(todo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    private void loadData(){
        alertDialog.show();
        if(arrayList.size()>0){
            arrayList.clear();
        }
        db.collection("ToDoList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc:task.getResult()){
                            Model model = new Model(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("description"));
                            arrayList.add(model);
                        }
                        adapter = new AdapterView(Homepage.this, arrayList);
                        listItem.setAdapter(adapter);
                        alertDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Homepage.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUncompletedData(){
        alertDialog.show();
        if(arrayList.size()>0){
            arrayList.clear();
        }
        db.collection("ToDoList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    if(doc.get("status").equals("uncompleted")){
                        Model model = new Model(doc.getString("id"),
                                doc.getString("title"),
                                doc.getString("description"));
                        arrayList.add(model);
                    }
                }
                adapter = new AdapterView(Homepage.this, arrayList);
                listItem.setAdapter(adapter);
                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Homepage.this, "Went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCompletedData() {
        alertDialog.show();
        if(arrayList.size()>0){
            arrayList.clear();
        }
        db.collection("ToDoList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult()){
                    if(doc.get("status").equals("completed")){
                        Model model = new Model(doc.getString("id"),
                                doc.getString("title"),
                                doc.getString("description"));
                        arrayList.add(model);
                    }
                }
                adapter = new AdapterView(Homepage.this, arrayList);
                listItem.setAdapter(adapter);
                alertDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Homepage.this, "Went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean updateStatus(String id, String tit, String desc){
        db.collection("ToDoList").document(id)
                .update("title", tit, "description", desc, "status", "completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadCompletedData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Homepage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
        return true;
    }
}
