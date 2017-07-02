package com.demo.leoguti.myfirebaseapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.demo.leoguti.myfirebaseapp.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText txtName;
    EditText txtLastName;
    EditText txtEmail;
    EditText txtUsername;
    EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.iniToolBar);
        toolbar.setTitle(R.string.registration);
        txtName = (EditText) findViewById(R.id.txtName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtUsername = (EditText) findViewById(R.id.txtUserName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
    }

    public void onSaveUser(View view) {

        final User newUser = new User();
        newUser.setNombre(txtName.getText().toString());
        newUser.setApellido(txtLastName.getText().toString());
        newUser.setCorreo(txtEmail.getText().toString());
        newUser.setUsername(txtUsername.getText().toString());
        newUser.setPassword(txtPassword.getText().toString());

        // Se guarda en la base de datos de firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(newUser.getUsername());
        ref.setValue(newUser);

        // Se muestra mensaje de exito y se devuelve el registro creado
        // para que se muestre en la lista inicial
        new AlertDialog.Builder(this).setMessage(R.string.message_create_record)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Return data
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("newUser", newUser);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }).create().show();

    }
}
