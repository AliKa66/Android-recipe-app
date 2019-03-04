package com.bektas.kitchendiary;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import id.zelory.compressor.Compressor;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bektas.kitchendiary.model.Recipe;
import com.bektas.kitchendiary.util.FirebaseUtil;
import com.bektas.kitchendiary.util.GlideUtil;
import com.bektas.kitchendiary.util.TimeParser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddOrEditMyRecipeActivity extends AppCompatActivity {
    private EditText txtName;
    private EditText txtPreparationTime;
    private EditText txtCookingTime;
    private ImageView imageView;
    private ProgressBar pbImageLoad;
    private Recipe recipe;
    private MenuItem saveMenuItem;
    private Uri resultUri;
    private LinearLayout layoutIngredients;
    private EditText lastIngredientsChild;
    private Chip actionChip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_my_recipe);
        txtName = (EditText) findViewById(R.id.txtName);
        txtPreparationTime = (EditText) findViewById(R.id.txtPreparationTime);
        txtCookingTime = (EditText) findViewById(R.id.txtCookingTime);
        imageView = (ImageView) findViewById(R.id.image);
        pbImageLoad = findViewById(R.id.pbImageLoad);
        layoutIngredients = findViewById(R.id.linearLayout_ingredients);
        lastIngredientsChild = (EditText)(layoutIngredients.getChildAt(layoutIngredients.getChildCount()-1));
        actionChip = findViewById(R.id.chip_ingredient);

        Intent intent = getIntent();
        Recipe recipe = (Recipe) intent.getSerializableExtra("Recipe");
        if (recipe == null){
            setTitle("New Recipe");
            recipe = new Recipe();
        }else{
            setTitle("Edit " + recipe.getTitle());
            List<String> ingredients = recipe.getIngredients();
            if (ingredients.size() > 0){
                ((EditText)findViewById(R.id.txtIngredients)).setText(ingredients.get(0));
                for (int i = 1; i < ingredients.size(); i++) {
                    EditText text = new EditText(AddOrEditMyRecipeActivity.this);
                    text.setHint(String.valueOf(i+1));
                    text.setText(ingredients.get(i));
                    layoutIngredients.addView(text);
                }
            }
            txtName.setText(recipe.getTitle());
            txtPreparationTime.setText(TimeParser.fromTotalMinute(recipe.getPreparationTime()).getTimeToDisplay());
            txtCookingTime.setText(TimeParser.fromTotalMinute(recipe.getCookingTime()).getTimeToDisplay());
            GlideUtil.showImage(recipe.getImageUrl(), this, imageView);
        }
        this.recipe = recipe;

        initListeners();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_activity_menu, menu);
        saveMenuItem = menu.findItem(R.id.save_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveRecipe();
                Toast.makeText(this,"Recipe saved", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                GlideUtil.showImage(resultUri.toString(), this, imageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("CropImage", error.getMessage());
            }
        }
    }

    private void initListeners(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(100,67)
                        .start(AddOrEditMyRecipeActivity.this);
            }
        });

        actionChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !lastIngredientsChild.getText().toString().trim().isEmpty()){
                    lastIngredientsChild = new EditText(AddOrEditMyRecipeActivity.this);
                    lastIngredientsChild.setHint(String.valueOf((layoutIngredients.getChildCount()+1)));
                    layoutIngredients.addView(lastIngredientsChild);
                }
                lastIngredientsChild.requestFocus();
            }
        });
        setTimeClickListener(txtCookingTime);
        setTimeClickListener(txtPreparationTime);
    }

    private void setTimeClickListener(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddOrEditMyRecipeActivity.this,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        editText.setText( String.format("%02d:%02d",hourOfDay,minute));
                    }
                }, 0, 0, true);
                mTimePicker.show();
            }
        });
    }

    private void disableSaveMenuItem() {
        saveMenuItem.setEnabled(false);
        pbImageLoad.setVisibility(View.VISIBLE);

    }

    private void enableSaveMenuItem() {
        saveMenuItem.setEnabled(true);
        pbImageLoad.setVisibility(View.INVISIBLE);
    }

    private void saveRecipe() {
        disableSaveMenuItem();

        recipe.setTitle(txtName.getText().toString());
        recipe.setPreparationTime(TimeParser.parse(txtPreparationTime.getText().toString()).totalMinute());
        recipe.setCookingTime(TimeParser.parse(txtCookingTime.getText().toString()).totalMinute());
        List<String> ingredients = new ArrayList<>();
        for (int i = 0; i < layoutIngredients.getChildCount(); i++) {
            EditText txt =((EditText) layoutIngredients.getChildAt(i));
            if (!txt.getText().toString().trim().isEmpty()){
                ingredients.add(txt.getText().toString());
            }
        }
        recipe.setIngredients(ingredients);

        //uploads image & thumbnail
        if (resultUri != null) {
            final StorageReference ref = FirebaseUtil.mStorageRef.child(resultUri.getLastPathSegment());
            ref.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    String imageName = task.getResult().getStorage().getPath();
                    recipe.setImageName(imageName);
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull final Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String downloadUri = task.getResult().toString();
                        recipe.setImageUrl(downloadUri);

                        File thumbnail = new File(resultUri.getPath());
                        try {
                            int maxWidth = getResources().getDimensionPixelSize(R.dimen.img_recipe_width);
                            int maxHeight = getResources().getDimensionPixelSize(R.dimen.img_recipe_height);
                            File compressedImageFile = new Compressor(AddOrEditMyRecipeActivity.this).setMaxWidth(maxWidth).setMaxHeight(maxHeight).compressToFile(thumbnail);
                            StorageReference refThumb = FirebaseUtil.mStorageRef.child("thumb").child(resultUri.getLastPathSegment());
                            refThumb.putFile(Uri.fromFile(compressedImageFile))
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            String thumbName = taskSnapshot.getTask().getResult().getStorage().getPath();
                                            recipe.setThumbName(thumbName);
                                            Task<Uri> thumbDownloadTask = taskSnapshot.getTask().getResult().getStorage().getDownloadUrl();
                                            thumbDownloadTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String thumbDownloadUri = uri.toString();
                                                    recipe.setThumbUrl(thumbDownloadUri);
                                                    FirebaseUtil.sendToDatabase(recipe);
                                                }
                                            });
                                        }
                                    });
                        } catch (IOException e) {
                            Log.e("Compressor", e.getMessage());
                        }
                    }
                }
            });
        } else {
            FirebaseUtil.sendToDatabase(recipe);
        }
    }

    private void backToList(){
        Intent intent = new Intent(this, MyRecipeListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
