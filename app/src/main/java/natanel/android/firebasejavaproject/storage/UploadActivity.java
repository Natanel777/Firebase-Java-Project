package natanel.android.firebasejavaproject.storage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import natanel.android.firebasejavaproject.databinding.UploadLayoutBinding;

public class UploadActivity extends AppCompatActivity {

    private final static String TAG = "UploadActivity";
    private UploadLayoutBinding binding;
    private StorageReference storageRef;
    private FirebaseAuth auth;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Uri imageUriId = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Binding
        binding = UploadLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Loading
        ProgressBar progressBar = binding.progressBar;
        TextView textLoading = binding.textLoading;

        storageRef = FirebaseStorage.getInstance().getReference();

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Register Activity Result Launcher
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);

                        Glide.with(this)
                                .load(uri)
                                .fitCenter()
                                .into(binding.btnChoseImage);
                        imageUriId = uri;
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                        toastMessage("No Image selected");
                    }
                });

        //Select Image Button
        binding.btnChoseImage.setOnClickListener(view -> {
            selectImage();
        });

        binding.btnUploadToStorage.setOnClickListener(view -> {

            FirebaseUser user = auth.getCurrentUser();
            String name = binding.editTextImageName.getText().toString().trim();;

            if (user != null) {
                String userID = user.getUid();
                if (!name.isEmpty()) {
                    if (imageUriId != null) {

                        //Show progress indicators
                        progressBar.setVisibility(View.VISIBLE);
                        textLoading.setVisibility(View.VISIBLE);

                        // Construct the storage reference path
                        String imageName = name + ".jpg";
                        String storagePath = "images/users/" + userID + "/" + imageName;
                        Log.d(TAG, "Storage Path: " + storagePath);
                        StorageReference imagesRef = storageRef.child(storagePath);

                        // Upload image to Firebase Storage
                        imagesRef.putFile(imageUriId)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Hide progress indicators on success
                                    progressBar.setVisibility(View.GONE);
                                    textLoading.setVisibility(View.GONE);
                                    // Image uploaded successfully
                                    imagesRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                        String imageUrl = downloadUri.toString();
                                        // Here you can handle the uploaded image URL
                                        // For example, save this URL to Firebase Database or use it elsewhere
                                        toastMessage("Image uploaded successfully: " + imageUrl);
                                    }).addOnFailureListener(e -> {
                                        // Handle any errors retrieving the download URL
                                        toastMessage("Failed to get image URL: " + e.getMessage());
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Hide progress indicators on failure
                                    progressBar.setVisibility(View.GONE);
                                    textLoading.setVisibility(View.GONE);
                                    // Handle unsuccessful uploads
                                    toastMessage("Image upload failed: " + e.getMessage());
                                });
                    } else {
                        toastMessage("Please Select Image");
                    }
                } else {
                    toastMessage("Please Provide Name");
                }
            } else {
                toastMessage("User not authenticated");
            }
        });

    }

    private void selectImage() {

        //Old Way to pick images

//        Intent intent = new Intent().setAction(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        intent.getAction();
//        startActivity(intent);

        // Launch the media picker using the registered launcher
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
