package maceda.alejandro.verificadoronline;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

public class MainActivity extends AppCompatActivity {

    ImageButton mMenu;
    private FirebaseAuth mAuth;
    private String _id;
    FirebaseDatabase database;
    //  FirebaseUser currentUser;
    private  String userId;
    private DatabaseReference mRef;
    private List<productList> productList;
    private RecyclerView recyclerView;
    private productAdapter adapter;
    private EditText search_product;
    private AdView mAdView;
    private ImageButton scan;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        scan = (ImageButton) findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();

            }
        });
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#303030"));
        }

        mMenu = (ImageButton) findViewById(R.id.popup_menu);


        mMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, mMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        //noinspection SimplifiableIfStatement
                        if (id == R.id.menu_login) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);

                            return true;
                        }

                        if (id == R.id.menu_settings) {
                          choose_country();
                        }
                      //  Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                try {
                    Method method = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                    method.setAccessible(true);
                    method.invoke(popup.getMenu(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method

        search_product = (EditText) findViewById(R.id.main_search_product);
        search_product.setImeOptions(EditorInfo.IME_ACTION_DONE);

        search_product.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent eevent) {
                if (id == EditorInfo.IME_ACTION_SEARCH
                        || id == EditorInfo.IME_ACTION_DONE
                       ) {
                    search_product_by_ean(search_product.getText().toString().trim());
                    return true;
                }

                return false;
            }
        });
        /*
        search_product.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            onSearchAction(v);
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });
                */
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        productList = new ArrayList<>();
        adapter = new productAdapter(this, productList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
     //   MobileAds.initialize(this, "ca-app-pub-2501920914988623~3203645707");
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
      //  MobileAds.initialize(this,
        //        "ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    /*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    private void search_product_by_ean (String ean) {



        database = FirebaseDatabase.getInstance();
       // FirebaseUser user = mAuth.getCurrentUser();
        //userId = user.getUid();
       // mRef = database.getReference().child("Products");
        Query nmRef = database.getReference().child("Products").orderByChild("country_ean")
                .equalTo(PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                        .getString("countrycode", "MX") + "_" + ean);




        nmRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // showProgress(false);
                productList.clear();
                //Log.d(TAG, "onDataChange: " + dataSnapshot);
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String key = dataSnapshot.getKey();
                        if (key.equals("Products")) {
                            // Toast.makeText(loggedActivity.this, postSnapshot.child("name").getValue().toString(), Toast.LENGTH_LONG).show();

                            productList ab = new productList(postSnapshot.child("ean").getValue().toString(),
                                    postSnapshot.child("name").getValue().toString(),
                                    postSnapshot.child("price").getValue().toString(),
                                    "", "", "");
                            productList.add(ab);


                            adapter.notifyDataSetChanged();
                            search_product.setText("");
                        }
                    }
                }
                else {
                        search_product.setText("");
                    adapter.notifyDataSetChanged();

                        Toast.makeText(MainActivity.this, "Not found", Toast.LENGTH_LONG).show();
                    }

                }



            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, "Some error occured", Toast.LENGTH_LONG).show();
                //showProgress(false);
            }
        });

    }

    private void choose_country() {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_choose_country, null);

           final TextView tv_country = (TextView) mView.findViewById(R.id.dialog_country_name);
            //  tv.setVisibility(View.GONE);
       tv_country.setText("Ubicacion: " + PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                .getString("countryname", "Mexico") + " - " +
               PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                       .getString("countrycode", "MX")
       );

           // tv.setText("Add products");


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

                    //Toast.makeText(MainActivity.this, "woriking...", Toast.LENGTH_LONG).show();

                    final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                            // Implement your code here
                            PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
                                    .edit()
                                    .putString("countrycode", code)
                                    .putString("countryname", name)
                                    .apply();
                            tv_country.setText("Ubicacion: " + name + " - " +
                                    code
                            );
                            Toast.makeText(MainActivity.this,name + " : " + code, Toast.LENGTH_SHORT).show();
                            picker.dismiss();
                        }
                    });
                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");


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
                search_product_by_ean(result.getContents());

                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
