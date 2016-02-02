package edu.nav.hermes;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapActivityFragment extends Fragment {

    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;

    public MapActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        MapView mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();

        this.mLocationOverlay = new MyLocationNewOverlay(getContext(), new GpsMyLocationProvider(getContext()), mapView);
        mapView.getOverlays().add(this.mLocationOverlay);

        final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(getContext());
        final ITileSource tileSource = new XYTileSource("MapQuest", 10, 16, 256, ".jpg", new String[]{});

        final File file = new File(Environment.getExternalStorageDirectory(), "osmdroid/rio_01.zip");

        try {
            final OfflineTileProvider provider = new OfflineTileProvider(registerReceiver, new File[]{file});
            mapView.setTileProvider(provider);
            mapView.setTileSource(tileSource);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(this.getClass().getName(), e.getMessage());
        }


        mapView.setUseDataConnection(false); //optional, but a good way to prevent loading from the network and test your zip loading.

        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(-22.905727, -43.289553);
        mapController.setCenter(startPoint);


        return rootView;
    }


    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        this.mLocationOverlay.enableMyLocation();
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {@link Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        this.mLocationOverlay.disableMyLocation();
    }
}
