package natanel.android.firebasejavaproject.database;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import natanel.android.firebasejavaproject.UserInformation;
import natanel.android.firebasejavaproject.databinding.AddUserInfoToDatabaseBinding;

public class AddUserInfoToDatabase extends AppCompatActivity {

    private static final String TAG = "AddUserInfoToDatabase";

    private AddUserInfoToDatabaseBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Binding
        binding = AddUserInfoToDatabaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        FirebaseUser user = mAuth.getCurrentUser();// Check if user is signed in (non-null)
        if (user == null) {
            toastMessage("User not authenticated");
        } else {
            userID = user.getUid(); // Get user ID

            // Set up ValueEventListener to listen for changes in database data
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "Snapshot: " + snapshot);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database Error: " + error.getMessage());
                }
            });

            binding.btnSubmit.setOnClickListener(view -> {
                Log.d(TAG, "onClick: Submit clicked");

                String name = binding.userTextName.getText().toString();
                String email = binding.userTextEmail.getText().toString();
                String phoneNum = binding.userTextPhone.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !phoneNum.isEmpty()){
                    UserInformation userInformation = new UserInformation(name,email,phoneNum);
                    myRef.child("users").child(userID).setValue(userInformation);

                    toastMessage("New Info Has Benn Saved!");

                    binding.userTextName.setText("");
                    binding.userTextEmail.setText("");
                    binding.userTextPhone.setText("");
                } else {
                    toastMessage("fill out all the fields");
                }
            });
        }
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
