package co.edu.univalle.www;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import co.edu.univalle.www.modelo.ProductoServicio;
import co.edu.univalle.www.modelo.Sesion;

public class CreatedProductsServicesFragment extends Fragment {

    RecyclerViewAdapter adapter;
    MainActivity mainActivity;
    ArrayList<ProductoServicio> arlProductosServicios;
    Sesion sesion;
    ActivityResultLauncher<Intent> activityResultLauncher;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreatedProductsServicesFragment(Sesion sesion, MainActivity mainActivity) {
        this.sesion = sesion;
        this.mainActivity = mainActivity;
        arlProductosServicios = new ArrayList<>();
        adapter = new RecyclerViewAdapter(arlProductosServicios, sesion);
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_created_products_services, container, false);



        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rvCreatedProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.addOnProductSelectedListener(mainActivity.userFragment);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setAdapter(adapter);
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
                        Log.d("APPMSG(CreatedProductsServicesFragment)", "update");
                        update();
                        mainActivity.searchFragment.update();
                    }
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                } else {
                    scrolling = false;
                }
            }
        });

        FloatingActionButton btnCrearProducto = view.findViewById(R.id.fbtnCreateProduct);
        btnCrearProducto.setOnClickListener(view2 -> {
            Intent intent = new Intent(getContext(), CreateProduct.class);
            intent.putExtra("usuario", sesion.getLoggedUser());
            activityResultLauncher.launch(intent);
        });

        return view;

    }

    public void notifyLogout(){
        arlProductosServicios.clear();
        adapter.notifyDataSetChanged();
    }

    public void update(){
        arlProductosServicios.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("productos_servicios")
                .whereEqualTo("usuario", sesion.getLoggedUser())
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
                                                            Log.d("APPMSG(UserFragment)", "Error loading pictures: ", task.getException());
                                                        }
                                                    }
                                                }
                                        );
                                //images.add(R.drawable.ic_baseline_filter_24);
                            }


                        } else {
                            Log.d("APPMSG(UserFragment)", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}