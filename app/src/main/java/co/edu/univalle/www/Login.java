package co.edu.univalle.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    EditText etCorreo;
    EditText etContrasena;
    Button btnIniciarSesion;
    Button btnCrearCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnIniciarSesion.setOnClickListener(view -> iniciarSesion());
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(view -> crearCuenta());
    }

    private void iniciarSesion(){
        String strCorreo = etCorreo.getText().toString().trim();
        String strContrasena = etContrasena.getText().toString();

        if(strCorreo.equals("")){
            Toast.makeText(this,"Debe digitar un correo.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(strContrasena.equals("")){
            Toast.makeText(this,"Debe digitar una contraseña.",Toast.LENGTH_SHORT).show();
            return;
        }

        ///////////////////////////////////////////////////////////////////
        //FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        boolean existeUsuario = false;
        //se verifica que no exista el correo

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.d("APPMSG", "Error al tratar de cifrar la contraseña");
            Toast.makeText(getApplicationContext(),"No ha sido posible iniciar sesion.",Toast.LENGTH_SHORT).show();
            return;
        }
        String encodedContrasena = Base64.getEncoder().encodeToString(strContrasena.getBytes());

        db.collection("usuarios")
                .whereEqualTo("correo", strCorreo)
                .whereEqualTo("contrasena", encodedContrasena)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty()){
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String strId = documentSnapshot.getId();
                                Intent intent = new Intent();
                                intent.putExtra("id", strId);
                                if(null != getParent()){
                                    getParent().setResult(Activity.RESULT_OK, intent);
                                } else {
                                    setResult(Activity.RESULT_OK, intent);
                                }

                                finish();
                            } else{
                                Toast.makeText(getApplicationContext(),"Usuario o contraseña incorrectos.",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),"Error al tratar de iniciar sesion.",Toast.LENGTH_SHORT).show();
                            Log.d("APPMSG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void crearCuenta(){
        Intent intent = new Intent(this, CreateUser.class);
        startActivity(intent);
    }

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

}