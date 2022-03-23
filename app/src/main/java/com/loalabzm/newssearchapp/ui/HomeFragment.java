package com.loalabzm.newssearchapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.loalabzm.newssearchapp.ArticlesAdapter;
import com.loalabzm.newssearchapp.R;
import com.loalabzm.newssearchapp.database.NewsSearchDBHelper;
import com.loalabzm.newssearchapp.model.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    public static final String BASE_URL = "https://content.guardianapis.com/search?api-key=1fb36b70-1588-4259-b703-2570ea1fac6a&q=";
    private static final String TAG = "myTag";

    ConstraintLayout constraintLayout;
    ListView listView;
    EditText etSearch;
    MaterialButton btnSearch;
    List<Article> articleList;
    ProgressBar progressBar;

    String query;
    NewsSearchDBHelper newsSearchDBHelper;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        showArticlesIfExists();

        btnSearch.setOnClickListener(view1 -> {
            query = etSearch.getText().toString().trim();
            if (!query.equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                new SearchArticles().execute();
            } else {
                Toast.makeText(requireContext(),
                        requireContext().getString(R.string.no_query_message),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showArticlesIfExists() {
        List<Article> articles = newsSearchDBHelper.getLastSavedArticles();
        if (articles != null) {
            setData(articles);
        }
    }

    private void init(View view) {
        listView = view.findViewById(R.id.newsListView);
        etSearch = view.findViewById(R.id.etSearch);
        btnSearch = view.findViewById(R.id.btnSearch);
        constraintLayout = view.findViewById(R.id.root);
        progressBar = view.findViewById(R.id.progressBar);
        articleList = new ArrayList<>();
        newsSearchDBHelper = new NewsSearchDBHelper(requireContext());
    }

    class SearchArticles extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(BASE_URL + query)
                    .method("GET", null)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                    JSONObject responseObject = jsonObject.getJSONObject("response");
                    JSONArray results = responseObject.getJSONArray("results");
                    if (results.length() > 0) {
                        articleList.clear();
                        newsSearchDBHelper.clearSavedList();
                        makeListView(results);
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(constraintLayout, getString(R.string.no_results_message), Snackbar.LENGTH_SHORT).show();
                            etSearch.setText("");
                        });
                    }
                } else {
                    Log.d(TAG, "doInBg: Something Went Wrong" + response.body());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: " + e);
            }

            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

    }


    private void makeListView(JSONArray results) {
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject jsonObject = results.getJSONObject(i);
                String id = jsonObject.getString("id");
                String type = jsonObject.getString("type");
                String sectionId = jsonObject.getString("sectionId");
                String sectionName = jsonObject.getString("sectionName");
                String webPublicationDate = jsonObject.getString("webPublicationDate");
                String webTitle = jsonObject.getString("webTitle");
                String webUrl = jsonObject.getString("webUrl");
                String apiUrl = jsonObject.getString("apiUrl");
                String isHosted = jsonObject.getString("isHosted");
                String pillarId = jsonObject.getString("pillarId");
                String pillarName = jsonObject.getString("pillarName");
                Article article = new Article(id, type, sectionId, sectionName, webPublicationDate, webTitle, webUrl, apiUrl, isHosted,
                        pillarId, pillarName);
                newsSearchDBHelper.saveArticle(article);
                articleList.add(article);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "makeListView: " + e);
            }
        }
        setData(articleList);
    }

    private void setData(List<Article> articles) {
        requireActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            ArticlesAdapter articlesAdapter = new ArticlesAdapter(articles);
            listView.setAdapter(articlesAdapter);
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent intent = new Intent(requireContext(), ArticleDetailActivity.class);
                intent.putExtra("selected_article", articles.get(i));
                startActivity(intent);
            });
        });
    }

}