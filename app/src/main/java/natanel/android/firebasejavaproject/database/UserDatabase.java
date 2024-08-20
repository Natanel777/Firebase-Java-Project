package natanel.android.firebasejavaproject.database;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Objects;

import natanel.android.firebasejavaproject.UserInformation;
import natanel.android.firebasejavaproject.databinding.UserDatabaseLayoutBinding;

//AppCompatActivity like Fragment support new Features
public class UserDatabase extends AppCompatActivity {
    private static final String TAG = "UserDatabase";

    private UserDatabaseLayoutBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;

    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Binding
        binding = UserDatabaseLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mListView = binding.listViewUserData;

        // Check if user is signed in (non-null)
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            toastMessage("User not authenticated");
        } else {
            // User is signed in, proceed with database operations
            userID = user.getUid(); // Get user ID

            // Set up ValueEventListener to listen for changes in database data
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "Snapshot: " + snapshot);
                    showData(snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database Error: " + error.getMessage());
                }
            });
        }
    }

    private void showData(DataSnapshot snapshot) {
        ArrayList<String> arrayList = new ArrayList<>(); // ArrayList to hold user data for ListView

        if (snapshot.exists()) {
            DataSnapshot usersSnapshot = snapshot.child("users"); // the parent node containing user data

            for (DataSnapshot ds : usersSnapshot.getChildren()) {
                if (Objects.equals(ds.getKey(), userID)) { // Check if the current snapshot user ID same as the logged-in user ID

                    // Retrieve user data from the snapshot
                    String name = ds.child("name").getValue(String.class);
                    String email = ds.child("email").getValue(String.class);
                    String phoneNum = ds.child("phone_num").getValue(String.class);

                    // Log user data for debugging
                    Log.d(TAG, "showData: name: " + name);
                    Log.d(TAG, "showData: email: " + email);
                    Log.d(TAG, "showData: phone_num: " + phoneNum);

                    // Create a UserInformation object and populate it with retrieved data
                    UserInformation userInfo = new UserInformation();
                    userInfo.setEmail(email); // Set email
                    userInfo.setName(name); // Set name
                    userInfo.setPhone_num(phoneNum); // Set phone number

                    // Log user data from UserInformation object (for verification)
                    Log.d(TAG, "showData: name: " + userInfo.getName());
                    Log.d(TAG, "showData: email: " + userInfo.getEmail());
                    Log.d(TAG, "showData: phone_num: " + userInfo.getPhone_num());

                    // Add user data to the ArrayList for ListView display
                    arrayList.add(userInfo.getName());
                    arrayList.add(userInfo.getEmail());
                    arrayList.add(userInfo.getPhone_num());

                    // Set up ListView adapter to display user data
                    if (mListView != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserDatabase.this, android.R.layout.simple_list_item_1, arrayList);
                        mListView.setAdapter(adapter);
                    } else {
                        Log.e(TAG, "mListView is null"); // Log error if mListView is null
                    }
                }
            }
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
