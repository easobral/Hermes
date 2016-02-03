package edu.nav.hermes;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import edu.nav.hermes.tasks.PathFinderTask;

/**
 * Created by eduardo on 03/02/16.
 */
public class PathFinderOverlay extends Overlay {

    Context context;

    public PathFinderOverlay(Context ctx) {
        super(ctx);
        this.context = ctx;
    }

    /**
     * Draw the overlay over the map. This will be called on all active overlays with shadow=true,
     * to lay down the shadow layer, and then again on all overlays with shadow=false. Callers
     * should check isEnabled() before calling draw(). By default, draws nothing.
     *
     * @param c      canvas to draw
     * @param osmv   mapview object
     * @param shadow don't know
     */
    @Override
    protected void draw(Canvas c, MapView osmv, boolean shadow) {
        //TODO
    }

    /**
     * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
     * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
     * or the underlying {@link MapView} has the chance to handle this event.
     *
     * @param e       motion event that generated event
     * @param mapView mapview
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        //TODO

        PathFinderTask task = new PathFinderTask(context);
        task.execute();
        return true;
    }

}
