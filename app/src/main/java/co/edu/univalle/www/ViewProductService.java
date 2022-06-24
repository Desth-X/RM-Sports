package co.edu.univalle.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import co.edu.univalle.www.modelo.ProductoServicio;

public class ViewProductService extends AppCompatActivity {

    SensorManager sensorManager;
    Sensor proximitySensor;
    ImageButton btnLike;

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

    SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // method to check accuracy changed in sensor.
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // check if the sensor type is proximity sensor.
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                if (event.values[0] == 0) {
                    Log.d("APPMSG(ViewProductService)", "Near");
                    btnLike.setVisibility(View.INVISIBLE);
                } else {
                    Log.d("APPMSG(ViewProductService)", "Away");
                    btnLike.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_service);

        // calling sensor service.
        sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        // from sensor service we are
        // calling proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // handling the case if the proximity
        // sensor is not present in users device.
        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // registering our sensor with sensor manager.
            sensorManager.registerListener(proximitySensorEventListener,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        ImageView image = findViewById(R.id.ivImagen);
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        TextView tvPrecio = findViewById(R.id.tvPrecio);
        btnLike = (ImageButton) findViewById(R.id.btnLike);

        ArrayList<String> arlTipos = new ArrayList<>();
        ArrayList<String> arlContactos = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.rvContactos);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerViewAdapterContacts rvAdapter = new RecyclerViewAdapterContacts(arlTipos, arlContactos);
        recyclerView.setAdapter(rvAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("bytes");
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        image.setImageBitmap(decodedByte);
        tvNombre.setText(intent.getStringExtra("nombre"));
        tvDescripcion.setText(intent.getStringExtra("descripcion"));
        tvPrecio.setText("$ "+intent.getStringExtra("precio")+" COP");

        ///////////////////////////////////////////////////////////////////
        //FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("contactos")
                .whereEqualTo("usuario", intent.getStringExtra("usuario"))
                .get().addOnCompleteListener(
                        new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for(DocumentSnapshot document: task.getResult().getDocuments()){
                                        String strTipoContacto = document.getString("tipo");
                                        String strDatoContacto = document.getString("dato_contacto");
                                        arlTipos.add(strTipoContacto);
                                        arlContactos.add(strDatoContacto);
                                    }
                                    rvAdapter.notifyDataSetChanged();
                                }
                                else{
                                    Log.d("APPMSG(ViewProductService)", "Error al cargar los contactos ", task.getException());
                                }
                            }
                        }
                );

        //Log.d("APPMSG(ViewProductService)", productoServicio.getId());
    }
}