package natanel.android.firebasejavaproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import natanel.android.firebasejavaproject.database.AddFoodToDatabase;
import natanel.android.firebasejavaproject.database.AddUserInfoToDatabase;
import natanel.android.firebasejavaproject.database.UserDatabase;
import natanel.android.firebasejavaproject.databinding.ActivityMainBinding;
import natanel.android.firebasejavaproject.notification.PushNotificationService;
import natanel.android.firebasejavaproject.storage.UploadActivity;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Get the curent Token for notifications
        FirebaseMessaging.getInstance().getToken()
                .addOnFailureListener( task -> Log.d(TAG, "Fetching FCM registration token failed", task.getCause()))
                .addOnCompleteListener(task -> {

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    Log.d(TAG, "Token: " + token);
                    Toast.makeText(MainActivity.this, "Token: " + token, Toast.LENGTH_LONG).show();
                });

        //SignIn Button
        binding.btnSignIn.setOnClickListener(view -> {
            String email = binding.etEmail.getText().toString();
            String pass = binding.etPassword.getText().toString();

            if (!email.isEmpty() && !pass.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Sign In:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Logs and messages
                            assert user != null;
                            Log.d("user: ", user.toString());
                            toastMessage("Sign In:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Sign In :failure", task.getException());
                            toastMessage("Sign In :failure");
                        }
                    }
                });
            } else {
                toastMessage("you didn't fill in all the fields");
            }
        });

        //SignOut Button
        binding.btnSignOut.setOnClickListener(view -> {
            mAuth.signOut();
            toastMessage("Signing Out!");
        });

        //Add Food To Database Button
        binding.btnAddFood.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddFoodToDatabase.class);
            startActivity(intent);
        });

        binding.btnAddUserData.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddUserInfoToDatabase.class);
            startActivity(intent);
        });

        //User Data Button
        binding.btnUserData.setOnClickListener(view -> {
            Intent intent = new Intent(this, UserDatabase.class);
            startActivity(intent);
        });

        binding.btnUploadImage.setOnClickListener(view -> {
            Intent intent = new Intent(this, UploadActivity.class);
            startActivity(intent);
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            currentUser.reload();
        }
        //updateUI(currentUser);
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}