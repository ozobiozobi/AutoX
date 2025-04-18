import { DialogBuildProperties, JsDialog } from "./types";


const dialogs = {
    rawInput, alert, confirm, select, singleChoice, multiChoice, build,
    prompt: rawInput,
    input: inputD
}
function inputD(title: string, prefill?: string): any | Promise<any>
function inputD(title: string, prefill: string | undefined, callback: (input: string) => void): void
function inputD(title: string, prefill?: string, callback?: (input: string) => void) {
    prefill = prefill || "";
    if (isUiThread() && !callback) {
        return new Promise(function (resolve) {
            rtDialogs().rawInput(title, prefill, function (str: string) {
                resolve(eval(str));
            });
        });
    }
    if (callback) {
        dialogs.rawInput(title, prefill, function (str) {
            callback(eval(str));
        });
        return;
    }
    return eval(dialogs.rawInput(title, prefill) as string);
}

function rawInput(title: string, prefill?: string): string | Promise<string>
function rawInput(title: string, prefill: string | undefined, callback: (input: string) => void): void
function rawInput(title: string, prefill?: string, callback?: (input: string) => void) {
    prefill = prefill || "";
    if (isUiThread() && !callback) {
        return new Promise(function (resolve) {
            rtDialogs().rawInput(title, prefill, function (data: string) {
                resolve(data);
            });
        });
    }
    return rtDialogs().rawInput(title, prefill, callback ? callback : null);
};

function alert(title: string, prefill?: string): void | Promise<void>
function alert(title: string, prefill: string | undefined, callback: () => void): void
function alert(title: string, prefill?: string, callback?: () => void) {
    prefill = prefill || "";
    if (isUiThread() && !callback) {
        return new Promise(function (resolve) {
            rtDialogs().alert(title, prefill, function (data: unknown) {
                resolve(data);
            });
        });
    }
    return rtDialogs().alert(title, prefill, callback ? callback : null);
}

function confirm(title: string, prefill?: string): boolean | Promise<boolean>
function confirm(title: string, prefill: string | undefined, callback: (value: boolean) => void): void
function confirm(title: string, prefill?: string, callback?: (value: boolean) => void) {
    prefill = prefill || "";
    if (isUiThread() && !callback) {
        return new Promise(function (resolve) {
            rtDialogs().confirm(title, prefill, function (data: unknown) {
                resolve(data);
            });
        });
    }
    return rtDialogs().confirm(title, prefill, callback ? callback : null);
}

function select(title: string, items: string[]): number | Promise<number>
function select(title: string, items: string[], callback?: (value: number) => void): void
function select(title: string, items: string[], callback?: (value: number) => void) {
    if (items instanceof Array) {
        if (isUiThread() && !callback) {
            return new Promise(function (resolve) {
                rtDialogs().select(title, items, function (data: unknown) {
                    resolve(data)
                });
            });
        }
        return rtDialogs().select(title, items, callback ? callback : null);
    }
    return rtDialogs().select(title, [].slice.call(arguments, 1), null);
}

function singleChoice(title: string, items: string[], index?: number): number | Promise<number>
function singleChoice(title: string, items: string[], index: number | undefined,
    callback: (value: number) => void): void
function singleChoice(title: string, items: string[], index: number = 0,
    callback?: (value: number) => void) {
    index = index || 0;
    if (isUiThread() && !callback) {
        return new Promise(function (resolve) {
            rtDialogs().singleChoice(title, index, items, function (data: unknown) {
                resolve(data);
            });
        });
    }
    return rtDialogs().singleChoice(title, index, items, callback ? callback : null);
}

function multiChoice(title: string, items: string[], index?: number[]): number[] | Promise<number[]>
function multiChoice(title: string, items: string[], index: number[] | undefined,
    callback: (value: number[]) => void): void
function multiChoice(title: string, items: string[], index?: number[],
    callback?: (value: number[]) => void) {
    index = index || [];
    if (isUiThread() && !callback) {
        return new Promise(function (resolve) {
            rtDialogs().singleChoice(title, index, items, function (r: any) {
                resolve(javaArrayToJsArray(r));
            });
        });
    }
    if (callback) {
        return rtDialogs().multiChoice(title, index, items, function (r: any) {
            callback(javaArrayToJsArray(r));
        });
    }
    return javaArrayToJsArray(rtDialogs().multiChoice(title, index, items, null));

}

var propertySetters = {
    "title": null,
    "titleColor": { adapter: parseColor },
    "buttonRippleColor": { adapter: parseColor },
    "icon": null,
    "content": null,
    "contentColor": { adapter: parseColor },
    "contentLineSpacing": null,
    "items": null,
    "itemsColor": { adapter: parseColor },
    "positive": { method: "positiveText" },
    "positiveColor": { adapter: parseColor },
    "neutral": { method: "neutralText" },
    "neutralColor": { adapter: parseColor },
    "negative": { method: "negativeText" },
    "negativeColor": { adapter: parseColor },
    "cancelable": null,
    "canceledOnTouchOutside": null,
    autoDismiss: null
};

function build(properties: DialogBuildProperties): JsDialog {
    var builder = Object.create(__runtime__.dialogs.newBuilder());
    builder.thread = threads.currentThread();
    for (var name in properties) {
        if (!Object.prototype.hasOwnProperty.call(properties, name)) {
            continue;
        }
        applyDialogProperty(builder, name, properties[name as keyof DialogBuildProperties]);
    }
    applyOtherDialogProperties(builder, properties);
    return ui.run(() => builder.buildDialog());
}

function applyDialogProperty(builder: any, name: string, value: any) {
    if (!Object.prototype.hasOwnProperty.call(propertySetters, name)) {
        return;
    }
    var propertySetter = (propertySetters as any)[name] || {};
    if (propertySetter.method == undefined) {
        propertySetter.method = name;
    }
    if (propertySetter.adapter) {
        value = propertySetter.adapter(value);
    }
    builder[propertySetter.method].call(builder, value);
}

function applyOtherDialogProperties(builder: any, properties: DialogBuildProperties) {
    if (properties.inputHint != undefined || properties.inputPrefill != undefined) {
        builder.input(wrapNonNullString(properties.inputHint), wrapNonNullString(properties.inputPrefill),
            function (dialog: unknown, input: any) {
                if (!builder.dialog) { return; }
                input = input.toString();
                builder.dialog.emit("input_change", builder.dialog, input);
            })
            .alwaysCallInputCallback();
    }
    if (properties.items != undefined) {
        var itemsSelectMode = properties.itemsSelectMode;
        if (itemsSelectMode == undefined || itemsSelectMode == 'select') {
            builder.itemsCallback(function (dialog: unknown, view: unknown, position: number, text: string) {
                builder.dialog.emit("item_select", position, text.toString(), builder.dialog);
            });
        } else if (itemsSelectMode == 'single') {
            builder.itemsCallbackSingleChoice(properties.itemsSelectedIndex == undefined ? -1 : properties.itemsSelectedIndex,
                function (dialog: unknown, view: android.View, which: number, text: string) {
                    builder.dialog.emit("single_choice", which, text && text.toString(), builder.dialog);
                    return true;
                });
        } else if (itemsSelectMode == 'multi') {
            builder.itemsCallbackMultiChoice(properties.itemsSelectedIndex == undefined ? null : properties.itemsSelectedIndex,
                function (dialog: any, view: android.View, indices: any, texts: unknown) {
                    builder.dialog.emit(
                        "multi_choice",
                        toJsArray(view, (l: any, i: number) => parseInt(l[i])),
                        toJsArray(indices, (l: any, i: number) => l[i]), builder.dialog);
                    return true;
                });
        } else {
            throw new Error("unknown itemsSelectMode " + itemsSelectMode);
        }
    }
    if (properties.progress != undefined) {
        var progress = properties.progress;
        var indeterminate = (progress.max == -1);
        builder.progress(indeterminate, progress.max, !!progress.showMinMax);
        builder.progressIndeterminateStyle(!!progress.horizontal);
    }
    if (properties.checkBoxPrompt != undefined || properties.checkBoxChecked != undefined) {
        builder.checkBoxPrompt(wrapNonNullString(properties.checkBoxPrompt), !!properties.checkBoxChecked,
            function (view: unknown, checked: unknown) {
                builder.getDialog().emit("check", checked, builder.getDialog());
            });
    }
    if (properties.customView != undefined) {
        let customView = properties.customView;
        if ((typeof customView) as string == 'xml' || typeof (customView) == 'string') {
            customView = ui.run<android.View>(() => ui.inflate(customView as string));
        }
        const wrapInScrollView = (properties.wrapInScrollView === undefined) ? true : properties.wrapInScrollView;
        builder.customView(customView, wrapInScrollView);
    }
}

function wrapNonNullString(str: any) {
    if (str == null || str == undefined) {
        return "";
    }
    return str;
}

function javaArrayToJsArray(javaArray: any[]) {
    var jsArray = [];
    var len = javaArray.length;
    for (var i = 0; i < len; i++) {
        jsArray.push(javaArray[i]);
    }
    return jsArray;
}

function toJsArray(object: any, adapter: any) {
    var jsArray = [];
    if (object != undefined) {
        var len = object.length;
        for (var i = 0; i < len; i++) {
            jsArray.push(adapter(object, i));
        }
    }
    return jsArray;
}

const __runtime__ = runtime
function rtDialogs() {
    var d = __runtime__.dialogs;
    if (!isUiThread()) {
        return d.nonUiDialogs;
    } else {
        return d;
    }
}

function isUiThread() {
    return android.os.Looper.myLooper() == android.os.Looper.getMainLooper();
}

function parseColor(c: any) {
    if (typeof (c) == 'string') {
        return colors.parseColor(c);
    }
    return c;
}

declare global {
    var rawInput: typeof dialogs.rawInput
    var alert: typeof dialogs.alert
    var confirm: typeof dialogs.confirm
    var prompt: typeof dialogs.prompt
}
global.rawInput = dialogs.rawInput.bind(dialogs);
global.alert = dialogs.alert.bind(dialogs);
global.confirm = dialogs.confirm.bind(dialogs);
global.prompt = dialogs.prompt.bind(dialogs);

export default dialogs;