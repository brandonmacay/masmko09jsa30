package maceda.alejandro.verificadoronline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class loggedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    private  String userId;
    private DatabaseReference mRef;

    EditText mName, mLink;
    TextView tName;
    String update_name;
    private RecyclerView recyclerView;
    private BussinesAdapter adapter;
    private List<Bussiness> BussinessList;
    private View mProgressView;
    private CircleImageView iv_image_profile;
    private String imagePath;
    private Uri url_image;
    private Uri url_image_business;
    private CircleImageView p_profile;
    private CircleImageView business_image;
    private TextView progress_tv;
    private Button save_profile;
    private boolean image_business_choosed = false;
    private String countrycode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mProgressView = findViewById(R.id.l_login_progress);

        showProgress(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                add_business();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView tEmail = (TextView) header.findViewById(R.id.navigation_email);
        tName = (TextView) header.findViewById(R.id.navigation_name);
         p_profile = (CircleImageView) header.findViewById(R.id.navigation_imageView);

      //   wellcome = (TextView) findViewById(R.id.wellcome);
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            for (UserInfo profile : currentUser.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();
                //userId = profile.getUid();

                // Name, email address, and profile photo Url
                update_name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();
                url_image = photoUrl;

                tEmail.setText(email);
                tName.setText(update_name);
                Picasso.get().load(photoUrl).noFade().into(p_profile);

            }
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        BussinessList = new ArrayList<>();
        adapter = new BussinesAdapter(this, BussinessList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
       // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

          // prepareAlbums();

        try {
         //   Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }


        load_firebase();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            // Handle the camera action
            edit_profile ();
        } else if (id == R.id.nav_gallery) {



        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
       // updateUI(currentUser);
    }

    private void edit_profile () {
       /*
        Toast.makeText(loggedActivity.this, "Editando...",
                Toast.LENGTH_SHORT).show();
                */
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(loggedActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        final EditText e_name = (EditText) mView.findViewById(R.id.l_name);
        iv_image_profile = (CircleImageView) mView.findViewById(R.id.image_profile_iv);
        final ImageButton upload = (ImageButton)mView.findViewById(R.id.profile_image_upload);
        progress_tv = (TextView) mView.findViewById(R.id.upload_progress);
        TextView tv = (TextView) mView.findViewById(R.id.textView);
        //  tv.setVisibility(View.GONE);
        tv.setText("Edit Account");
        e_name.setText(update_name);
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        save_profile = (Button) mView.findViewById(R.id.l_ok);

        mBuilder.setView(mView);
        //mBuilder.setTitle("Crear una contraseña");
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
         Picasso.get().load(url_image).into(iv_image_profile);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(loggedActivity.this);
                        */
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(loggedActivity.this);
                startActivityForResult(intent, 1);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(e_name.getText().toString().trim()) ) {
                    e_name.setError("Write your name");

                }
                else {
                    /*
                    Toast.makeText(loggedActivity.this,
                            "Guardando...",
                            Toast.LENGTH_SHORT).show();
                            */
                    update_name = e_name.getText().toString();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(update_name)
                            .setPhotoUri(url_image)
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
                                        Picasso.get().load(url_image).noFade().into(p_profile);

                                    }
                                    else {
                                        Toast.makeText(loggedActivity.this,
                                                "Error updating",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }
    private void add_business() {

        countrycode = PreferenceManager.getDefaultSharedPreferences(loggedActivity.this)
                .getString("countrycode", "MX");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(loggedActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_bussiness, null);
        final EditText add_e_name = (EditText) mView.findViewById(R.id.add_l_name);
        final TextView country = (TextView) mView.findViewById(R.id.business_country_tv);
        country.setText("Ubicacion: " + PreferenceManager.getDefaultSharedPreferences(loggedActivity.this)
                        .getString("countryname", "Mexico") + " - " +
                        countrycode);

        Button change_country = (Button) mView.findViewById(R.id.business_change_country);
        change_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        // Implement your code here

                        country.setText("Ubicacion: " + name + " - " +
                                code
                        );
                        countrycode = code;
                        Toast.makeText(loggedActivity.this,name + " : " + code, Toast.LENGTH_SHORT).show();
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");


            }
        });
        business_image = (CircleImageView) mView.findViewById(R.id.add_business_image);
        final ImageButton uploadd = (ImageButton)mView.findViewById(R.id.business_upload_ib);
        final TextView tv = (TextView) mView.findViewById(R.id.textView);
        //  tv.setVisibility(View.GONE);
        tv.setText("Add bussiness");
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        final Button register = (Button) mView.findViewById(R.id.l_add);

        uploadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             /*
                CropImage.activity()

                        .setGuidelines(CropImageView.Guidelines.ON).
                        .start(loggedActivity.this);
                        */

                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(loggedActivity.this);
                startActivityForResult(intent, 2);

            }
        });

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


                if (TextUtils.isEmpty(add_e_name.getText().toString().trim())) {
                    add_e_name.setError("Write your name");

                } else {

                   // dialog.dismiss();
                   // Toast.makeText(loggedActivity.this,
                     //       "Guardando...",
                       //     Toast.LENGTH_SHORT).show();
                    if (image_business_choosed) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference storageRef = storage.getReference();
                       // final StorageReference profileRef = storageRef.child("" + userId +  "/images/business/");

                        // StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                         Uri file = Uri.fromFile(new File(url_image_business.getPath()));
                          final StorageReference businessRef = storageRef.child("" + userId +  "/images/business/"+file.getLastPathSegment());
                        UploadTask uploadTask = businessRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                tv.setText("Upload is " + progress + "% done");
                                register.setText("Uploading...");
                                register.setEnabled(false);
                            }
                        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                                System.out.println("Upload is paused");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                                businessRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        url_image = uri;
                                        Toast.makeText(loggedActivity.this, "Upload Complete" , Toast.LENGTH_LONG).show();
                                       // Picasso.get().load(url_image).into(iv_image_profile);
                                        // Picasso.get().load(url_image).into(p_profile);
                                        register.setEnabled(true);
                                        //save_profile.setText("SAVE");
                                        DatabaseReference mmRef = database.getReference().child("Bussiness").push();
                                        //mRef.push();
                                        mmRef.child("name").setValue(add_e_name.getText().toString());
                                        mmRef.child("link").setValue(mmRef.getKey());
                                        mmRef.child("user").setValue(userId);
                                        mmRef.child("country").setValue(countrycode);
                                        mmRef.child("image").setValue(url_image.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Write was successful!
                                                // ...
                                                load_firebase();
                                                image_business_choosed = false;
                                                dialog.dismiss();
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Write failed
                                                        // ...
                                                    }
                                                });

                                        //Do what you want with the url
                                    }
                                });

                            }
                        });
                    }
                    else {
                        DatabaseReference mmRef = database.getReference().child("Bussiness").push();
                        //mRef.push();
                        mmRef.child("name").setValue(add_e_name.getText().toString());
                        mmRef.child("link").setValue(mmRef.getKey());
                        mmRef.child("user").setValue(userId);
                        mmRef.child("country").setValue(countrycode);
                        mmRef.child("image").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Write was successful!
                                // ...
                                dialog.dismiss();
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




                }

            }
        });
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);



            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void load_firebase () {
        database = FirebaseDatabase.getInstance();
        //FirebaseUser user = mAuth.getCurrentUser();
        userId = currentUser.getUid();
      //  mRef = database.getReference().child("Bussiness");
        Query nmRef = database.getReference().child("Bussiness").orderByChild("user").equalTo(userId);




        nmRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showProgress(false);
                BussinessList.clear();
                //Log.d(TAG, "onDataChange: " + dataSnapshot);
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                  //  Toast.makeText(loggedActivity.this, postSnapshot.toString(), Toast.LENGTH_LONG).show();

                    Bussiness a = new Bussiness(postSnapshot.child("name").getValue().toString(),
                            postSnapshot.child("country").getValue().toString(), "2",
                            postSnapshot.child("link").getValue().toString(),
                            postSnapshot.child("image").getValue().toString(), "6");
                    BussinessList.add(a);
                    adapter.notifyDataSetChanged();
                    /*
                    String key = dataSnapshot.getKey();
                    if (!key.equals("Products")) {
                        // Toast.makeText(loggedActivity.this, postSnapshot.child("name").getValue().toString(), Toast.LENGTH_LONG).show();
                        Bussiness a = new Bussiness(postSnapshot.child("name").getValue().toString(), "1", "2",
                                postSnapshot.child("link").getValue().toString(), postSnapshot.child("image").getValue().toString(), "6");
                        BussinessList.add(a);


                        adapter.notifyDataSetChanged();
                    }
                    */


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(loggedActivity.this, "Some error occured", Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
              //  Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
                // Create a storage reference from our app
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageRef = storage.getReference();
                final StorageReference profileRef = storageRef.child("" + userId +  "/images/profile.jpg");

               // StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
               // Uri file = Uri.fromFile(new File(res);
             //   StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                UploadTask uploadTask = profileRef.putFile(resultUri);

// Register observers to listen for when the download is done or if it fails
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                       progress_tv.setText("Upload is " + progress + "% done");
                       save_profile.setText("Uploading...");
                       save_profile.setEnabled(false);
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                             @Override
                                                                             public void onSuccess(Uri uri) {
                                                                                 url_image= uri;
                          Toast.makeText(loggedActivity.this, "Upload Complete" , Toast.LENGTH_LONG).show();
                          Picasso.get().load(url_image).into(iv_image_profile);
                          // Picasso.get().load(url_image).into(p_profile);
                           save_profile.setEnabled(true);
                           save_profile.setText("SAVE");


                                                                                 //Do what you want with the url
                                                                             }
                                                                         });

                    }
                });
               // e_link.setText("" + resultUri);



            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == 2) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                Picasso.get().load(resultUri).noFade().into(business_image);
                url_image_business = resultUri;
                image_business_choosed = true;

            }
            //Toast.makeText(loggedActivity.this, "Funciona perro " , Toast.LENGTH_LONG).show();

        }

    }

}
