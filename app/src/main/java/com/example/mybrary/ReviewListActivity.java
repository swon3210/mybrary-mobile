package com.example.mybrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybrary.model.ReviewModel;
import com.example.mybrary.ui.gallery.GalleryFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nonnull;

public class ReviewListActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    private RecyclerView reviewList;
    private FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        reviewList  = findViewById(R.id.reviewList);
        final Intent it = new Intent(this, ReviewItemActivity.class);

        Query query = firebaseFirestore.collection("reviews").whereEqualTo("reviewerId", user.getUid());

        FirestoreRecyclerOptions<ReviewModel> options = new FirestoreRecyclerOptions.Builder<ReviewModel>().setQuery(query, ReviewModel.class).build();

        adapter = new FirestoreRecyclerAdapter<ReviewModel, ReviewListActivity.ReviewsViewHolder>(options) {
            @NonNull
            @Override
            public ReviewListActivity.ReviewsViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
                return new ReviewListActivity.ReviewsViewHolder(view);
            };

            @Override
            protected void onBindViewHolder (@Nonnull ReviewListActivity.ReviewsViewHolder holder, int position, @Nonnull ReviewModel model) {
                final String reviewTitle = model.getTitle();
                final String reviewText = model.getText();

                holder.reviewTitle.setText(model.getTitle());
                holder.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (user == null) {
                            Toast.makeText(ReviewListActivity.this, "로그인 후 이용해주십시오.", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseFirestore.collection("reviews").whereEqualTo("title", reviewTitle).whereEqualTo("text", reviewText).whereEqualTo("reviewerId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("아이디!", document.getId());
                                            it.putExtra("id", document.getId());
                                            it.putExtra("reviewTitle", reviewTitle);
                                            it.putExtra("reviewText", reviewText);
                                            it.putExtra("editMode", true);
                                            it.putExtra("reviewerId", user.getUid());
                                            it.putExtra("reviewer", user.getEmail());
                                        }
                                        startActivity(it);
                                    }
                                }
                            });
                        }


                    }
                });
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user == null) {
                            Toast.makeText(ReviewListActivity.this, "로그인 후 이용해주십시오.", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseFirestore.collection("reviews").whereEqualTo("title", reviewTitle).whereEqualTo("text", reviewText).whereEqualTo("reviewerId", user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            firebaseFirestore.collection("reviews").document(document.getId()).delete();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            };
        };

        reviewList.setHasFixedSize(true);
        reviewList.setLayoutManager(new LinearLayoutManager(this));
        reviewList.setAdapter(adapter);
    }

    private class ReviewsViewHolder extends RecyclerView.ViewHolder {
        private TextView reviewTitle;
        private ImageButton editButton;
        private ImageButton deleteButton;
        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewTitle = itemView.findViewById(R.id.reviewTitle);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}
