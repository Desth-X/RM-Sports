package co.edu.univalle.www;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link userCreate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class userCreate extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    EditText etCorreo;
    EditText etNombre;
    Spinner spTipo;
    EditText etContrasena;
    Button btnCrearCuenta;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public userCreate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment create_user.
     */
    // TODO: Rename and change types and number of parameters
    public static userCreate newInstance(String param1, String param2) {
        userCreate fragment = new userCreate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_create, container, false);
        etCorreo = view.findViewById(R.id.etCorreo);
        etNombre = view.findViewById(R.id.etNombre);
        spTipo = view.findViewById(R.id.spTipo);
        String[] straTipos = new String[]{"Estandar","Negocio"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, straTipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        etContrasena = view.findViewById(R.id.etContrasena);
        btnCrearCuenta = view.findViewById(R.id.btnCrearCuenta);
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearCuenta();
            }
        });
        return view;

    }

    private void crearCuenta(){
        try{
            String strCorreo = etCorreo.getText().toString();
            String strNombre = etNombre.getText().toString();
            String strTipo = spTipo.getSelectedItem().toString();
            String strContrasena = etContrasena.getText().toString();
            System.out.println("APPMSG: "+strCorreo+" "+strNombre+" "+strTipo + " "+strContrasena);
        }
        catch (Exception ex){
            System.out.println("APPMSG: "+ ex);
        }
    }
}