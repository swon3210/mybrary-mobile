package com.example.mybrary.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybrary.LoginActivity;
import com.example.mybrary.R;
import com.example.mybrary.ReviewItemActivity;
import com.example.mybrary.ReviewListActivity;
import com.example.mybrary.model.ReviewModel;

import com.example.mybrary.ui.review.ReviewViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.annotation.Nonnull;

public class GalleryFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

//    List<DocumentSnapshot> reviewDocumentList;

    private GalleryViewModel galleryViewModel;
    private RecyclerView reviewList;
    private FirestoreRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        reviewList  = root.findViewById(R.id.reviewList);

        Button loginButton = root.findViewById(R.id.loginButton);
        Button logoutButton = root.findViewById(R.id.logoutButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Toast.makeText(getActivity(), "로그아웃 됨", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        if (user == null) {
            logoutButton.setVisibility(View.GONE);
        } else {
            loginButton.setVisibility(View.GONE);
        }

        Button button1 = root.findViewById(R.id.allListButton); // click시 Fragment를 전환할 event를 발생시킬 버튼을 정의합니다.
        ImageButton button2 = root.findViewById(R.id.editButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user == null) {
                    Toast.makeText(getActivity(), "로그인 후 이용해주십시오.", Toast.LENGTH_SHORT).show();
                } else {
                    moveToReviewListActivity(v);
                }
            }
        });

        Query query = firebaseFirestore.collection("reviews");

        FirestoreRecyclerOptions<ReviewModel> options = new FirestoreRecyclerOptions.Builder<ReviewModel>().setQuery(query, ReviewModel.class).build();

        adapter = new FirestoreRecyclerAdapter<ReviewModel, ReviewsViewHolder>(options) {
            @NonNull
            @Override
            public ReviewsViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
                return new ReviewsViewHolder(view);
            };
            Intent it = new Intent(getActivity(), ReviewItemActivity.class);
            @Override
            protected void onBindViewHolder (@Nonnull ReviewsViewHolder holder, int position, @Nonnull ReviewModel model) {
                final String reviewTitle = model.getTitle();
                final String reviewText = model.getText();

                holder.reviewTitle.setText(model.getTitle());
                holder.reviewText.setText(model.getText());
                holder.viewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user == null) {
                            Toast.makeText(getActivity(), "로그인 후 이용해주십시오.", Toast.LENGTH_SHORT).show();
                        } else {
                            it.putExtra("reviewTitle", reviewTitle);
                            it.putExtra("reviewText", reviewText);
                            it.putExtra("editMode", false);
                            it.putExtra("reviewerId", user.getUid());
                            it.putExtra("reviewer", user.getEmail());
                            Log.i("ActivityTest", "onClick!");

                            startActivity(it);
                        }
                    }
                });
            };
        };

        reviewList.setHasFixedSize(true);
        reviewList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        reviewList.setAdapter(adapter);

        return root;
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

    private class ReviewsViewHolder extends RecyclerView.ViewHolder {

        private TextView reviewTitle;
        private TextView reviewText;
        private ImageButton viewButton;

        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewTitle = itemView.findViewById(R.id.reviewTitle);
            reviewText = itemView.findViewById(R.id.reviewText);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
    }

    public void moveToReviewListActivity(View v) {
        Intent it = new Intent(getActivity(), ReviewListActivity.class);

        Log.i("ActivityTest", "onClick!");

        startActivity(it);
    }

    public void moveToReviewItemActivity(View v) {

        Intent it = new Intent(getActivity(), ReviewItemActivity.class);

        Log.i("ActivityTest", "onClick!");

        startActivity(it);
    }


}
