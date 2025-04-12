
var __selector__ = runtime.selector();
var __obj__ = new java.lang.Object();
var scope = global as any
type Selector = Autox.UiSelector
declare global {
    var id: Selector['id']
    var text: Selector['text']
    var desc: Selector['desc']
    var className: Selector['className']
    var packageName: Selector['packageName']
    var idMatches: Selector['idMatches']
    var textMatches: Selector['textMatches']
}


for (var method in __selector__) {
    if (!(method in __obj__) && !(method in scope)) {
        scope[method] = (function (method) {
            return function (...args: unknown[]) {
                var s = selector() as any;
                return s[method](...args);
            };
        })(method);
    }
}

export default function selector(): Selector {
    return runtime.selector();
};