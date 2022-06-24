package co.edu.univalle.www;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.edu.univalle.www.modelo.ProductSelectedListener;
import co.edu.univalle.www.modelo.ProductoServicio;
import co.edu.univalle.www.modelo.Sesion;


public class UserFragment extends Fragment implements ProductSelectedListener {

    Sesion sesion;
    TextView tvNombre;
    TextView tvCorreo;
    MainActivity mainActivity;
    CreatedProductsServicesFragment createdProductsServicesFragment;
    LikedProductsServicesFragment likedProductsServicesFragment;


    public UserFragment(Sesion sesion, MainActivity mainActivity) {
        this.sesion = sesion;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        tvNombre = view.findViewById(R.id.tvNombre);
        tvCorreo = view.findViewById(R.id.tvCorreo);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(view2 -> {
            sesion.logOut();
            createdProductsServicesFragment.notifyLogout();
            BottomNavigationView bottomNavigationView = mainActivity.findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.search);
        });

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Creados"));
        tabLayout.addTab(tabLayout.newTab().setText("Favoritos"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        ViewPager viewPager =(ViewPager) view.findViewById(R.id.viewPager);

        createdProductsServicesFragment = new CreatedProductsServicesFragment(sesion, mainActivity);
        likedProductsServicesFragment = new LikedProductsServicesFragment();

        FragmentPagerAdapter fm = new FragmentPagerAdapter(getFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return createdProductsServicesFragment;
                    case 1:
                        return likedProductsServicesFragment;
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
        viewPager.setAdapter(fm);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(sesion.isLogged()){
            update();
        }

        return view;
    }

    public void update(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .document(sesion.getLoggedUser())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                tvNombre.setText(document.getString("nombre"));
                                tvCorreo.setText(document.getString("correo"));
                                createdProductsServicesFragment.update();
                                Log.d("APPMSG(UserFragment)", "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d("APPMSG(UserFragment)", "No such document");
                            }
                        } else {
                            Log.d("APPMSG(UserFragment)", "get failed with ", task.getException());
                        }
                    }
                });

        ///////////////////////////////////////////////////////////////////


    }

    @Override
    public void onProductSelected(ProductoServicio productoServicio) {

    }
}