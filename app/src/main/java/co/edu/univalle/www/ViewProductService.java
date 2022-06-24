package co.edu.univalle.www;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import co.edu.univalle.www.modelo.ProductoServicio;

public class ViewProductService extends AppCompatActivity {

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
        setContentView(R.layout.activity_view_product_service);

        ImageView image = findViewById(R.id.ivImagen);
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvDescripcion = findViewById(R.id.tvDescripcion);
        TextView tvPrecio = findViewById(R.id.tvPrecio);

        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra("bytes");
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        image.setImageBitmap(decodedByte);
        tvNombre.setText(intent.getStringExtra("nombre"));
        tvDescripcion.setText(intent.getStringExtra("descripcion"));
        tvPrecio.setText(intent.getStringExtra("precio"));

        //Log.d("APPMSG(ViewProductService)", productoServicio.getId());
    }
}