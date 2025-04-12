var threads = Object.create(runtime.threads) as Autox.Threads & {
    runAsync<T>(fn: <T>() => T): Promise<T>
}

threads.runAsync = function <T>(fn: <T>() => T): Promise<T> {
    return new Promise(function (resolve, reject) {
        runtime.threads.runTaskForThreadPool(function () {
            try {
                const result: T = fn();
                setImmediate(resolve, result)
            } catch (e) {
                setImmediate(reject, e)
            }
        })
    })
}
declare global {
    var sync: (func: unknown, lock: unknown) => any
}
global.sync = function (func: unknown, lock: unknown) {
    lock = lock || null;
    return new org.mozilla.javascript.Synchronizer(func, lock);
}

// Extend the Promise interface to include the wait method
declare global {
    interface Promise<T> {
        wait(): T;
    }
}

global.Promise.prototype.wait = function () {
    var disposable = threads.disposable();
    this.then(result => {
        disposable.setAndNotify({ result: result });
    }).catch(error => {
        disposable.setAndNotify({ error: error });
    });
    var r = disposable.blockedGet();
    if (r.error) {
        throw r.error;
    }
    return r.result;
}

export default threads;