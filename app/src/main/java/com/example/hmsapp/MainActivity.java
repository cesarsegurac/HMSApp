package com.example.hmsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.GoogleAuthProvider;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.agconnect.crash.AGConnectCrash;

public class MainActivity extends AppCompatActivity  {
    Button btnLogin;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int SIGN_IN_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // HMS
        // crash
        //AGConnectCrash.getInstance().testIt(this); //no levanta en el emulador huawei
        AGConnectCrash.getInstance().enableCrashCollection(true);

        //AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
         .requestIdToken(CLIENT_ID)
         .requestProfile()
         .build();
client = GoogleSignIn.getClient(this, gso);
Intent signInIntent = client.getSignInIntent();
startActivityForResult(signInIntent, RC_SIGN_IN); */
        // FIN HMS

        // GMS
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id0))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        // FIN GMS

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, SIGN_IN_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE) {
            //GOOGLE
            /*GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){

                GoogleSignInAccount account = result.getSignInAccount();
                String welcome = "Bienvenido " + account.getDisplayName() + " (" + account.getEmail() + ")";
                Toast.makeText(this, welcome, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Error en la autentificación con su cuenta de Google, verificar los datos de usuario y clave ingresados.", Toast.LENGTH_SHORT).show();
            }*/

            //HMS


            Task<GoogleSignInAccount> signedInAccountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data);
            signedInAccountFromIntent.addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                @Override
                public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                    AGConnectAuthCredential credential =
                            GoogleAuthProvider.credentialWithToken(googleSignInAccount.getIdToken());
                    AGConnectAuth.getInstance().signIn(credential)
                            .addOnSuccessListener(signInResult -> {
                                MainActivity.this.loginSuccess(signInResult);
                            })
                            .addOnFailureListener(e -> {
                                Log.d("hmsdemo", "1: " + e.toString());
                                MainActivity.this.showToast(e.getMessage());
                            });
                }
            });
            signedInAccountFromIntent.addOnFailureListener(e -> {
                Log.d("hmsdemo", "2: " + e.toString());
                showToast(e.getMessage());
            });



            /*GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<GoogleSignInAccount>() {
                        @Override
                        public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                            AGConnectAuthCredential credential = GoogleAuthProvider.credentialWithToken(googleSignInAccount.getIdToken());
                            AGConnectAuth.getInstance().signIn(credential)
                                    .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                                        @Override
                                        public void onSuccess(SignInResult signInResult) {
                                            AGConnectUser user = signInResult.getUser();
                                            String welcome = "Bienvenido " + user.getDisplayName() + " (" + user.getEmail() + ")";
                                            Toast.makeText(MainActivity.this, welcome, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(MainActivity.this, "Problemas al realizar la autentificación con Google." + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });*/

       /*     Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            task.addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<GoogleSignInAccount>(){
                @Override
                public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                    String idToken = googleSignInAccount.getIdToken();

                    AGConnectAuthCredential credential = GoogleAuthProvider.credentialWithToken(idToken);
                    AGConnectAuth.getInstance().signIn(credential)
                            .addOnSuccessListener(new OnSuccessListener<SignInResult>() {
                                @Override
                                public void onSuccess(SignInResult signInResult) {
                                    // onSuccess

                                    AGConnectUser user = signInResult.getUser();
                                    String welcome = "Bienvenido " + user.getDisplayName() + " (" + user.getEmail() + ")";
                                    Toast.makeText(MainActivity.this, welcome, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    // onFail
                                    Log.d("hmsdemo", "1: " + e.toString());
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener(){
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("hmsdemo", "2: " + e.toString());
                    Toast.makeText(MainActivity.this, "Problemas al realizar la autentificación con Google." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
*/
        }
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void loginSuccess(SignInResult signInResult) {
        AGConnectUser user = signInResult.getUser();
        showToast("Bienvenido " + user.getDisplayName() + " (" + user.getEmail() + ")");
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            mGoogleSignInClient.signOut();
        }*/
    }
}