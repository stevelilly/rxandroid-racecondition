package rxracecondition;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends Activity {

    private static final String TAG = "RxRaceCondition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button_test).setOnClickListener(v -> testSingleFromCallable());
    }

    Handler repeatHandler = new Handler();
    int callCount = 0;

    void testSingleFromCallable() {
        if ((++callCount % 1000) == 0) {
            Log.d(TAG, "called " + callCount + " times");
        }
        Single.fromCallable(Object::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(() -> repeatHandler.post(this::testSingleFromCallable))
                .subscribe(object -> {
                    throw new IllegalStateException("should never be called");
                })
                .dispose();
    }

    @Override
    protected void onPause() {
        super.onPause();
        repeatHandler.removeCallbacksAndMessages(null);
    }
}
