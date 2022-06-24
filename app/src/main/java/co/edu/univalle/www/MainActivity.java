package co.edu.univalle.www;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import co.edu.univalle.www.modelo.Sesion;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResultLauncher;
    boolean isLogged = false;
    SearchFragment searchFragment;
    UserFragment userFragment;
    Sesion sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sesion = new Sesion();

        searchFragment = new SearchFragment(sesion);
        userFragment = new UserFragment(sesion);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        sesion.setLoggedUser(data.getStringExtra("id"));
                        System.out.println("APPMSG: "+ "LOGGED " + sesion.getLoggedUser());
                        isLogged = true;
                        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                        bottomNavigationView.setSelectedItemId(R.id.user);
                    }
                });



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // as soon as the application opens the first
        // fragment should be shown to the user
        // in this case it is algorithm fragment
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container, searchFragment, "1").commit();
        fm.beginTransaction().add(R.id.fragment_container, userFragment, "2").hide(userFragment).commit();
        //fm.beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //Fragment selectedFragment = null;
            FragmentManager fm = getSupportFragmentManager();
            switch (item.getItemId()) {
                case R.id.search:
                    //selectedFragment = searchFragment;
                    fm.beginTransaction().show(searchFragment).hide(userFragment).commit();
                    break;
                case R.id.user:
                    if (isLogged){
                        fm.beginTransaction().hide(searchFragment).show(userFragment).commit();
                    } else{
                        Intent login = new Intent(getApplicationContext(), Login.class);
                        activityResultLauncher.launch(login) ;
                    }
                    break;
            }
            return true;
        }
    };

}
