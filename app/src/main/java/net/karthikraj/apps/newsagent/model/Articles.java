package net.karthikraj.apps.newsagent.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by karthik on 30/10/17.
 */

public class Articles implements Parcelable {
    private String status;
    private String source;
    private String sortBy;
    private List<Article> articles = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.source);
        dest.writeString(this.sortBy);
        dest.writeTypedList(this.articles);
    }

    public Articles() {
    }

    protected Articles(Parcel in) {
        this.status = in.readString();
        this.source = in.readString();
        this.sortBy = in.readString();
        this.articles = in.createTypedArrayList(Article.CREATOR);
    }

    public static final Parcelable.Creator<Articles> CREATOR = new Parcelable.Creator<Articles>() {
        @Override
        public Articles createFromParcel(Parcel source) {
            return new Articles(source);
        }

        @Override
        public Articles[] newArray(int size) {
            return new Articles[size];
        }
    };
}
