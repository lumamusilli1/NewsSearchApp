package com.loalabzm.newssearchapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.loalabzm.newssearchapp.R;
import com.loalabzm.newssearchapp.database.NewsSearchDBHelper;
import com.loalabzm.newssearchapp.model.Article;

public class ArticleDetailActivity extends AppCompatActivity {

    Article article;
    MaterialButton btnAddToFavorites, btnUnFavorite;
    NewsSearchDBHelper newsSearchDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_article_detail);

        article = (Article) getIntent().getSerializableExtra("selected_article");
        setDetails();
    }

    private void setDetails() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvSectionName = findViewById(R.id.tvSectionName);
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvLink = findViewById(R.id.tvLink);
        btnAddToFavorites = findViewById(R.id.btnAddToFavorite);
        btnUnFavorite = findViewById(R.id.btnUnFavorite);
        newsSearchDBHelper = new NewsSearchDBHelper(this);

        if (newsSearchDBHelper.isArticleAlreadyInFavorites(article.getId())) {
            btnUnFavorite.setVisibility(View.VISIBLE);
        } else {
            btnUnFavorite.setVisibility(View.GONE);
        }

        tvTitle.setText(article.getWebTitle());
        tvSectionName.setText(article.getSectionName());
        tvDate.setText(article.getWebPublicationDate());
        tvLink.setText(article.getWebUrl());

        tvLink.setMovementMethod(LinkMovementMethod.getInstance());

        tvLink.setOnClickListener(view -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("webUrl", article.getWebUrl());
            startActivity(intent);
        });

        btnAddToFavorites.setOnClickListener(view -> {
            if (newsSearchDBHelper.isArticleAlreadyInFavorites(article.getId())) {
                Toast.makeText(this, "Already in favorites", Toast.LENGTH_SHORT).show();
            } else {
                newsSearchDBHelper.makeFavorite(article);
            }
        });

        btnUnFavorite.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.alert_message))
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    newsSearchDBHelper.unFavorite(article.getId());
                    Toast.makeText(this, getString(R.string.removed_from_fav), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show());
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
                    .setMessage(getString(R.string.home_screen_info))
                    .setPositiveButton(getString(R.string.Ok),
                            (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }

        return true;
    }
}