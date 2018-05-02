This demo is based on the belief that when running the following code in the main thread it should
not be possible to reach the exception: 

```java
Single.fromCallable(Object::new)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(object -> {
        throw new IllegalStateException("should never be called");
    })
    .dispose();
```

After a few thousand calls I often encounter the exception. More interestingly, in the debugger
we can see that the `HandlerScheduler$ScheduledRunnable` has already been disposed, yet its `run()`
method is still called.

![Debugging run() on disposed Runnable](https://github.com/stevelilly/rxandroid-racecondition/blob/master/runOnDisposedRunnable.png?raw=true)

AFAICT the code in the `HandlerScheduler` is using `removeCallbacks` and `removeCallbacksAndMessages`
in the proper way, however it appears it is not safe to call `delegate.run()` without first checking
`isDisposed`.

This seems to affect API 24+ much more often than API 23 when I test it, but this could just be the
specific timings of my emulator.