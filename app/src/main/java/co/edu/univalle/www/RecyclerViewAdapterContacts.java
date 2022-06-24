package co.edu.univalle.www;

import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.edu.univalle.www.modelo.ProductSelectedListener;
import co.edu.univalle.www.modelo.ProductoServicio;
import co.edu.univalle.www.modelo.Sesion;

public class RecyclerViewAdapterContacts  extends RecyclerView.Adapter<RecyclerViewAdapterContacts.ViewHolder> {

    private ArrayList<String> arlContactos;
    private ArrayList<String> arlTipos;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivImage;
        private final TextView tvContact;

        public ViewHolder(View view) {
            super(view);
            ivImage = view.findViewById(R.id.ivImage);
            tvContact = view.findViewById(R.id.tvContact);
        }

        public void setImage(String strImage){
            if(strImage.equals("Whatsapp")){
                ivImage.setImageResource(R.drawable.whatsapp);
            } else if(strImage.equals("Gmail")){
                ivImage.setImageResource(R.drawable.gmail);
            }
        }

        public void setContact(String strContact){
            tvContact.setText(strContact);
        }

    }

    public RecyclerViewAdapterContacts(ArrayList<String> arlTipos, ArrayList<String> arlContactos) {
        this.arlContactos = arlContactos;
        this.arlTipos = arlTipos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.contact_row, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.setImage(arlTipos.get(position));
        viewHolder.setContact(arlContactos.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return arlContactos.size();
    }


}
