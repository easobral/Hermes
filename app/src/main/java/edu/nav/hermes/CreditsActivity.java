package edu.nav.hermes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class CreditsActivity extends AppCompatActivity {

    private static final String license =
            "Map data © by Openstreetmap contributors licenced under ODbL 1.0.\n" +
                    "This map is available under CC-BY-SA licence (© by MeMoMaps).\n" +
                    "ODbl :http://opendatacommons.org/licenses/odbl/\n" +
                    "OpenStreetmap:http://www.openstreetmap.org/\n" +
                    "CC-BY-SA 2.0: http://creativecommons.org/licenses/by-sa/2.0/\n" +
                    "tiles: http://www.xn--pnvkarte-m4a.de/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        TextView textView = (TextView) findViewById(R.id.credits);

        textView.setText(license);
    }
}
