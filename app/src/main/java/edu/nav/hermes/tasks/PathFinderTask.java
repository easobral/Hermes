package edu.nav.hermes.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import edu.nav.hermes.math.algorithms.AStarAlgorithm;
import edu.nav.hermes.math.algorithms.Answer;
import edu.nav.hermes.math.algorithms.DijkstraAlgorithm;
import edu.nav.hermes.math.algorithms.Graph;
import edu.nav.hermes.math.algorithms.LoopListener;

/**
 * Created by eduardo on 02/02/16.
 * The database
 */
public class PathFinderTask extends AsyncTask<PathFinderTask.Params, Integer, Answer> {

    double max_dist = 0;
    int visited_nodes = 0;
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
        progressDialog.setMessage("Por favor, aguarde um momento");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param answer The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Answer answer) {
        progressDialog.dismiss();
        listener.onTaskCompleted(answer.path);
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
    protected Answer doInBackground(PathFinderTask.Params... params) {
        //TODO
        par = params[0];
        clock.start();
        Answer answer = new Answer();
        ArrayList<IGeoPoint> path = new ArrayList<>();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String algo = pref.getString("pref_algoritmo_algoritmo", "a_star");
        int cache_size = Integer.parseInt(pref.getString("pref_performance_cache_size", "1"));

        Graph graph = new Graph(context.getAssets(), cache_size);
        Long p1 = graph.getClosestNode(par.start);
        Long p2 = graph.getClosestNode(par.end);

        if (null == p1 || null == p2) {
            return answer;
        }

        max_dist = graph.getData(p1).distanceTo(graph.getData(p2));
        progressDialog.setMax((int) max_dist);

        if (algo.equals("a_star")) {
            AStarAlgorithm.Heuristic heuristc = new AStarAlgorithm.Heuristic() {
                @Override
                public double cost(Graph graph, Long start, Long end) {
                    return graph.getData(start).distanceTo(graph.getData(end));
                }
            };

            if (pref.getString("pref_algoritimo_heuristica", "dist").equals("dist_sqr")) {
                heuristc = new AStarAlgorithm.Heuristic() {
                    @Override
                    public double cost(Graph graph, Long start, Long end) {
                        double dist = graph.getData(start).distanceTo(graph.getData(end));
                        return dist * dist;
                    }
                };
            } else if (pref.getString("pref_algoritimo_heuristica", "dist").equals("dist_sqr_over_max")) {
                heuristc = new AStarAlgorithm.Heuristic() {
                    @Override
                    public double cost(Graph graph, Long start, Long end) {
                        double dist = graph.getData(start).distanceTo(graph.getData(end));
                        return dist * dist / max_dist;
                    }
                };

            }

            AStarAlgorithm aStarAlgorithm = new AStarAlgorithm(graph, p1, p2, new Loop(), heuristc);
            answer = aStarAlgorithm.start();
        } else if (algo.equals("dijkstra")) {
            DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph, p1, p2, new Loop());
            answer = dijkstraAlgorithm.start();
        }



        Log.d(getClass().getSimpleName(), "" + clock.getTime());

        return answer;
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
            float cur_distance = node.getData().distanceTo(par.end);
            if (cur_distance < best_distance) {
                best_distance = cur_distance;
            }
            if (visited_nodes % 10 == 0) {
                publishProgress((int) (max_dist - best_distance));
            }
        }
    }

}
