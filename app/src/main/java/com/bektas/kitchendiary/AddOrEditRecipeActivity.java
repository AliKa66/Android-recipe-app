package com.bektas.kitchendiary;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import id.zelory.compressor.Compressor;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bektas.kitchendiary.model.Recipe;
import com.bektas.kitchendiary.util.FirebaseUtil;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;

public class AddOrEditRecipeActivity extends AppCompatActivity {
    private EditText txtName;
    private EditText txtPreparationTime;
    private EditText txtCookingTime;
    private EditText txtIngredients;
    private ImageView imageView;
    private ProgressBar pbImageLoad;
    private Recipe recipe;
    private MenuItem saveMenuItem;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_recipe);
//        FirebaseUtil.openFbReference("recipes", this);

        txtName = (EditText) findViewById(R.id.txtName);
        txtPreparationTime = (EditText) findViewById(R.id.txtPreparationTime);
        txtCookingTime = (EditText) findViewById(R.id.txtCookingTime);
        txtIngredients = findViewById(R.id.txtIngredients);
        imageView = (ImageView) findViewById(R.id.image);
        pbImageLoad = findViewById(R.id.pbImageLoad);

        Intent intent = getIntent();
        Recipe recipe = (Recipe) intent.getSerializableExtra("Recipe");
        if (recipe == null){
            recipe = new Recipe();
        }
        this.recipe = recipe;

        txtName.setText(recipe.getTitle());
        txtPreparationTime.setText(recipe.getPreparationTime());
        txtCookingTime.setText(recipe.getCookingTime());
        txtIngredients.setText(recipe.getIngredients());
        if (recipe.getImageUrl() != null){
            showImage(recipe.getImageUrl());
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(100,67)
                        .start(AddOrEditRecipeActivity.this);
            }
        });
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
                clear();
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
                showImage(resultUri.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("CropImage", error.getMessage());
            }
        }
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
        recipe.setPreparationTime(txtPreparationTime.getText().toString());
        recipe.setCookingTime(txtCookingTime.getText().toString());
        recipe.setIngredients(txtIngredients.getText().toString());

        //uploads image & thumbnail
        if (resultUri != null){
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
                            File compressedImageFile = new Compressor(AddOrEditRecipeActivity.this).setMaxWidth(maxWidth).setMaxHeight(maxHeight).compressToFile(thumbnail);
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
                                                    sendRecipeToFirebase(recipe);
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
        }else {
            sendRecipeToFirebase(recipe);
        }

    }

    private void sendRecipeToFirebase(Recipe recipe) {
        if (recipe.getId() == null) {
            FirebaseUtil.mDatabaseReference.push().setValue(recipe);
        } else {
            FirebaseUtil.mDatabaseReference.child(recipe.getId()).setValue(recipe);
        }
    }

    private void backToList(){
        Intent intent = new Intent(this,RecipeListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        finish();
    }

    //TODO Create GlideApp
    private void showImage(String url){
        if (url != null && !url.isEmpty()){
//            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
            circularProgressDrawable.setStrokeWidth(5);
            circularProgressDrawable.setCenterRadius(30);
            circularProgressDrawable.start();
            Glide.with(this).load(url).placeholder(circularProgressDrawable).into(imageView);
        }
    }

    private void clear() {
        txtName.setText("");
        txtPreparationTime.setText("");
        txtCookingTime.setText("");
        txtIngredients.setText("");
    }
}
