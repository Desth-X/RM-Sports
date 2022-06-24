package co.edu.univalle.www;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.edu.univalle.www.modelo.ProductSelectedListener;
import co.edu.univalle.www.modelo.ProductoServicio;
import co.edu.univalle.www.modelo.Sesion;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private ArrayList<ProductoServicio> arlProductosServicios;

        ArrayList<ProductSelectedListener> listeners;

        Sesion sesion;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView name;
            private final ImageView image;
            private final TextView precio;
            private ProductoServicio productoServicio;

            public ViewHolder(Sesion sesion, View view, ArrayList<ProductSelectedListener> listeners) {
                super(view);
                // Define click listener for the ViewHolder's View
                productoServicio = new ProductoServicio();
                view.setOnClickListener(srcView -> {
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).onProductSelected(productoServicio);
                    }
                });
                name = view.findViewById(R.id.name);
                image = view.findViewById(R.id.image);
                precio = view.findViewById(R.id.precio);
            }

            public void setProductoServicio(ProductoServicio productoServicio) {
                this.productoServicio = productoServicio;
                name.setText(productoServicio.getNombre());
                precio.setText("$ " + productoServicio.getPrecio() + " COP");
                try{
                    image.setImageBitmap(productoServicio.getImagen());
                }catch (Exception ex){
                    image.setImageResource(R.drawable.ic_baseline_filter_24);
                }
            }
        }


        public RecyclerViewAdapter(ArrayList<ProductoServicio> arlProductoServicios, Sesion sesion) {
            this.sesion = sesion;
            this.arlProductosServicios = arlProductoServicios;
            listeners = new ArrayList<>();
        }

        public void addOnProductSelectedListener(ProductSelectedListener listener){
            listeners.add(listener);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row, viewGroup, false);
            return new ViewHolder(sesion, view, listeners);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            ProductoServicio productoServicio = arlProductosServicios.get(position);
            viewHolder.setProductoServicio(productoServicio);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return arlProductosServicios.size();
        }


}
