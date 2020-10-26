package com.example.hmsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;*/

import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.ClientSecretBasic;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity  {
    Button btnLogin;
    //private GoogleSignInClient mGoogleSignInClient;
    public static final int SIGN_IN_CODE = 777;

    static String CLIENT_ID = "179184621922-m8btq1hf9106r8fe75ohrr2jn3igg0ts.apps.googleusercontent.com";
    static String TAG = "hmsdemo";
    AuthState authState;
    TextView tvtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvtoken = findViewById(R.id.tvToken);

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
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id0))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);*/
        // FIN GMS

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                //startActivityForResult(signInIntent, SIGN_IN_CODE);

                AuthorizationServiceConfiguration serviceConfiguration =
                        new AuthorizationServiceConfiguration(
                                Uri.parse("https://accounts.google.com/o/oauth2/auth"), // authorization endpoint
                                Uri.parse("https://oauth2.googleapis.com/token")); // token endpoint

                AuthorizationRequest.Builder authRequestBuilder =
                        new AuthorizationRequest.Builder(
                                serviceConfiguration, // the authorization service configuration
                                CLIENT_ID, // the client ID, typically pre-registered and static
                                ResponseTypeValues.CODE, // the response_type value: we want a code
                                Uri.parse("com.example.hmsapp:/oauth2redirect")); // the redirect URI to which the auth response is sent

                authRequestBuilder.setScope("openid email profile");
                AuthorizationRequest authRequest = authRequestBuilder.build();

                AuthorizationService authService = new AuthorizationService(MainActivity.this);
                Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
                startActivityForResult(authIntent, SIGN_IN_CODE);
            }
        });

        try {
            String token = HmsInstanceId.getInstance(this).getToken(CLIENT_ID, "HCM");
            Log.i(TAG, "el token = " + token);


        } catch (ApiException e) {
            e.printStackTrace();
        }


        Log.i(TAG, "entro main activity");

        // PUSH
        MyReceiver receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.hmsapp.ON_NEW_TOKEN");
        MainActivity.this.registerReceiver(receiver,filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE) {

            AuthorizationResponse authResponse = AuthorizationResponse.fromIntent(data);
            AuthorizationException authException = AuthorizationException.fromIntent(data);
            authState = new AuthState(authResponse, authException);
            if(authResponse != null){
                AuthorizationService authService = new AuthorizationService(this);
                authService.performTokenRequest(
                        authResponse.createTokenExchangeRequest(),
                        new AuthorizationService.TokenResponseCallback() {
                            @Override public void onTokenRequestCompleted(
                                    TokenResponse tokenResponse, AuthorizationException ex) {
                                if (tokenResponse != null) {
                                    authState.update(tokenResponse, ex);
                                    String accessToken = tokenResponse.accessToken;
                                    String idToken = tokenResponse.idToken;

                                    writeAuthState(authState);
                                    new GetGoogleUserInfoTask().execute(accessToken);

                                    //Toast.makeText(MainActivity.this, "access token: " + accessToken + "\nidtoken: " + idToken, Toast.LENGTH_SHORT).show();

                                    Log.e(
                                            TAG,
                                            "Token Response [ Access Token: ${tokenResponse.accessToken}, ID Token: ${tokenResponse.idToken}"
                                    );
                                } else {
                                    Log.e(TAG, "Token Exchange failed", ex);
                                }
                            }
                        });
            }

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

/*
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
*/


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
/*
    public String getUserInfo(String accessToken) throws IOException {
        try {
            URL url = new URL("https://www.googleapis.com/oauth2/v3/userinfo?alt=json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            InputStream inputStream = conn.getInputStream();
            return convertStreamToString(inputStream);
        }catch (NetworkOnMainThreadException e){
            Log.e(TAG, "Error: "  + e);
            e.printStackTrace();
        }
        return null;
    }
*/
    public String convertStreamToString(InputStream inputStream){
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line=bufferedReader.readLine())!=null){
                sb.append(line).append('\n');
            }

        } catch (IOException e) {
            Log.e(TAG, "Read get info failed: ", e);
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }

    @Override
    protected void onStart() {
        super.onStart();

        /*GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            mGoogleSignInClient.signOut();
        }*/

        try {
            authState = readAuthState();
            if(authState != null && authState.isAuthorized()){
                refreshTokens();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class GetGoogleUserInfoTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String accessToken = params[0];
            URL url = null;
            HttpURLConnection conn = null;
            InputStream inputStream = null;

            try {
                url = new URL("https://www.googleapis.com/oauth2/v3/userinfo?alt=json");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                inputStream = conn.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return convertStreamToString(inputStream);
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                String name = obj.getString("name");
                String email = obj.getString("email");

                Toast.makeText(MainActivity.this, "Bienvenido " + name + " (" + email + ")", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public AuthState readAuthState() throws JSONException {
        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        String stateJson = authPrefs.getString("stateJson", "");
        if (!stateJson.equals("")) {
            return AuthState.jsonDeserialize(stateJson);
        } else {
            return null;
        }
    }

    public void writeAuthState(@NonNull AuthState state) {
        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        authPrefs.edit()
                .putString("stateJson", state.jsonSerializeString())
                .apply();
    }

    private void refreshTokens() {
        AuthorizationService service = new AuthorizationService(this);

        authState.performActionWithFreshTokens(service,
                new AuthState.AuthStateAction() {
                    @Override
                    public void execute(String accessToken, String idToken,
                                        AuthorizationException authException) {
                        // Handle token refresh error here

                        if(authException == null){
                            new GetGoogleUserInfoTask().execute(accessToken);
                        }
                        //executeApiCall(accessToken);
                    }
                });
    }

    // PUSH
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.hmsapp.ON_NEW_TOKEN".equals(intent.getAction())) {
                String token = intent.getStringExtra("token");
                tvtoken.setText(token);
            }
        }
    }
}