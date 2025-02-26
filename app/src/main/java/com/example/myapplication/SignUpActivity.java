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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    EditText User, UserEmail, UserPassword;
    TextView SignIn, SignUp;
    String name,email, password;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        UserEmail = findViewById(R.id.emailText1);
        UserPassword = findViewById(R.id.passWord1);
        SignIn = findViewById(R.id.SignIn1);
        SignUp = findViewById(R.id.SignUp1);
        User = findViewById(R.id.User1);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = UserEmail.getText().toString().trim();
                password = UserPassword.getText().toString().trim();
                name = User.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    User.setError("Please enter your name");
                    User.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    UserEmail.setError("Please enter your email");
                    UserEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    UserPassword.setError("Please enter your password");
                    UserPassword.requestFocus();
                    return;
                }

            SignUp();
            }
        });

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignActivity.class);
                startActivity(intent);
            }

        });
    }
        protected void onStart(){
            super.onStart();
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                finish();
            }
        }

        private void SignUp(){

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(),password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    UserProfileChangeRequest userProfileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null) {
                        firebaseUser.updateProfile(userProfileChangeRequest);
                    }

                    UserModel userModel=new UserModel(FirebaseAuth.getInstance().getUid(),email, name, password);
                   databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(userModel);
                   Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                   intent.putExtra("name",name);
                   startActivity(intent);
                   finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this,"signupfailed so its singup error",Toast.LENGTH_SHORT).show();

                }
            });

        }

    }

