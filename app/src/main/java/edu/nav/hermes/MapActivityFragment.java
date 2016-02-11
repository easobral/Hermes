package edu.nav.hermes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;

import edu.nav.hermes.tasks.TileDownloader;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapActivityFragment extends Fragment {

    private MyLocationNewOverlay mLocationOverlay;
    private PathFinderOverlay pathFinderOverlay;

    public MapActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        MapView mapView = (MapView) rootView.findViewById(R.id.map);
        setHasOptionsMenu(true);

        setupMapView(mapView);

        return rootView;
    }

    public void updateMapView() {
        View rootView = getView();
        if (null == rootView) return;
        MapView mapView = (MapView) rootView.findViewById(R.id.map);
        setupMapView(mapView);
        mapView.invalidate();
    }

    private void setupMapView(MapView mapView) {
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();

        this.mLocationOverlay = new MyLocationNewOverlay(getContext(), new GpsMyLocationProvider(getContext()), mapView);
        mapView.getOverlays().add(this.mLocationOverlay);
        this.pathFinderOverlay = new PathFinderOverlay(getContext());
        mapView.getOverlays().add(this.pathFinderOverlay);


        final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(getContext());
        final ITileSource tileSource = new XYTileSource("OSMPublicTransport", 10, 16, 256, ".jpg", new String[]{});

        final File file = new File(Environment.getExternalStorageDirectory(), "osmdroid/rio_01.zip");

        if (file.exists()) {
            try {
                final OfflineTileProvider provider = new OfflineTileProvider(registerReceiver, new File[]{file});
                mapView.setTileProvider(provider);
                mapView.setTileSource(tileSource);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(this.getClass().getName(), e.getMessage());
            }
        }


        mapView.setUseDataConnection(false); //optional, but a good way to prevent loading from the network and test your zip loading.

        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(-22.905727, -43.289553);
        mapController.setCenter(startPoint);

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
        File file = new File(Environment.getExternalStorageDirectory(), "osmdroid/rio_01.zip");
        if (!file.exists()) {
            new TileDownloader(getContext(), this).execute();
        }

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

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.action_execute_with_saved_points == item.getItemId()) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            int lat = Integer.parseInt(pref.getString("pref_debug_start_lat", "0"));
            int lon = Integer.parseInt(pref.getString("pref_debug_start_lon", "0"));

            GeoPoint start = new GeoPoint(lat, lon);

            lat = Integer.parseInt(pref.getString("pref_debug_end_lat", "0"));
            lon = Integer.parseInt(pref.getString("pref_debug_end_lon", "0"));

            GeoPoint end = new GeoPoint(lat, lon);

            MapView mapView = (MapView) getView().findViewById(R.id.map);

            pathFinderOverlay.findPath(start, end, mapView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
