package co.edu.univalle.www;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import co.edu.univalle.www.modelo.ProductSelectedListener;
import co.edu.univalle.www.modelo.ProductoServicio;
import co.edu.univalle.www.modelo.Sesion;

public class SearchFragment extends Fragment implements ProductSelectedListener {

    String TAG = "APPMSG(SearchFragment)";
    Sesion sesion;
    RecyclerViewAdapter adapter;
    ArrayList<ProductoServicio> arlProductosServicios;

    public SearchFragment(Sesion sesion) {
        this.sesion = sesion;
        arlProductosServicios = new ArrayList<>();
        adapter = new RecyclerViewAdapter(arlProductosServicios, sesion);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        // Inflate the layout for this fragment
        // data to populate the RecyclerView with

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.mainRecyclerView);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lm);

        adapter.addOnProductSelectedListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean scrolling = false;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolling = true;
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    if(!scrolling){
                        Log.d(TAG, "update");
                        update();
                    }
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                } else {
                    scrolling = false;
                }
            }
        });
        update();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(adapter);

        return view;
    }

    public void update(){
        arlProductosServicios.clear();
        ///////////////////////////////////////////////////////////////////
        //FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("productos_servicios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductoServicio productoServicio = new ProductoServicio();
                                productoServicio.setId(document.getId());
                                productoServicio.setNombre(document.getString("nombre"));
                                productoServicio.setDescripcion(document.getString("descripcion"));
                                productoServicio.setPrecio(document.getString("precio"));
                                productoServicio.setUsuario(document.getString("usuario"));
                                //se consulta la imagen
                                db.collection("previsualizaciones")
                                        .whereEqualTo("producto_servicio", document.getId())
                                        .limit(1)
                                        .get().addOnCompleteListener(
                                                new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document2 =task.getResult().getDocuments().get(0);
                                                            String encodedImage = document2.getString("imagen");
                                                            //Log.d(TAG, "enCodedImage: "+encodedImage + " id query " + document.getId());
                                                            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                                                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                                            productoServicio.setImagen(decodedByte);
                                                            productoServicio.setBytes(decodedString);
                                                            arlProductosServicios.add(productoServicio);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                        else{
                                                            Log.d(TAG, "Error loading pictures: ", task.getException());
                                                        }
                                                    }
                                                }
                                        );
                                //images.add(R.drawable.ic_baseline_filter_24);
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onProductSelected(ProductoServicio productoServicio) {
        Log.d("APPMGS(SearchFragment)", productoServicio.getId() + sesion.getLoggedUser());
        Intent intent = new Intent(getContext(), ViewProductService.class);

        intent.putExtra("id", productoServicio.getId());
        intent.putExtra("bytes", productoServicio.getBytes());
        intent.putExtra("nombre", productoServicio.getNombre());
        intent.putExtra("descripcion", productoServicio.getDescripcion());
        intent.putExtra("precio", productoServicio.getPrecio());
        intent.putExtra("usuario", productoServicio.getUsuario());
        startActivity(intent);
    }


}