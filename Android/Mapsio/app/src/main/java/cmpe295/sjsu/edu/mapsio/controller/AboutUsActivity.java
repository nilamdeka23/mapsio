package cmpe295.sjsu.edu.mapsio.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

import cmpe295.sjsu.edu.mapsio.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fav_toolbar);
        setSupportActionBar(toolbar);

       /* TextView view = (TextView)findViewById(R.id.about_us_content);
        String formattedText ="<font face=\"monspace\"><p><b><i><strong><font color=\"#009688\">Mapsio</font></strong></i></b><font color=\"black\"> significantly improves user experience by providing intelligent recommendations, exploiting machine learning techniques based on user’s commute pattern, calendar events, and favorites.With an enhanced user interface, this application aims to be more intuitive and less distracting to users.----</font></p></font>";
        view.setText(Html.fromHtml(getString(R.string.about_us_text)));*/

        WebView view = (WebView) findViewById(R.id.textContent);
        view.loadData(getString(R.string.about_us_text), "text/html", "utf-8");
    }
}
