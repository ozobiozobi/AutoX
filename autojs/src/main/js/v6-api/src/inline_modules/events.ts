const KeyEvent = android.view.KeyEvent

var events = Object.create(runtime.events);

events.__asEmitter__ = function (obj: any, thread?: any) {
    var emitter = thread ? events.emitter(thread) : events.emitter();
    for (var key in emitter) {
        if (obj[key] == undefined && typeof (emitter[key]) == 'function') {
            obj[key] = emitter[key].bind(emitter);
        }
    }
    return obj;
}
var keys = {
    "home": KeyEvent.KEYCODE_HOME,
    "menu": KeyEvent.KEYCODE_MENU,
    "back": KeyEvent.KEYCODE_BACK,
    "volume_up": KeyEvent.KEYCODE_VOLUME_UP,
    "volume_down": KeyEvent.KEYCODE_VOLUME_DOWN
};
; (global as any).keys = keys;



export default events as Autox.Events