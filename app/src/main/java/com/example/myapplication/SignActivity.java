package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignActivity extends AppCompatActivity {


    EditText UserEmail, UserPassword;
    TextView SignIn, SignUp;
    String email, password;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        UserEmail =findViewById(R.id.emailText);
        UserPassword=findViewById(R.id.passWord);
        SignIn =findViewById(R.id.SignIn);
        SignUp = findViewById(R.id.SignUp);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=UserEmail.getText().toString().trim();
                password=UserPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    UserEmail.setError("Please enter your email");
                    UserEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    UserPassword.setError("Please enter your email");
                    UserPassword.requestFocus();
                    return;
                }
            SignIn();

            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SignActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }
    protected void onStart(){
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(SignActivity.this,MainActivity.class));
            finish();
        }
    }

    private void SignIn(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password.trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String username= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                Intent intent = new Intent(SignActivity.this,MainActivity.class);
                intent.putExtra("name",username);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseAuthInvalidUserException){
                    Toast.makeText(SignActivity.this,"user does not have valid email and password", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SignActivity.this,"Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


}