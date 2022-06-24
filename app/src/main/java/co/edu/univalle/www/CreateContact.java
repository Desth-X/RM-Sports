package co.edu.univalle.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.edu.univalle.www.modelo.ProductoServicio;

public class CreateContact extends AppCompatActivity {

    ArrayList<String> arlTipos;
    ArrayList<String> arlContactos;

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
        setContentView(R.layout.activity_create_contact);

        arlTipos = new ArrayList<>();
        arlContactos = new ArrayList<>();

        EditText etContacto = findViewById(R.id.etContacto);

        Spinner spTipoContacto = findViewById(R.id.spTipoContacto);
        String[] straTipos = new String[]{"Gmail","Whatsapp"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, straTipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoContacto.setAdapter(adapter);

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvContacts);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewAdapterContacts rvAdapter = new RecyclerViewAdapterContacts(arlTipos, arlContactos);
        recyclerView.setAdapter(rvAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        Button btnAgregarContacto = findViewById(R.id.btnAgregarContacto);
        btnAgregarContacto.setOnClickListener(view -> {
            String strContacto = etContacto.getText().toString().trim();
            String strTipoContacto = spTipoContacto.getSelectedItem().toString();
            if(!strContacto.equals("")){
                arlContactos.add(strContacto);
                arlTipos.add(strTipoContacto);
                etContacto.setText("");
                rvAdapter.notifyDataSetChanged();
            }
        });

        Button btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(view -> crearCuenta());


    }

    private void crearCuenta(){
        Map<String, Object> user = new HashMap<>();
        user.put("correo", getIntent().getStringExtra("correo"));
        user.put("nombre", getIntent().getStringExtra("nombre"));
        user.put("tipo", getIntent().getStringExtra("tipo"));
        user.put("contrasena", getIntent().getStringExtra("contrasena"));
        // Add a new document with a generated ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("APPMSG:" + "DocumentSnapshot added with ID: " + documentReference.getId());
                    //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    crearContactos(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    System.out.println("APPMSG: "+ "Error adding document " + e);
                    //Log.w(TAG, "Error adding document", e);
                });
    }

    private void crearContactos(String strUserID){
        for (int i = 0; i < arlContactos.size(); i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("usuario", strUserID);
            user.put("dato_contacto", arlContactos.get(i));
            user.put("tipo", arlTipos.get(i));
            // Add a new document with a generated ID
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("contactos")
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        System.out.println("APPMSG:" + "DocumentSnapshot added with ID: " + documentReference.getId());
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("APPMSG: "+ "Error adding document " + e);
                        //Log.w(TAG, "Error adding document", e);
                    });
        }

    }
}