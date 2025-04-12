

export function setGlobal(key: string, value: any) {
    (global as any)[key] = value;
}

export function setGlobalAnd$(key: string, value: any) {
    if (key.startsWith("$")) {
        key = key.substring(1);
    }
    setGlobal(key, value);
    setGlobal('$' + key, value);
}

export function exitIfError(action: () => void, defReturnValue?: any) {
    try {
        return action();
    } catch (err) {
        if (err instanceof java.lang.Throwable) {
            exit(err);
        } else if (err instanceof Error) {
            const e: any = err
            exit(new org.mozilla.javascript.EvaluatorException(err.name + ": " + err.message, e.fileName, e.lineNumber));
        } else {
            exit();
        }
        return defReturnValue;
    }
};

export function defineGetter(obj: any, prop: string, getter: <T>() => T) {
    Object.defineProperty(obj, prop, { get: getter });
}

export function asGlobal(obj: any, keys: string[]) {
    var len = keys.length;
    for (var i = 0; i < len; i++) {
        var funcName = keys[i];
        var func = obj[funcName]
        if (!func) {
            continue;
        }
        (global as any)[funcName] = func.bind(obj);
    }
}