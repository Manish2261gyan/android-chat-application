package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView text;
    RecyclerView recyclerView ;

    String YourName;
    UserAdapter usersAdapter;

    DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         String userName = getIntent().getStringExtra("user");
         getSupportActionBar().setTitle(userName);

          usersAdapter=new UserAdapter(this);
          recyclerView=findViewById(R.id.recycleview);
          recyclerView.setAdapter(usersAdapter);
          recyclerView.setLayoutManager(new LinearLayoutManager(this));

          databaseReference = FirebaseDatabase.getInstance().getReference("user");
          databaseReference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  usersAdapter.clear();
                  int count = 0;  // Debug counter
                  for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                      UserModel userModel = dataSnapshot.getValue(UserModel.class);
                      if (userModel != null && userModel.getUserID() != null &&
                              !userModel.getUserID().equals(FirebaseAuth.getInstance().getUid())) {

                          usersAdapter.add(userModel);
                          count++;  // Increase counter
                          Log.d("DEBUG", "User added: " + userModel.getUserName());  // Log added user
                      }
                  }
                  usersAdapter.notifyDataSetChanged();  // Ensure UI updates
                  Log.d("DEBUG", "Total Users Loaded: " + count);  // Log final user count

              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {
                  Log.e("DEBUG", "Firebase Error: " + error.getMessage());
              }

          });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,SignActivity.class));
            finish();
            return true;
        }

        return false;
    }
}


