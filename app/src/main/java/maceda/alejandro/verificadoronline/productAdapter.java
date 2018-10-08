package maceda.alejandro.verificadoronline;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Method;
import java.util.List;

public class productAdapter extends RecyclerView.Adapter<productAdapter.MyViewHolder> {

    private Context mContext;
    private List<productList> productList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price, ean, link;
        public ImageView thumbnail, overflow;
        public ImageButton mMenu;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.product_card_name);
            link = (TextView) view.findViewById(R.id.product_card_link);
            thumbnail = (ImageView) view.findViewById(R.id.product_thumbnail);
            price = (TextView) view.findViewById(R.id.product_card_price);
            mMenu = (ImageButton) view.findViewById(R.id.popup_product_menu);
            ean = (TextView) view.findViewById(R.id.product_card_ean);



        }
    }


    public productAdapter(Context mContext, List<productList> albumList) {
        this.mContext = mContext;
        this.productList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final productList album = productList.get(position);
        holder.title.setText(album.getName());
        holder.price.setText(album.getPrice());
        holder.ean.setText(album.getEan());
        holder.link.setText(album.getLink());

        if (!TextUtils.isEmpty(album.getImage())) {
            Picasso.get().load(album.getImage()).into(holder.thumbnail);
            //  Toast.makeText(mContext, album.getImage(),
            //        Toast.LENGTH_SHORT).show();

        } else {
            Picasso.get().load(R.drawable.store).into(holder.thumbnail);

        }


        // loading album cover using Glide library
        //Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showPopupMenu(holder.overflow);
                Toast.makeText(mContext, holder.title.getText(),
                        Toast.LENGTH_SHORT).show();


            }
        });


        holder.mMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(mContext, holder.mMenu);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_product_menu, popup.getMenu());


                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        //noinspection SimplifiableIfStatement
                        if (id == R.id.menu_product_edit) {
                            //  Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            //startActivity(intent);
                            ((productsActivity)mContext).edit_product(album.getName(),
                                    album.getEan(), album.getPrice(), album.getLink());


                            return true;
                        }

                        if (id == R.id.menu_product_delete) {
                            // choose_country();
                            ((productsActivity)mContext).delete_product(album.getLink(), album.getName());

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

    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

}