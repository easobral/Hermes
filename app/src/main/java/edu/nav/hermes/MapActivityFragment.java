package edu.nav.hermes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.views.MapView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapActivityFragment extends Fragment {

    public MapActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        MapView mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        return rootView;
    }
}
