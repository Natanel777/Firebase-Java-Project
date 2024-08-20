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

import natanel.android.firebasejavaproject.databinding.AddFoodToDatabaseLayoutBinding;

public class AddFoodToDatabase extends AppCompatActivity {
    private static final String TAG = "AddToDatabase";

    private AddFoodToDatabaseLayoutBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Binding
        binding = AddFoodToDatabaseLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Declare the database reference object. this is what we use to access the database
        //Note: unless you are signed in, this will not be usable
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue(Object.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //Add New Food Button
        binding.btnAddNewFood.setOnClickListener(view -> {
            try {
                String newFood = binding.editTextFood.getText().toString().trim();
                if (!newFood.isEmpty()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userID = user.getUid(); // get UserID our database starts with userID value
                        myRef
                                .child(userID)
                                .child("Food")
                                .child("Favorite Foods")
                                .child(newFood).setValue(true);

                        binding.editTextFood.setText("");
                        toastMessage("Food added to database");
                    } else {
                        toastMessage("User not authenticated");
                    }
                } else {
                    toastMessage("Please enter a food item");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding food to database", e);
                toastMessage("Error: " + e.getMessage());
            }
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
