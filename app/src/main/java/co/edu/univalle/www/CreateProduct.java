package co.edu.univalle.www;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateProduct extends AppCompatActivity {

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

    ActivityResultLauncher activityResultLauncher;

    EditText etNombre;
    EditText etDescripcion;
    EditText etPrecio;
    Spinner spTipo;
    FloatingActionButton btnSeleccionarImagen;
    TextView tvImagenesSeleccionadas;
    Button btnPublicar;
    ArrayList<String> arlEncodedImagenes;

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        arlEncodedImagenes = new ArrayList<String>();
                        ClipData clipData = result.getData().getClipData();
                        System.out.println("APPMSG: Items selected "+clipData.getItemCount());
                        for(int i = 0; i < clipData.getItemCount();i++){
                            Uri uri = clipData.getItemAt(i).getUri();
                            System.out.println("APPMSG uri" +uri.getPath());
                            InputStream iStream = null;
                            try {
                                iStream = getContentResolver().openInputStream(uri);
                                byte[] inputData = getBytes(iStream);

                                String encodedImage = Base64.encodeToString(inputData, Base64.DEFAULT);
                                arlEncodedImagenes.add(encodedImage);
                                tvImagenesSeleccionadas.setText(String.valueOf(arlEncodedImagenes.size()) + " Imagenes Seleccionadas");

                            } catch (FileNotFoundException e) {
                                Log.d("APGMSG", "Error al leer bytes " + e);
                                e.printStackTrace();
                            } catch (IOException e) {
                                Log.d("APGMSG", "Error al leer bytes 2 " + e);
                            } finally {
                                // close the stream
                                try{ iStream.close(); } catch (IOException ignored){ /* do nothing */ }
                            }
                        }
                    }
                });

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        spTipo = findViewById(R.id.spTipo);
        String[] straTipos = new String[]{"Producto","Servicio"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, straTipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarImagen.setOnClickListener(view -> cargarImagenes());
        tvImagenesSeleccionadas = findViewById(R.id.tvImagenesSeleccionadas);
        btnPublicar = findViewById(R.id.btnPublicar);
        btnPublicar.setOnClickListener(view -> publicar());
    }

    private void cargarImagenes(){
        Log.d("APPMSG", "cargarImagenes: ");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activityResultLauncher.launch(intent);
    }

    private void publicar(){
        String strNombre = etNombre.getText().toString().trim();
        String strDescripcion = etDescripcion.getText().toString().trim();
        String strPrecio = etPrecio.getText().toString().trim();
        String strTipo = spTipo.getSelectedItem().toString();
        if(strNombre.equals("")){
            Toast.makeText(this,"Debe digitar un nombre.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(strDescripcion.equals("")){
            Toast.makeText(this,"Debe digitar una Descripcion.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(strPrecio.equals("")){
            Toast.makeText(this,"Debe digitar un precio.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(strTipo.equals("")){
            Toast.makeText(this,"Debe indicar un tipo.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(arlEncodedImagenes == null){
            Toast.makeText(this,"Debe adjuntar al menos una imagen.",Toast.LENGTH_SHORT).show();
            return;
        }
        ///////////////////////////////////////////////////////////////////
        //FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", strNombre);
        user.put("descripcion", strDescripcion);
        user.put("precio", strPrecio);
        user.put("usuario", getIntent().getStringExtra("usuario"));
        user.put("tipo", strTipo);
        // Add a new document with a generated ID
        db.collection("productos_servicios")
                .add(user)
                .addOnSuccessListener(documentReference -> {

                    String id = documentReference.getId();
                    Log.d("APPMSG:", "DocumentSnapshot added with ID: " + id);
                    //se agregan las imagenes
                    for (int i = 0; i < arlEncodedImagenes.size(); i++) {
                        Map<String, Object> image = new HashMap<>();
                        image.put("imagen", arlEncodedImagenes.get(i));
                        image.put("url", "Not implemented");
                        image.put("producto_servicio", id);
                        db.collection("previsualizaciones")
                                .add(image)
                                .addOnSuccessListener(documentReference2 -> {
                                })
                                .addOnFailureListener((e -> {
                                    System.out.println("APPMSG: " + "Error adding document (Image) " + e);
                                    return;
                                    //Log.w(TAG, "Error adding document", e);
                                }));
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("APPMSG: "+ "Error adding document " + e);
                    //Log.w(TAG, "Error adding document", e);
                });
        ///////////////////////////////////////////////////////////////////
        setResult(Activity.RESULT_OK);
        finish();
        Toast.makeText(getApplicationContext(),strTipo+" Creado con exito.",Toast.LENGTH_SHORT).show();


    }
}