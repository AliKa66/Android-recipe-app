package com.bektas.kitchendiary;

import com.bektas.kitchendiary.model.Recipe;
import com.bektas.kitchendiary.util.FirebaseUtil;
import com.bumptech.glide.Glide;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Recipe mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = FirebaseUtil.recipe_map.get(getArguments().getString(ARG_ITEM_ID));
//            Activity activity = this.getActivity();
//            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//            if (appBarLayout != null) {
//                appBarLayout.setTitle(mItem.getTitle());
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ImageView image = rootView.findViewById(R.id.image);
            TextView txtName = rootView.findViewById(R.id.txtName);
            TextView txtPrepTime = rootView.findViewById(R.id.txtPreparationTime);
            TextView txtCookTime = rootView.findViewById(R.id.txtCookingTime);
            TextView txtIngredients = rootView.findViewById(R.id.txtIngredients);

            txtName.setText(mItem.getTitle());
            txtPrepTime.setText(mItem.getPreparationTime());
            txtCookTime.setText(mItem.getCookingTime());
            txtIngredients.setText(mItem.getIngredients());
            if (mItem.getImageUrl() != null && !mItem.getImageUrl().isEmpty()){
                Log.d("Image url", mItem.getImageUrl());
                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(rootView.getContext());
                circularProgressDrawable.setStrokeWidth(5);
                circularProgressDrawable.setCenterRadius(30);
                circularProgressDrawable.start();
                Glide.with(rootView.getContext())
                        .load(mItem.getImageUrl())
                        .placeholder(circularProgressDrawable)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(image);
            }
        }

        return rootView;
    }

}
