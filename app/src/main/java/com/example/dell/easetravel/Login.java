package com.example.dell.easetravel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth authentication;
    DatabaseReference ref;
    FirebaseDatabase db;

    Button save;
    EditText email, password;
    TextView text1;
    String status = "";
    String email_login, pass_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        status = intent.getStringExtra("status");

        save = (Button) findViewById(R.id.save);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        text1 = (TextView) findViewById(R.id.text1);

        authentication = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        text1.setText("Login As "+ status);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email_login = email.getText().toString();
                pass_login = password.getText().toString();

                checkForUser(email_login);
            }
        });
    }
    public void SignInFirebase(final String email, String pass)
    {
        authentication.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                
                if(task.isSuccessful())
                {
                    Intent intent = null;

                    SharedPreferences settings1 = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor edit1 = settings1.edit();

                    if(status.equals("Passenger"))
                    {
                        edit1.putString("passenger_driver", "passenger");
                        edit1.apply();

                        intent = new Intent(getApplicationContext(), Passenger.class);
                    }
                    else if(status.equals("Driver"))
                    {
                        edit1.putString("passenger_driver", "driver");
                        edit1.apply();

                        intent = new Intent(getApplicationContext(), Driver.class);
                    }

                    SharedPreferences settings = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putString("logout", "no");
                    edit.apply();

                    String username = getSplit(email.toString());

                    intent.putExtra("name", username.toString());
                    startActivity(intent);
                    finish();

                }
                else
                    Toast.makeText(Login.this, "Account Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String getSplit(String email)

    {
        String name[]  =  email.split("@");
        return name[0];
    }
    public void checkForUser(String val)
    {
        final String username1 = getSplit(val);
        if(status.equals("Passenger"))
        {
            ref.child("Users").child("Passengers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(username1))
                        SignInFirebase(email_login, pass_login);
                    else
                        new AlertDialog.Builder(Login.this).setPositiveButton("OK", null)
                                .setTitle("Error").setMessage("Account Not Found").create().show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(status.equals("Driver"))
        {
            ref.child("Users").child("Drivers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(username1))
                        SignInFirebase(email_login, pass_login);
                    else
                        new AlertDialog.Builder(Login.this).setPositiveButton("OK", null)
                                .setTitle("Error").setMessage("Account Not Found").create().show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
