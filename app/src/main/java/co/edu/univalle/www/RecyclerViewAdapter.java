package co.edu.univalle.www;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter  extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private ArrayList<String> localDataSet;
        private ArrayList<Integer> images;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView name;
            private final ImageView image;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View

                name = (TextView) view.findViewById(R.id.name);
                image = view.findViewById(R.id.image);
            }

            public TextView getName() {
                return name;
            }

            public ImageView getImage(){
                return image;
            }
        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public RecyclerViewAdapter(ArrayList<String> dataSet, ArrayList<Integer> images) {
            localDataSet = dataSet;
            this.images = images;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row, viewGroup, false);
            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.getName().setText(localDataSet.get(position));
            viewHolder.getImage().setImageResource(images.get(position));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.size();
        }


}
