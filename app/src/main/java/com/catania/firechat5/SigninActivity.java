package com.catania.firechat5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SigninActivity extends AppCompatActivity {

    EditText userEmail, userPassword;
    TextView signinBtn, signupBtn;
    String email, password;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_signin);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        userEmail    = findViewById(R.id.emailtext);
        userPassword = findViewById(R.id.passwordtext);
        signinBtn    = findViewById(R.id.login);
        signupBtn    = findViewById(R.id.signup);

        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email    = userEmail.getText().toString().trim();
                password = userPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Inserisci email");
                    userEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    userPassword.setError("Inserisci password");
                    userPassword.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    userPassword.setError("Password deve essere lunga almeno 6 caratteri");
                    userPassword.requestFocus();
                    return;
                }
                Signin();
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Signin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(),password.trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.putExtra("name",username);
                        startActivity(intent);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof FirebaseAuthInvalidUserException){
                            Toast.makeText(SigninActivity.this, "Utente non esistente", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(SigninActivity.this, "Errore di autenticazione", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(SigninActivity.this,MainActivity.class));
            finish();
        }
    }
}