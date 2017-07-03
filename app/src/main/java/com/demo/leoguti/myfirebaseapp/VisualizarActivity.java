package com.demo.leoguti.myfirebaseapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.demo.leoguti.myfirebaseapp.Model.User;
import com.demo.leoguti.myfirebaseapp.Varios.ValoresFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VisualizarActivity extends AppCompatActivity {

    //region Controles
    Button   btnSave;
    EditText txtName;
    EditText txtLastName;
    EditText txtEmail;
    EditText txtUserName;
    EditText txtPassword;
    //endregion endControles

    User userCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar);

        AsignarControles();
        ObtenerDatosVisualizacion();
    }

    private void AsignarControles() {

        Toolbar toolbar = (Toolbar)findViewById(R.id.iniToolBar);
        toolbar.setTitle(R.string.TitleActView);

        btnSave = (Button) findViewById(R.id.btnSave);
        txtName=(EditText)  findViewById(R.id.txtName);
        txtLastName=(EditText)  findViewById(R.id.txtLastName);
        txtEmail=(EditText)  findViewById(R.id.txtEmail);
        txtUserName=(EditText)  findViewById(R.id.txtUserName);
        txtPassword=(EditText)  findViewById(R.id.txtPassword);
        txtUserName.setEnabled(false);
    }

    private void ObtenerDatosVisualizacion() {

        Intent intent = getIntent();
        userCurrent = (User) intent.getSerializableExtra("user");

        //Asigna los valores a los controles
        txtName.setText(userCurrent.getNombre());
        txtLastName.setText(userCurrent.getApellido());
        txtEmail.setText(userCurrent.getCorreo());
        txtUserName.setText(userCurrent.getUsername());
        txtPassword.setText(userCurrent.getPassword());
        //
    }

    public void onSaveUser(View view) {
        //region Se setean los valores ingresados
        final User newUser = new User();
        newUser.setNombre(txtName.getText().toString());
        newUser.setApellido(txtLastName.getText().toString());
        newUser.setCorreo(txtEmail.getText().toString());
        newUser.setUsername(userCurrent.getUsername());//Este valor no se puede modificar
        newUser.setPassword(txtPassword.getText().toString());
        //endregion Se setean los valores ingresados

        //region Se guarda en la base de datos de firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference usuariosRef = db.getReference(ValoresFireBase.REFERENCE_USUARIOS);
        usuariosRef.child(newUser.getUsername()).setValue(newUser);
        //endregion Se guarda en la base de datos de firebase

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
