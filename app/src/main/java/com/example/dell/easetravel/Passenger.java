package com.example.dell.easetravel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Passenger extends AppCompatActivity {

    TextView text1;
    ImageView logout;

    String username, email, password;

    Account account = new Account();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);

        text1 = (TextView) findViewById(R.id.text1);
        logout = (ImageView) findViewById(R.id.logout);
        Button button = (Button) findViewById(R.id.button2);

        Intent intent = getIntent();
        username = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("pass");

        account.setAccount(username, email, password);

        String text = text1.getText().toString();
        text = text + " " + account.getUsername();
        text1.setText(text.toString());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), PassengerMaps.class);
                intent.putExtra("username", account.getUsername());
                startActivity(intent);
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
    }
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
