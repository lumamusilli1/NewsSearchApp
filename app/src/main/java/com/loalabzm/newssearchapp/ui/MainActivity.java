package com.loalabzm.newssearchapp.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.loalabzm.newssearchapp.R;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        getLocale();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new HomeFragment())
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(Gravity.LEFT);
        } else if (item.getItemId() == R.id.action_language) {
            makeLanguageSelectionDialogue();
        } else if (item.getItemId() == R.id.action_info) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.screen_info))
                    .setMessage(getString(R.string.home_screen_info))
                    .setPositiveButton(getString(R.string.Ok),
                            (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        }
        return false;
    }

    private void makeLanguageSelectionDialogue() {
        String lan = sharedPreferences.getString("KEY", "en");
        int selectedItem;
        if (lan.equals("en")) {
            selectedItem = 0;
        } else {
            selectedItem = 1;
        }

        final String[] languages = {"English", "French"};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.chooseapp_language))
                .setSingleChoiceItems(languages, selectedItem, (dialogInterface, i) -> {
                    if (i == 0) {
                        setLocale("en");
                    } else if (i == 1) {
                        setLocale("fr");
                    }
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void setLocale(String langCode) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final Locale myLocale = new Locale(langCode);
        final Resources res = getResources();
        final DisplayMetrics dm = res.getDisplayMetrics();
        final Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        editor.putString("KEY", langCode);
        editor.apply();
    }

    private void getLocale() {
        setLocale(sharedPreferences.getString("KEY", "en"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            drawer.closeDrawers();
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
        } else {
            finishAffinity();
        }
        return true;
    }
}