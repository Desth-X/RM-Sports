package co.edu.univalle.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CreateUser extends AppCompatActivity {

    EditText etCorreo;
    EditText etNombre;
    Spinner spTipo;
    EditText etContrasena;
    EditText etContrasena2;
    Button btnCrearCuenta;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Finalizar la actividad cuando se presione atras
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        etCorreo = findViewById(R.id.etCorreo);
        etNombre = findViewById(R.id.etNombre);
        spTipo = findViewById(R.id.spTipo);
        String[] straTipos = new String[]{"Estandar","Negocio"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, straTipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        etContrasena = findViewById(R.id.etContrasena);
        etContrasena2 = findViewById(R.id.etContrasena2);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(view -> crearCuenta());

    }

    private void crearCuenta(){
        try{
            String strCorreo = etCorreo.getText().toString().trim();
            String strNombre = etNombre.getText().toString().trim();
            String strTipo = spTipo.getSelectedItem().toString().trim();
            String strContrasena = etContrasena.getText().toString();
            String strContrasena2 = etContrasena2.getText().toString();
            System.out.println("APPMSG: "+strCorreo+" "+strNombre+" "+strTipo + " "+strContrasena);

            if(strCorreo.equals("")){
                Toast.makeText(this,"Debe digitar un correo.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(strNombre.equals("")){
                Toast.makeText(this,"Debe digitar un nombre.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(strTipo.equals("")){
                Toast.makeText(this,"Debe indicar un tipo.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(strContrasena.equals("")){
                Toast.makeText(this,"Debe digitar una contraseña.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!strContrasena.equals(strContrasena2)){
                Toast.makeText(this,"Las contraseñas no coinciden.",Toast.LENGTH_SHORT).show();
                return;
            }
            ///////////////////////////////////////////////////////////////////
            //FirebaseApp.initializeApp(this);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            boolean existeUsuario = false;
            //se verifica que no exista el correo
            db.collection("usuarios")
                    .whereEqualTo("correo", strCorreo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(!task.getResult().isEmpty()){
                                    Toast.makeText(getApplicationContext(),"El Usuario ya se encuentra registrado.",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    ///////////////////////////////////////////////////////////////////
                                    // Create a new user with a first and last name
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("correo", strCorreo);
                                    user.put("nombre", strNombre);
                                    user.put("tipo", strTipo);

                                    MessageDigest md = null;
                                    try {
                                        md = MessageDigest.getInstance("MD5");
                                    } catch (NoSuchAlgorithmException e) {
                                        Log.d("APPMSG", "Error al tratar de cifrar la contraseña");
                                        Toast.makeText(getApplicationContext(),"No ha sido posible crear la cuenta.",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String encodedContrasena = Base64.getEncoder().encodeToString(strContrasena.getBytes());

                                    user.put("contrasena", encodedContrasena);
                                    // Add a new document with a generated ID
                                    db.collection("usuarios")
                                            .add(user)
                                            .addOnSuccessListener(documentReference -> {
                                                System.out.println("APPMSG:" + "DocumentSnapshot added with ID: " + documentReference.getId());
                                                //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            })
                                            .addOnFailureListener(e -> {
                                                System.out.println("APPMSG: "+ "Error adding document " + e);
                                                //Log.w(TAG, "Error adding document", e);
                                            });
                                    ///////////////////////////////////////////////////////////////////
                                    finish();
                                    Toast.makeText(getApplicationContext(),"Usuario Creado con exito.",Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),"Error al consultar la base de datos.",Toast.LENGTH_SHORT).show();
                                Log.d("APPMSG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        catch (Exception ex){
            System.out.println("APPMSG: "+ ex);
        }
    }
}