package co.edu.univalle.www;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import co.edu.univalle.www.modelo.Sesion;


public class UserFragment extends Fragment {

    public UserFragment(Sesion sesion) {
        // Required empty public constructor
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
        Button btnCrearProducto = view.findViewById(R.id.btnCrearProducto);
        btnCrearProducto.setOnClickListener(view2 -> {
            Intent intent = new Intent(getContext(), CreateProduct.class);
            startActivity(intent);
        });
        return view;
    }


}