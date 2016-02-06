package edu.nav.hermes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

import java.util.Iterator;
import java.util.List;

import edu.nav.hermes.tasks.PathFinderTask;

/**
 * Created by eduardo on 03/02/16.
 *
 *
 */
public class PathFinderOverlay extends Overlay {

    Context context;
    boolean is_to_draw;
    List<IGeoPoint> pathToDraw;
    TapState tapState;

    public PathFinderOverlay(Context ctx) {
        super(ctx);
        this.context = ctx;
        tapState = new FirstTap();
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
        if (is_to_draw) {
            drawPath(c, osmv, pathToDraw);
        }
    }

    private void drawPath(Canvas canvas, MapView osmv, List<IGeoPoint> path) {
        Projection projection = osmv.getProjection();
        IGeoPoint first, second;
        Point first_p = new Point(),
                second_p = new Point();
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(128);
        paint.setStrokeCap(Paint.Cap.ROUND);

        Iterator<IGeoPoint> itr = path.iterator();
        second = itr.next();

        while (itr.hasNext()) {
            first = second;
            second = itr.next();
            projection.toPixels(first, first_p);
            projection.toPixels(second, second_p);
            canvas.drawLine(first_p.x, first_p.y,
                    second_p.x, second_p.y, paint);
        }

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
        Projection projection = mapView.getProjection();
        IGeoPoint p = projection.fromPixels((int) e.getX(), (int) e.getY());
        tapState = tapState.onTap(p, mapView);
        return true;
    }


    private interface TapState {
        TapState onTap(IGeoPoint point, MapView mapView);
    }

    private class FirstTap implements TapState {
        @Override
        public TapState onTap(IGeoPoint point, MapView mapView) {
            is_to_draw = false;
            mapView.invalidate();
            return new SecondTap(point);
        }
    }

    private class SecondTap implements TapState {
        IGeoPoint p;

        SecondTap(IGeoPoint p) {
            this.p = p;
        }

        @Override
        public TapState onTap(IGeoPoint point, final MapView mapView) {
            PathFinderTask task = new PathFinderTask(context, new PathFinderTask.TaskCompletedListener() {
                @Override
                public void onTaskCompleted(List<IGeoPoint> path) {
                    pathToDraw = path;
                    is_to_draw = true;
                    mapView.invalidate();
                    Log.d("TAG", "Done");
                }
            });
            PathFinderTask.Params params = new PathFinderTask.Params();
            params.start = new GeoPoint(p.getLatitude(), p.getLongitude());
            params.end = new GeoPoint(point.getLatitude(), point.getLongitude());
            task.execute(params);
            return new FirstTap();
        }
    }
}
