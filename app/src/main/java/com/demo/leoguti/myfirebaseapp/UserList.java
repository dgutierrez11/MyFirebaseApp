package com.demo.leoguti.myfirebaseapp;

import android.content.Intent;
import android.database.SQLException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.demo.leoguti.myfirebaseapp.Model.User;
import com.demo.leoguti.myfirebaseapp.View.CustomAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

public class UserList extends AppCompatActivity {

    private final static String TAG = UserList.class.getSimpleName();
    ArrayList<User> dataModels;
    private ListView listaRegistros;
    private static CustomAdapter adapter;
    static final int CREATE_ALERT_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_toolbar_list);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivityForResult(intent, CREATE_ALERT_REQUEST);
            }
        });

        listaRegistros = (ListView) findViewById(R.id.lista_registro);
        dataModels= new ArrayList<>();

        User user1 = new User();
        user1.setNombre("David");
        dataModels.add(user1);

        List<String> listaUsernames = new ArrayList<>();
        listaUsernames.add("leoguti11");
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaUsernames);

        listaRegistros.setAdapter(adapter);
       /*
        adapter = new CustomAdapter(dataModels,getApplicationContext());
        listaRegistros.setAdapter(adapter);*/
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
     /*   if (requestCode == CREATE_ALERT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                //startActivity(new Intent(Intent.ACTION_VIEW, data));
                Alerta alerta = (Alerta) data.getSerializableExtra("newAlert");
                dataModels.add(alerta);
                adapter.notifyDataSetChanged();
            }
        }*/
    }

    public void onDeleteAlerta(View view){
        Toast toast = Toast.makeText(getApplicationContext(), "Delete", Toast.LENGTH_SHORT);
        toast.show();
    }
}
