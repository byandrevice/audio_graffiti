package com.scanner.audio_graffiti.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.scanner.audio_graffiti.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Bottom Sheet for Voice Responses, Threaded Replies, and Heart Reactions (campus_comments_sheet.xml).
 * Loads real comment data dynamically.
 */
public class VoiceCommentsSheet extends BottomSheetDialog {

    private final TextView memoTitle;
    private final TextView memoAuthorLocation;
    private final TextView textLikesCount;
    private final TextView textCommentsCount;
    private final MaterialCardView btnLikeMemo;
    private final Button btnRecordComment;
    private final RecyclerView commentsRecyclerView;

    private int likesCount = 0;
    private boolean isLiked = false;
    private final List<CommentItem> commentList = new ArrayList<>();
    private CommentsAdapter adapter;
    private final OnRecordReplyListener recordReplyListener;

    public interface OnRecordReplyListener {
        void onRecordReply(String replyToUsername);
    }

    public VoiceCommentsSheet(@NonNull Context context, String title, String authorLocation, OnRecordReplyListener listener) {
        this(context, title, authorLocation, new ArrayList<>(), 0, listener);
    }

    public VoiceCommentsSheet(@NonNull Context context, String title, String authorLocation, List<CommentItem> comments, int initialLikes, OnRecordReplyListener listener) {
        super(context);
        setContentView(R.layout.campus_comments_sheet);
        this.recordReplyListener = listener;
        this.likesCount = initialLikes;

        memoTitle = findViewById(R.id.memo_title);
        memoAuthorLocation = findViewById(R.id.memo_author_location);
        textLikesCount = findViewById(R.id.text_likes_count);
        textCommentsCount = findViewById(R.id.text_comments_count);
        btnLikeMemo = findViewById(R.id.btn_like_memo);
        btnRecordComment = findViewById(R.id.btn_record_comment);
        commentsRecyclerView = findViewById(R.id.comments_recycler_view);

        if (memoTitle != null) memoTitle.setText(title);
        if (memoAuthorLocation != null) memoAuthorLocation.setText(authorLocation);
        if (textLikesCount != null) textLikesCount.setText(likesCount + " Likes");

        // Heart Reaction Click -> Increment/Decrement Likes
        if (btnLikeMemo != null) {
            btnLikeMemo.setOnClickListener(v -> {
                isLiked = !isLiked;
                likesCount = isLiked ? likesCount + 1 : Math.max(0, likesCount - 1);
                if (textLikesCount != null) {
                    textLikesCount.setText(likesCount + " Likes");
                }
                Toast.makeText(context, isLiked ? "Liked voice drop! ❤️" : "Unliked voice drop", Toast.LENGTH_SHORT).show();
            });
        }

        // Record Voice Response Button
        if (btnRecordComment != null) {
            btnRecordComment.setOnClickListener(v -> {
                dismiss();
                if (recordReplyListener != null) {
                    recordReplyListener.onRecordReply(null);
                }
            });
        }

        if (comments != null) {
            commentList.addAll(comments);
        }

        updateCommentsCount();

        if (commentsRecyclerView != null) {
            commentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new CommentsAdapter(commentList, replyTo -> {
                dismiss();
                if (recordReplyListener != null) {
                    recordReplyListener.onRecordReply(replyTo);
                }
            });
            commentsRecyclerView.setAdapter(adapter);
        }
    }

    private void updateCommentsCount() {
        if (textCommentsCount != null) {
            textCommentsCount.setText(commentList.size() + (commentList.size() == 1 ? " comment" : " comments"));
        }
    }

    // Comment Data Class
    public static class CommentItem {
        String author;
        String timeAgo;
        String duration;
        String replyToUser;
        int likes;

        public CommentItem(String author, String timeAgo, String duration, String replyToUser, int likes) {
            this.author = author;
            this.timeAgo = timeAgo;
            this.duration = duration;
            this.replyToUser = replyToUser;
            this.likes = likes;
        }
    }

    // RecyclerView Adapter for Threaded Comments
    private static class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
        private final List<CommentItem> items;
        private final OnCommentReplyClickListener clickListener;

        public interface OnCommentReplyClickListener {
            void onReplyClick(String username);
        }

        public CommentsAdapter(List<CommentItem> items, OnCommentReplyClickListener clickListener) {
            this.items = items;
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voice_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CommentItem item = items.get(position);
            holder.author.setText(item.author);
            holder.time.setText(item.timeAgo);
            holder.playBtn.setText("▶ " + item.duration);

            if (item.replyToUser != null && !item.replyToUser.isEmpty()) {
                holder.replyTargetContainer.setVisibility(View.VISIBLE);
                holder.threadAccentBar.setVisibility(View.VISIBLE);
                holder.replyToText.setText("↩ Replying to " + item.replyToUser);
            } else {
                holder.replyTargetContainer.setVisibility(View.GONE);
                holder.threadAccentBar.setVisibility(View.GONE);
            }

            holder.replyBtn.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onReplyClick(item.author);
                }
            });

            holder.playBtn.setOnClickListener(v -> 
                Toast.makeText(v.getContext(), "Playing voice reply from " + item.author, Toast.LENGTH_SHORT).show());

            holder.likeBtn.setOnClickListener(v -> {
                item.likes++;
                Toast.makeText(v.getContext(), "Liked " + item.author + "'s reply (❤️ " + item.likes + ")", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView author, time, replyToText, replyBtn;
            Button playBtn;
            View threadAccentBar, replyTargetContainer;
            MaterialCardView likeBtn;

            ViewHolder(View view) {
                super(view);
                author = view.findViewById(R.id.comment_author);
                time = view.findViewById(R.id.comment_time);
                replyToText = view.findViewById(R.id.comment_reply_to_text);
                replyBtn = view.findViewById(R.id.btn_reply_to_comment);
                playBtn = view.findViewById(R.id.btn_play_comment);
                threadAccentBar = view.findViewById(R.id.thread_accent_bar);
                replyTargetContainer = view.findViewById(R.id.reply_target_container);
                likeBtn = view.findViewById(R.id.btn_like_comment);
            }
        }
    }
}
