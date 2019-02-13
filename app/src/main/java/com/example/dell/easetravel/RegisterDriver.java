package com.example.dell.easetravel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterDriver extends AppCompatActivity {

    FirebaseUser user;
    FirebaseAuth authentication;
    DatabaseReference ref;
    FirebaseDatabase db;

    Button save;
    EditText email, password, route, number, capacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        save = (Button) findViewById(R.id.save);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        number = (EditText) findViewById(R.id.number);
        capacity = (EditText) findViewById(R.id.capacity);
        route = (EditText) findViewById(R.id.route);

        authentication = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_passenger = email.getText().toString();
                String pass_passenger = password.getText().toString();

                connectToFirebase(email_passenger, pass_passenger);
            }
        });
    }
    public void connectToFirebase(final String email, final String pass)
    {
        final String username = getSplit(email);
        authentication.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                user = authentication.getCurrentUser();
                if(task.isSuccessful())
                {
                    ref.child("Users").child("Drivers").child(getSplit(username)).setValue(user.getUid());
                    ref.child("Users").child("Drivers").child(getSplit(username)).child("Bus Number").setValue(number.getText().toString());
                    ref.child("Users").child("Drivers").child(getSplit(username)).child("Bus Route").setValue(route.getText().toString());
                    ref.child("Users").child("Drivers").child(getSplit(username)).child("Bus Capacity").setValue(capacity.getText().toString());
                    ref.child("Users").child("Drivers").child(getSplit(username)).child("Location").setValue("On");

                    SharedPreferences settings = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putString("logout", "no");
                    edit.apply();

                    SharedPreferences settings1 = getSharedPreferences("PREFS", 0);
                    SharedPreferences.Editor edit1 = settings1.edit();
                    edit1.putString("passenger_driver", "driver");
                    edit1.apply();

                    Intent intent = new Intent(getApplicationContext(), Driver.class);
                    intent.putExtra("name", username.toString());
                    intent.putExtra("email", email.toString());
                    intent.putExtra("pass", pass.toString());
                    intent.putExtra("bus_n", number.toString());
                    intent.putExtra("bus_c", capacity.toString());
                    intent.putExtra("bus_r", route.toString());
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(RegisterDriver.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public String getSplit(String email)

    {
        String name[]  =  email.split("@");
        return name[0];
    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
