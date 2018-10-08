package maceda.alejandro.verificadoronline;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class productsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String _id;
    private String country;
    FirebaseDatabase database;
  //  FirebaseUser currentUser;
    private  String userId;
    private DatabaseReference mRef;
    private List<productList> productList;
    private RecyclerView recyclerView;
    private productAdapter adapter;
    ImageButton mMenu;
    private ImageButton p_scan;
    private EditText add_p_ean;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        _id = getIntent().getExtras().getString("_id", "");
        country = getIntent().getExtras().getString("country", "");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                add_products();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        adapter = new productAdapter(this, productList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        load_firebase();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logged, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void add_products() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(productsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_products, null);
        add_p_ean = (EditText) mView.findViewById(R.id.add_p_ean);
        final EditText add_p_name = (EditText) mView.findViewById(R.id.add_p_name);
        final EditText add_p_price = (EditText) mView.findViewById(R.id.add_p_price);
        TextView tv = (TextView) mView.findViewById(R.id.textView);
        p_scan = (ImageButton) mView.findViewById(R.id.product_scan);
        p_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(productsActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();

            }
        });
        //  tv.setVisibility(View.GONE);
        tv.setText("Add products");
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        Button register = (Button) mView.findViewById(R.id.l_add);

        mBuilder.setView(mView);
        //mBuilder.setTitle("Crear una contraseña");
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(add_p_ean.getText().toString().trim())) {
                    add_p_ean.setError("Write your name");

                } else {

                    dialog.dismiss();
                    Toast.makeText(productsActivity.this,
                            "Guardando...",
                            Toast.LENGTH_SHORT).show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    DatabaseReference mRef = database.getReference().child("Products").push();
                    mRef.child("ean").setValue(add_p_ean.getText().toString());
                    mRef.child("name").setValue(add_p_name.getText().toString());
                    mRef.child("price").setValue(add_p_price.getText().toString());
                    mRef.child("country").setValue(country);
                    mRef.child("country_ean").setValue(country + "_" + add_p_ean.getText().toString());
                    mRef.child("business_id").setValue(_id);
                  //  mRef.child("user").setValue(userId);
                    mRef.child("user").setValue(userId).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            // ...
                            load_firebase();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    // ...
                                }
                            });

                    /*
                    update_name = e_name.getText().toString();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(update_name)
                            .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();

                    currentUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Log.d(TAG, "User profile updated.");
                                        Toast.makeText(loggedActivity.this,
                                                "User profile updated",
                                                Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        tName.setText(update_name);
                                    }
                                    else {
                                        Toast.makeText(loggedActivity.this,
                                                "Error updating",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            */
                }

            }
        });
    }

    public void edit_product(String name, String ean, String price, final String reff) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(productsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_products, null);
        final EditText add_p_ean = (EditText) mView.findViewById(R.id.add_p_ean);
        add_p_ean.setEnabled(false);
        final EditText add_p_name = (EditText) mView.findViewById(R.id.add_p_name);
        final EditText add_p_price = (EditText) mView.findViewById(R.id.add_p_price);
        TextView tv = (TextView) mView.findViewById(R.id.textView);
        //  tv.setVisibility(View.GONE);
        tv.setText(reff);
        tv.setTextSize(10);
        tv.setTextColor(Color.WHITE);
        add_p_ean.setText(ean);
        add_p_name.setText(name);
        add_p_price.setText(price);
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        Button register = (Button) mView.findViewById(R.id.l_add);
        register.setText("SAVE");

        mBuilder.setView(mView);
        //mBuilder.setTitle("Crear una contraseña");
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(add_p_ean.getText().toString().trim())) {
                    add_p_ean.setError("Write your name");

                } else {

                    dialog.dismiss();
                 //   Toast.makeText(productsActivity.this,
                   //         "Guardando...",
                   //         Toast.LENGTH_SHORT).show();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    //FirebaseUser user = mAuth.getCurrentUser();
                    //String userId = user.getUid();
                    DatabaseReference mRef = database.getReference()
                            .child("Products").child(reff);


//                    mRef.child("ean").setValue(add_p_ean.getText().toString());
                    mRef.child("name").setValue(add_p_name.getText().toString());
                    mRef.child("price").setValue(add_p_price.getText().toString());
                    load_firebase();
                  //  mRef.child("country").setValue(country);
                    //mRef.child("country_ean").setValue(country + "_" + add_p_ean.getText().toString());
                    //mRef.child("business_id").setValue(_id);



                }

            }
        });
    }

    public void delete_product(final String reff, String name) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(productsActivity.this);

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Are you sure ? \n " + name)
                .setTitle("DELETE");
        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                //FirebaseUser user = mAuth.getCurrentUser();
                //String userId = user.getUid();
                DatabaseReference mRef = database.getReference()
                        .child("Products").child(reff);
                mRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        // ...
                        productList.clear();
                        adapter.notifyDataSetChanged();
                        load_firebase();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                // ...
                            }
                        });
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });



// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void load_firebase () {


        database = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
         Query nmRef = database.getReference().child("Products").orderByChild("business_id").equalTo(_id);



        nmRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // showProgress(false);
                productList.clear();
                //Log.d(TAG, "onDataChange: " + dataSnapshot);
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        // Toast.makeText(loggedActivity.this, postSnapshot.child("name").getValue().toString(), Toast.LENGTH_LONG).show();

                        productList ab = new productList(postSnapshot.child("ean").getValue().toString(),
                                postSnapshot.child("name").getValue().toString(),
                                postSnapshot.child("price").getValue().toString(),

                                postSnapshot.getKey(), "", "" );
                        productList.add(ab);
                        adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(productsActivity.this, "Some error occured", Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        });
    }
    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                add_p_ean.setText(result.getContents());

                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
