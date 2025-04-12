import { asGlobal, defineGetter } from "@/utils";

var r = runtime;
function performAction(action: any, args: any[]) {
    if (args.length == 4) {
        return action(r.automator.bounds(args[0], args[1], args[2], args[3]));
    } else if (args.length == 2) {
        return action(r.automator.text(args[0], args[1]));
    } else {
        return action(r.automator.text(args[0], -1));
    }
}
const automator = {
    click: function (
        x: number | string | android.Rect,
        y?: number,
        ...args: number[]
    ) {
        if (arguments.length == 2 && typeof x == "number" && typeof y == "number") {
            return r.automator.click(x, y);
        }
        if (arguments.length == 1 && x instanceof android.graphics.Rect) {
            const rect = x as android.Rect;
            return r.automator.click(rect.centerX(), rect.centerY());
        }
        return performAction(
            function (target: unknown) {
                return r.automator.click(target);
            },
            [...arguments]
        );
    },

    longClick: function (
        a: number | string | android.Rect,
        b?: number,
        c?: number,
        d?: number
    ) {
        if (arguments.length == 2 && typeof a == "number" && typeof b == "number") {
            return r.automator.longClick(a, b);
        }
        if (arguments.length == 1 && a instanceof android.graphics.Rect) {
            const rect = a as android.Rect;
            return r.automator.longClick(rect.centerX(), rect.centerY());
        }
        return performAction(
            function (target: unknown) {
                return r.automator.longClick(target);
            },
            [...arguments]
        );
    },
    press: function (x: number | android.Rect, y: number, duration: number) {
        if (arguments.length == 2 && x instanceof android.graphics.Rect) {
            const rect = x as android.Rect;
            return r.automator.press(rect.centerX(), rect.centerY(), y);
        }
        return r.automator.press(x, y, duration);
    },
    gesture: r.automator.gesture.bind(r.automator, 0),
    gestureAsync: r.automator.gestureAsync.bind(r.automator, 0),
    swipe: r.automator.swipe.bind(r.automator),

    gestures: function (...args: (number | [number, number])[]) {
        return r.automator.gestures(toStrokes([...args]));
    },

    gesturesAsync: function (...args: (number | [number, number])[]) {
        r.automator.gesturesAsync(toStrokes([...args]));
    },
    scrollDown: function (a?: number) {
        if (arguments.length == 0) return r.automator.scrollMaxForward();
        if (arguments.length == 1 && typeof a === "number")
            return r.automator.scrollForward(a);
        return performAction(
            function (target: any) {
                return r.automator.scrollForward(target);
            },
            [...arguments]
        );
    },
    scrollUp: function (a?: number) {
        if (arguments.length == 0) return r.automator.scrollMaxBackward();
        if (arguments.length == 1 && typeof a === "number")
            return r.automator.scrollBackward(a);
        return performAction(
            function (target: any) {
                return r.automator.scrollBackward(target);
            },
            [...arguments]
        );
    },
    setText: function (i: number | string, text?: string) {
        if (arguments.length == 1) {
            return r.automator.setText(r.automator.editable(-1), i);
        } else {
            return r.automator.setText(r.automator.editable(i), text);
        }
    },
    input: function (i: number | string, text?: string) {
        if (arguments.length == 1) {
            return r.automator.appendText(r.automator.editable(-1), i);
        } else {
            return r.automator.appendText(r.automator.editable(i), text);
        }
    },
};

function toStrokes(args: string | IArguments | any[]) {
    var screenMetrics = r.getScreenMetrics();
    var len = args.length;
    var strokes = java.lang.reflect.Array.newInstance(
        android.accessibilityservice.GestureDescription.StrokeDescription,
        len
    );
    for (var i = 0; i < len; i++) {
        var gesture = args[i];
        var pointsIndex = 1;
        var start, delay;
        if (typeof gesture[1] == "number") {
            start = gesture[0];
            delay = gesture[1];
            pointsIndex = 2;
        } else {
            start = 0;
            delay = gesture[0];
        }
        var gestureLen = gesture.length;
        var path = new android.graphics.Path();
        path.moveTo(
            screenMetrics.scaleX(gesture[pointsIndex][0]),
            screenMetrics.scaleY(gesture[pointsIndex][1])
        );
        for (var j = pointsIndex + 1; j < gestureLen; j++) {
            path.lineTo(
                screenMetrics.scaleX(gesture[j][0]),
                screenMetrics.scaleY(gesture[j][1])
            );
        }
        strokes[i] =
            new android.accessibilityservice.GestureDescription.StrokeDescription(
                path,
                start,
                delay
            );
    }
    return strokes;
}

var modes = {
    normal: 0,
    fast: 1,
};

const flagsMap = {
    findOnUiThread: 1,
    useUsageStats: 2,
    useShell: 4,
};

function auto(mode: "fast" | "normal") {
    if (mode) {
        auto.setMode(mode);
    }
    r.accessibilityBridge.ensureServiceEnabled();
}

auto.takeScreenshot = function (): Autox.Image {
    return r.automator.takeScreenshot2Sync();
};

auto.takeScreenshotAsync = function (callback: (image: Autox.Image, errCode: number) => void) {
    if (typeof callback !== "function") {
        throw new TypeError("callback should be a function");
    }
    r.automator.takeScreenshot2(callback);
};

auto.waitFor = function () {
    r.accessibilityBridge.waitForServiceEnabled();
};

auto.setMode = function (modeStr: (keyof typeof modes)) {
    if (typeof modeStr !== "string") {
        throw new TypeError("mode should be a string");
    }
    const mode = modes[modeStr];
    if (mode == undefined) {
        throw new Error("unknown mode for auto.setMode(): " + modeStr);
    }
    r.accessibilityBridge.setMode(mode);
};

auto.setFlags = function (flags: string | string[]) {
    let flagStrings: string[];
    if (Array.isArray(flags)) {
        flagStrings = flags;
    } else if (typeof flags == "string") {
        flagStrings = [flags];
    } else {
        throw new TypeError("flags = " + flags);
    }
    let flagsInt = 0;
    for (let i = 0; i < flagStrings.length; i++) {
        const flag = (flagsMap as any)[flagStrings[i]];
        if (flag == undefined) {
            throw new Error("unknown flag for auto.setFlags(): " + flagStrings[i]);
        }
        flagsInt |= flag;
    }
    r.accessibilityBridge.setFlags(flagsInt);
};

type Auto = typeof auto & {
    service: any;
    windows: any[];
    root: any;
    rootInActiveWindow: any;
    windowRoots: any[];
};
defineGetter(auto, "service", function () {
    return r.accessibilityBridge.getService();
});

defineGetter(auto, "windows", function () {
    var service = (auto as Auto).service;
    return service == null ? [] : util.java.toJsArray(service.getWindows(), true);
});

defineGetter(auto, "root", function () {
    var root = r.accessibilityBridge.getRootInCurrentWindow();
    if (root == null) {
        return null;
    }
    return com.stardust.automator.UiObject.Companion.createRoot(root);
});

defineGetter(auto, "rootInActiveWindow", function () {
    var root = r.accessibilityBridge.getRootInActiveWindow();
    if (root == null) {
        return null;
    }
    return com.stardust.automator.UiObject.Companion.createRoot(root);
});

defineGetter(auto, "windowRoots", function () {
    return util.java
        .toJsArray(r.accessibilityBridge.windowRoots(), false)
        .map((root: unknown) =>
            com.stardust.automator.UiObject.Companion.createRoot(root)
        );
});

auto.setWindowFilter = function (filter: (window: any) => boolean) {
    r.accessibilityBridge.setWindowFilter(
        new com.stardust.autojs.core.accessibility.AccessibilityBridge.WindowFilter(
            filter
        )
    );
};

declare global {
    var auto: Auto;

    var click: typeof automator['click']
    var longClick: typeof automator['longClick']
    var press: typeof automator['press']
    var swipe: typeof automator['swipe']
    var gesture: typeof automator['gesture']
    var gestures: typeof automator['gestures']
    var gesturesAsync: typeof automator['gesturesAsync']
    var scrollDown: typeof automator['scrollDown']
    var scrollUp: typeof automator['scrollUp']
    var setText: typeof automator['setText']
    var input: typeof automator['input']

    var back: typeof r.automator['back']
    var home: typeof r.automator['home']
    var powerDialog: typeof r.automator['powerDialog']
    var notifications: typeof r.automator['notifications']
    var quickSettings: typeof r.automator['quickSettings']
    var recents: typeof r.automator['recents']
    var splitScreen: typeof r.automator['splitScreen']
    var takeScreenshot: typeof r.automator['takeScreenshot']
    var lockScreen: typeof r.automator['lockScreen']
}
global.auto = auto as Auto;

asGlobal(r.automator, [
    "back",
    "home",
    "powerDialog",
    "notifications",
    "quickSettings",
    "recents",
    "splitScreen",
    "takeScreenshot",
    "lockScreen",
    "dismissNotificationShade",
    "keyCodeHeadsetHook",
    "accessibilityShortcut",
    "accessibilityButtonChooser",
    "accessibilityButton",
    "accessibilityAllApps",
    "dpadUp",
    "dpadDown",
    "dpadRight",
    "dpadLeft",
    "dpadCenter",
]);

asGlobal(automator, [
    "click",
    "longClick",
    "press",
    "swipe",
    "gesture",
    "gestures",
    "gestureAsync",
    "gesturesAsync",
    "scrollDown",
    "scrollUp",
    "input",
    "setText",
]);

export default automator;
