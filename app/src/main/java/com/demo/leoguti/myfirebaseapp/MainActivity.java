package com.demo.leoguti.myfirebaseapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.demo.leoguti.myfirebaseapp.Model.User;
import com.demo.leoguti.myfirebaseapp.Varios.Accion;
import com.demo.leoguti.myfirebaseapp.Varios.ValoresFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText txtName;
    EditText txtLastName;
    EditText txtEmail;
    EditText txtUsername;
    EditText txtPassword;

    Accion AccionCurrent;
    User userCurrent;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.iniToolBar);


        txtName = (EditText) findViewById(R.id.txtName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtUsername = (EditText) findViewById(R.id.txtUserName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        ObtenerDatosVisualizacion();
    }

    private void ObtenerDatosVisualizacion() {

        Intent intent = getIntent();
        userCurrent = (User) intent.getSerializableExtra("user");
        AccionCurrent =(Accion) intent.getSerializableExtra("accion");

        switch (AccionCurrent)
        {
            case Consultar:
                //Asigna los valores a los controles
                txtName.setText(userCurrent.getNombre());
                txtLastName.setText(userCurrent.getApellido());
                txtEmail.setText(userCurrent.getCorreo());
                txtUsername.setText(userCurrent.getUsername());
                txtPassword.setText(userCurrent.getPassword());
                txtUsername.setEnabled(false);
                //
                toolbar.setTitle(R.string.TitleActView);
                break;
            default:
                toolbar.setTitle(R.string.registration);
                break;
        }
    }

    public void onSaveUser(View view) {
        //region Se setean los valores ingresados
        final User newUser = new User();
        newUser.setNombre(txtName.getText().toString());
        newUser.setApellido(txtLastName.getText().toString());
        newUser.setCorreo(txtEmail.getText().toString());
        newUser.setUsername(txtUsername.getText().toString());
        newUser.setPassword(txtPassword.getText().toString());
        //endregion Se setean los valores ingresados

        //region Se realizan las validaciones a la información ingresada
        String res = ValidateValues(newUser);
        if (res != "") {
            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
            return;
        }
        //endregion Se realizan las validaciones a la información ingresada

        //region Se guarda en la base de datos de firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference usuariosRef = db.getReference(ValoresFireBase.REFERENCE_USUARIOS);
        usuariosRef.child(newUser.getUsername()).setValue(newUser);
        //endregion Se guarda en la base de datos de firebase

        // Se muestra mensaje de exito y se devuelve el registro creado
        // para que se muestre en la lista inicial

        int message = R.string.message_create_record;
        if(AccionCurrent== Accion.Consultar)
        {
            message = R.string.message_update_record;
        }

        new AlertDialog.Builder(this).setMessage(message)
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

    private String ValidateValues(User user) {

        if (user.getUsername().length()==0)
            return getString(R.string.req_username);

        if (user.getUsername().length()>30)
            return getString(R.string.validate_username);

        if(user.getNombre().length()>60)
            return getString(R.string.validate_name);

        if(user.getApellido().length()>60)
            return getString(R.string.validate_lastname);

        if(user.getCorreo().length()>120)
            return getString(R.string.validate_email);

        if(user.getPassword().length()>20)
            return getString(R.string.validate_password);

        return "";
    }
}
