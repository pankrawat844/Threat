package com.example.threat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.threat.utils.Locator;
import com.example.threat.utils.SharedPreference;
import com.example.threat.utils.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.DocumentViewChange;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN";
    private FirebaseFirestore mFirestore;
    private Button button;
    private Locator locator;
    private User user;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

        }
        checkLocationPermission();
        init();
        locator = new Locator(this);
        // Firestore
        mFirestore = FirebaseFirestore.getInstance();
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(MainActivity.this, "Loading", "Wait while loading...");
                locator.getLocation(new Locator.Listener() {
                    @Override
                    public void onLocationFound(Location location) {
                        user.latitude = location.getLatitude();
                        user.longitude = location.getLongitude();
                        user.isHandled = false;
                        sendThreat();
                    }

                    @Override
                    public void onLocationNotFound() {

                    }
                });

            }
        });
    }

    private void sendThreat() {
        user.date = new Date();
        if (user.id == "") {
            mFirestore.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            user.id = documentReference.getId();
                            setUser();
                            dialog.dismiss();
                            showAlert("Threat notification sent.");
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    });
        } else {
            mFirestore.collection("users")
                    .document(user.id)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            showAlert("Threat notification sent.");
                        }
                    });
        }

        mFirestore.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<Token> types = documentSnapshots.toObjects(Token.class);

                            // Add all to your list
                            ArrayList<Token> mArrayList = new ArrayList<>();
                            mArrayList.addAll(types);
                            for(int i=0;i<mArrayList.size();i++){
                                try {
                                JSONObject jsonObject= new JSONObject();
                                jsonObject.put("id",user.id);
                                jsonObject.put("name",user.name);
                                jsonObject.put("latitude",user.latitude);
                                 jsonObject.put("longitude",user.longitude);
                                 Log.e("jsonobj",jsonObject.toString());
                                    sendNotification(mArrayList.get(i).getToken(),jsonObject.toString(),"TEST");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                            Log.d(TAG, "onSuccess: " + mArrayList.toString());
                        }
                    }
                });
    }

    private void sendNotification(final String token, final String msg, final String title){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, "https://schmanagement.000webhostapp.com/sendNotification.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap hashMap= new HashMap();
                hashMap.put("arr",token);
                hashMap.put("msg",msg);
                hashMap.put("title",title);
                return hashMap;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification")
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }

    private void init() {
        getUser();
        if (user.id == "") {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter your name");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    user.name = input.getText().toString();
                    setUser();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    private void getUser() {
        user = new User(
                SharedPreference.getInstance(this).getStringValue("id", ""),
                SharedPreference.getInstance(this).getStringValue("name", "")
        );
    }

    private void setUser() {
        SharedPreference.getInstance(getApplicationContext())
                .setValue("id", user.id);
        SharedPreference.getInstance(getApplicationContext())
                .setValue("name", user.name);
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {
                    showAlert("You need to allow location permission.");
                }
                return;
            }

        }
    }
}
