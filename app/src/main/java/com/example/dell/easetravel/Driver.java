package com.example.dell.easetravel;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Driver extends AppCompatActivity {

    TextView text1;
    ImageView logout;

    String email, password, username, route, number, cap, location;

    Bus bus = new Bus();
    Account account = new Account();

    FirebaseAuth authentication;
    FirebaseDatabase db;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        authentication = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference();

        text1 = (TextView) findViewById(R.id.text1);
        logout = (ImageView) findViewById(R.id.logout);
        Button button = (Button) findViewById(R.id.button3);
        Button modify = (Button) findViewById(R.id.modify);

        Intent intent = getIntent();
        username = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("pass");
        number = intent.getStringExtra("bus_n");
        route = intent.getStringExtra("bus_r");
        cap = intent.getStringExtra("bus_c");
        location = "On";

        account.setAccount(username, email, password);
        bus.setBus(number, route, cap);

        String text = text1.getText().toString();
        text = text + " " + account.getUsername();
        text1.setText(text.toString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DriverMaps.class);
                intent.putExtra("username", account.getUsername());
                startActivity(intent);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences settings = getSharedPreferences("PREFS", 0);
                SharedPreferences.Editor edit = settings.edit();
                edit.putString("logout", "yes");
                edit.apply();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Driver.this).setTitle("Modify");
                LinearLayout layout = new LinearLayout(Driver.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText bus_n = new EditText(Driver.this);
                bus_n.setHint("Enter Updated Bus Number");
                bus_n.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(bus_n);

                final EditText bus_c = new EditText(Driver.this);
                bus_c.setHint("Enter Updated Bus Cpacity");
                bus_c.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(bus_c);

                final EditText bus_r = new EditText(Driver.this);
                bus_r.setHint("Enter Updated Bus Route");
                bus_r.setInputType(InputType.TYPE_CLASS_TEXT);
                layout.addView(bus_r);

                builder.setView(layout);

                builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String numb = bus_n.getText().toString();
                        String rout = bus_r.getText().toString();
                        String capac = bus_c.getText().toString();

                        if(!(numb.equals("")))
                            ref.child("Users").child("Drivers").child(account.getUsername()).child("Bus Number").setValue(numb);

                        if(!(rout.equals("")))
                            ref.child("Users").child("Drivers").child(account.getUsername()).child("Bus Route").setValue(rout);

                        if(!(capac.equals("")))
                            ref.child("Users").child("Drivers").child(account.getUsername()).child("Bus Capacity").setValue(capac);

                    }
                });
                builder.setNegativeButton("Cancel",  null);
                builder.show();
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
