package com.example.mybrary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybrary.model.ReviewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewItemActivity extends AppCompatActivity {

    private ReviewModel review;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_item);
        getSupportActionBar().hide();

        Intent intent = getIntent();

        boolean editMode = intent.getBooleanExtra("editMode", false);
        final String reviewText = intent.getStringExtra("reviewText");
        final String reviewTitle = intent.getStringExtra("reviewTitle");
        final String id = intent.getStringExtra("id");
        final String reviewerId = intent.getStringExtra("reviewerId");
        final String reviewer = intent.getStringExtra("reviewer");
        db = FirebaseFirestore.getInstance();

        final EditText reviewTitleEdit = findViewById(R.id.reviewTitleEdit);
        reviewTitleEdit.setText(reviewTitle);
        final EditText reviewTextEdit = findViewById(R.id.reviewTextEdit);
        reviewTextEdit.setText(reviewText);
        ImageButton editButton = findViewById(R.id.editCompleteButton);
        GridLayout editArea = findViewById(R.id.editArea);
        TextView reviewerText = findViewById(R.id.reviewer);

        reviewerText.setText(reviewer);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> review = new HashMap<>();
                String newTitle = reviewTitleEdit.getText().toString();
                String newText = reviewTextEdit.getText().toString();
                review.put("reviewerId", reviewerId);
                review.put("reviewer", reviewer);
                review.put("title", newTitle);
                review.put("text", newText);
                db.collection("reviews").document(id).update(review).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ReviewItemActivity.this, "리뷰가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ReviewItemActivity.this, ReviewListActivity.class));
                    }
                });
            }
        });

        if (editMode == false) {
            editArea.setVisibility(View.GONE);
            reviewTextEdit.setFocusable(false);
            reviewTextEdit.setClickable(false);
            reviewTitleEdit.setFocusable(false);
            reviewTitleEdit.setClickable(false);
        }
    }
}
