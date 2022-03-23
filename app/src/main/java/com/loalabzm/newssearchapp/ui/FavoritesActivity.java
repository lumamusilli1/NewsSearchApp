package com.loalabzm.newssearchapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.loalabzm.newssearchapp.ArticlesAdapter;
import com.loalabzm.newssearchapp.R;
import com.loalabzm.newssearchapp.database.NewsSearchDBHelper;
import com.loalabzm.newssearchapp.model.Article;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    ListView listView;
    TextView tvNoFav;
    NewsSearchDBHelper newsSearchDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_favorites);

        setTitle(getString(R.string.all_favorites));
        setListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListView();
    }

    private void setListView() {
        newsSearchDBHelper = new NewsSearchDBHelper(this);
        tvNoFav = findViewById(R.id.tvNoFavorites);
        listView = findViewById(R.id.favoritesListView);
        List<Article> articles = newsSearchDBHelper.getAllFavorites();
        if (articles.size() > 0) {
            tvNoFav.setVisibility(View.GONE);
        } else {
            tvNoFav.setVisibility(View.VISIBLE);
        }
        ArticlesAdapter articlesAdapter = new ArticlesAdapter(articles);
        listView.setAdapter(articlesAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra("selected_article", articles.get(i));
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_info) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.screen_info))
                    .setMessage(getString(R.string.favorite_screen_info))
                    .setPositiveButton(getString(R.string.Ok),
                            (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }
        return true;
    }
}