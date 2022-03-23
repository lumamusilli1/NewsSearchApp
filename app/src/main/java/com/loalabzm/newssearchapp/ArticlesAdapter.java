package com.loalabzm.newssearchapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loalabzm.newssearchapp.model.Article;

import java.util.List;

public class ArticlesAdapter extends BaseAdapter {

    List<Article> articles;

    public ArticlesAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @SuppressLint("ViewHolder")
        View view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_article, viewGroup, false);
        TextView tvTitle = view1.findViewById(R.id.tvTitle);
        TextView tvSectionName = view1.findViewById(R.id.tvSectionName);
        TextView tvDate = view1.findViewById(R.id.tvDate);
        tvTitle.setText(articles.get(i).getWebTitle());
        tvSectionName.setText(articles.get(i).getSectionName());
        tvDate.setText(articles.get(i).getWebPublicationDate().substring(0, 10));
        return view1;
    }
}
