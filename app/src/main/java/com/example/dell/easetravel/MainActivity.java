package com.example.dell.easetravel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.NotNull;

import io.ghyeok.stickyswitch.widget.StickySwitch;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth authentication;
    DatabaseReference ref;
    FirebaseDatabase db;

    Button start, go;

    String Switch_Info = "Driver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences("PREFS", 0);
        String logout = settings.getString("logout", "");
        if(logout.equals("yes"))
            restoreUser();
        else
            setCurrentUser();

        start = (Button) findViewById(R.id.start);

        go = (Button) findViewById(R.id.go);
        StickySwitch switchh = (StickySwitch) findViewById(R.id.sticky_switch);

        switchh.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String s) {
                Switch_Info = s;
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;

                if(Switch_Info.equals("Passenger"))
                    intent = new Intent(getApplicationContext(), RegisterPassenger.class);
                else
                    intent = new Intent(getApplicationContext(), RegisterDriver.class);

                startActivity(intent);
                finish();

            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( ! (LoadMain()) )
                {
                    Intent intent = new Intent(getApplicationContext(), Login.class);

                    if(Switch_Info.equals("Passenger"))
                        intent.putExtra("status", "Passenger");
                    else
                        intent.putExtra("status", "Driver");

                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    public void setCurrentUser()
    {
        authentication = FirebaseAuth.getInstance();
        user = authentication.getCurrentUser();
    }
    public boolean LoadMain()
    {
        authentication = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        if(user == null)
            return false;
        else
        {
            Intent intent = null;

            final String username = getSplit(user.getEmail());

            SharedPreferences settings = getSharedPreferences("PREFS", 0);
            String pass_driv = settings.getString("passenger_driver", "");

            if(pass_driv.equals("passenger"))
            {
                intent = new Intent(getApplicationContext(), Passenger.class);
            }
            else if(pass_driv.equals("driver"))
            {
                intent = new Intent(getApplicationContext(), Driver.class);
            }
            intent.putExtra("name", username.toString());
            startActivity(intent);
            finish();

            return true;
        }
    }
    public String getSplit(String email)

    {
        String name[]  =  email.split("@");
        return name[0];
    }
    public void restoreUser()
    {
        authentication = FirebaseAuth.getInstance();
        user = authentication.getCurrentUser();
        user = null;
    }
}
