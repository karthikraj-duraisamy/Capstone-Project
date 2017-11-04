package net.karthikraj.apps.newsagent.feeds;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import net.karthikraj.apps.newsagent.R;
import net.karthikraj.apps.newsagent.model.Article;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 1/11/17.
 */

public class NewsFeedsAdapter extends RecyclerView.Adapter<NewsFeedsAdapter.ViewHolder> {

    private Context mContext;
    private ArticleClickListener articleClickListener;
    private List<Article> articleList;
    private FirebaseAnalytics mFirebaseAnalytics;

    public interface ArticleClickListener {
        void onArticleClicked(Article article, View imageView, View titleView, View authorView, View dateView);
    }


    public NewsFeedsAdapter(Context context, ArticleClickListener listener) {
        this.mContext = context;
        this.articleClickListener = listener;
        articleList = new ArrayList<>();
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

    }

    public void updateDataSet(List<Article> list) {
        articleList = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tvSourceName) TextView sourceNameText;
        @BindView(R.id.tvNewsTitle) TextView newsTitleText;
        @BindView(R.id.tvAuthorName) TextView authorNameText;
        @BindView(R.id.tvPusblishedAt) TextView publishedAtText;
        @BindView(R.id.ivNewsFeedImage)
        ImageView newsFeedImage;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, articleList.get(getAdapterPosition()).getTitle());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            articleClickListener.onArticleClicked(articleList.get(getAdapterPosition()), newsFeedImage, newsTitleText, authorNameText, publishedAtText);
        }
    }

    @Override
    public NewsFeedsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_news_feeds, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = articleList.get(position);

        try {
            Picasso.with(holder.newsFeedImage.getContext()).
                    load(article.getUrlToImage()).
                    placeholder(R.drawable.news_feed_placeholder).
                    error(R.drawable.news_feed_placeholder).
                    fit().
                    into(holder.newsFeedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.sourceNameText.setText(article.getSourceName());
        holder.newsTitleText.setText(article.getTitle());
        holder.authorNameText.setText(article.getAuthor());
        holder.publishedAtText.setText(article.getPublishedAt());

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}

