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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
import com.squareup.picasso.Picasso;

import java.util.List;

public class BussinesAdapter extends RecyclerView.Adapter<BussinesAdapter.MyViewHolder> {
 
    private Context mContext;
    private List<Bussiness> bussinessList;
 
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, country;
        public ImageView thumbnail, overflow;
 
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.card_title);
            count = (TextView) view.findViewById(R.id.card_link);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            country = (TextView) view.findViewById(R.id.card_country);

        }
    }
 
 
    public BussinesAdapter(Context mContext, List<Bussiness> albumList) {
        this.mContext = mContext;
        this.bussinessList = albumList;
    }
 
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bussiness_card, parent, false);
 
        return new MyViewHolder(itemView);
    }
 
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Bussiness album = bussinessList.get(position);
        holder.title.setText(album.getName());
        holder.count.setText(album.getLink());
        holder.country.setText(album.getCountry());

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
            //    Toast.makeText(mContext, holder.title.getText(),
              //          Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, productsActivity.class);
               // String strName = null;
               intent.putExtra("_id", holder.count.getText());
               intent.putExtra("country", holder.country.getText() );
                mContext.startActivity(intent);
            }
        });
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showPopupMenu(holder.overflow);
                Toast.makeText(mContext, holder.title.getText(),
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, productsActivity.class);
                intent.putExtra("_id", holder.count.getText());

                mContext.startActivity(intent);
            }
        });
    }
 


    @Override
    public int getItemCount() {
        return bussinessList.size();
    }

}