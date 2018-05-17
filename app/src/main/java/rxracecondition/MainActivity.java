package rxracecondition;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private static final String TAG = "RxRaceCondition";

    private enum Phase { CREATE_FRAGMENT, CREATE_VIEW, RESUME, PAUSE, DESTROY_VIEW }

    private Phase phase = Phase.CREATE_FRAGMENT;
    private Fragment fragment;
    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_test).setOnClickListener(v -> repeatExerciseFragmentLifecycle());
        container = new LinearLayout(this);
    }

    Handler repeatHandler = new Handler();
    int resumeCount = 0;

    void repeatExerciseFragmentLifecycle() {
        switch (phase) {
            case CREATE_FRAGMENT:
                fragment = new TestFragment();
                fragment.onCreate(null);
                phase = Phase.CREATE_VIEW;
                break;
            case CREATE_VIEW:
                View view = fragment.onCreateView(getLayoutInflater(), container, null);
                fragment.onViewCreated(view, null);
                container.addView(view);
                phase = Phase.RESUME;
                break;
            case RESUME:
                if ((++resumeCount % 1000) == 0) {
                    Log.d(TAG, "resumed " + resumeCount + " times");
                }
                fragment.onResume();
                phase = Phase.PAUSE;
                break;
            case PAUSE:
                fragment.onPause();
                phase = Phase.DESTROY_VIEW;
                break;
            case DESTROY_VIEW:
                fragment.onDestroyView();
                container.removeAllViews();
                phase = Phase.CREATE_VIEW;
                break;
        }
        repeatHandler.post(this::repeatExerciseFragmentLifecycle);
    }

    @Override
    protected void onPause() {
        super.onPause();
        repeatHandler.removeCallbacksAndMessages(null);
    }
}
