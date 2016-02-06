package edu.nav.hermes.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import edu.nav.hermes.math.algorithms.AStarAlgorithm;
import edu.nav.hermes.math.algorithms.Answer;
import edu.nav.hermes.math.algorithms.Graph;
import edu.nav.hermes.math.algorithms.LoopListener;

/**
 * Created by eduardo on 02/02/16.
 * The database
 */
public class PathFinderTask extends AsyncTask<PathFinderTask.Params, Integer, List<IGeoPoint>> {

    private ProgressDialog progressDialog;
    private Context context;
    private PathFinderTask.TaskCompletedListener listener;
    private Params par;
    private Clock clock;

    public PathFinderTask(Context ctx, PathFinderTask.TaskCompletedListener listener) {
        context = ctx;
        this.listener = listener;
        clock = new Clock();
    }

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Procurando Rota");
        progressDialog.setMessage("Por favor, espere um momento");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param path The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<IGeoPoint> path) {
        progressDialog.dismiss();
        listener.onTaskCompleted(path);
    }


    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        Integer i = values[0];
        progressDialog.setProgress(i);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<IGeoPoint> doInBackground(PathFinderTask.Params... params) {
        //TODO
        par = params[0];
        Graph graph = new Graph(context.getAssets());
        ArrayList<IGeoPoint> path = new ArrayList<>();
        Long p1 = graph.getClosestNode(par.start);
        Long p2 = graph.getClosestNode(par.end);
        clock.start();


//        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph,p1,p2,new Loop());
//        Answer answer = dijkstraAlgorithm.start();

        AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(graph, p1, p2, new Loop(), new AStarAlgorithm.Heuristic() {
            @Override
            public double cost(Graph graph, Long start, Long end) {
                return graph.getData(start).distanceTo(graph.getData(end));
            }
        });
        Answer answer = aStarAlgorithm.start();

        Log.d(getClass().getSimpleName(), "" + clock.getTime());

        return answer.path;
    }

    public interface TaskCompletedListener {
        void onTaskCompleted(List<IGeoPoint> path);
    }

    public static class Params {
        public GeoPoint start;
        public GeoPoint end;
    }

    private static class Clock {
        long t_start;

        public void start() {
            t_start = System.currentTimeMillis();
        }

        public long getTime() {
            return System.currentTimeMillis() - t_start;
        }
    }

    private class Loop implements LoopListener {
        int i = 0;
        float best_distance = Float.POSITIVE_INFINITY;

        @Override
        public void onLoop(Graph.Node node) {
            i++;
            float cur_distance = node.getData().distanceTo(par.end);
            if (cur_distance < best_distance) {
                best_distance = cur_distance;
            }
            if (10 < i) {
                float total_distance = par.start.distanceTo(par.end);
                Integer progress = (int) Math.floor((1 - best_distance / total_distance) * 100);
                publishProgress(progress);
            }
        }
    }

}
