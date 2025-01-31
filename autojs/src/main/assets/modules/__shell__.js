

module.exports = function(__runtime__, scope){

    scope.SetScreenMetrics = function(w, h){
//        __runtime__.getRootShell().SetScreenMetrics(w, h);
    }

    scope.Tap = function(x, y){
        __runtime__.shell.exec(`input tap ${x} ${y}`, true);
    }

    scope.Swipe = function(x1, y1, x2, y2, duration){
        if(arguments.length == 5){
            __runtime__.shell.exec(`input swipe ${x1} ${y1} ${x2} ${y2} ${duration}`, true);
        }else{
            __runtime__.shell.exec(`input swipe ${x1} ${y1} ${x2} ${y2}`, true);
        }
    }

    scope.Screencap = function(path){
         __runtime__.shell.exec(`screencap -p '${path}'`, true);
    }

    scope.KeyCode = function(keyCode){
        __runtime__.shell.exec("input keyevent " + keyCode, true);
    }

    scope.Home = function(){
        return KeyCode(3);
    }

    scope.Back = function(){
        return KeyCode(4);
    }

    scope.Power = function(){
        return KeyCode(26);
    }

    scope.Up = function(){
        return KeyCode(19);
    }

    scope.Down = function(){
        return KeyCode(20);
    }

    scope.Left = function(){
        return KeyCode(21);
    }

    scope.Right = function(){
        return KeyCode(22);
    }

    scope.OK = function(){
        return KeyCode(23);
    }

    scope.VolumeUp = function(){
        return KeyCode(24);
    }

    scope.VolumeDown = function(){
        return KeyCode(25);
    }

    scope.Menu = function(){
        return KeyCode(1);
    }

    scope.Camera = function(){
        return KeyCode(27);
    }

    scope.Text = function(text){
         __runtime__.shell.exec(`input text '${text}'`, true);
    }

    scope.Input = scope.Text;

    scope.Shell = function (root){
        return __runtime__.shell.createShell(!!root);
    }

    return function(cmd, root){
       return __runtime__.shell.exec(cmd, !!root);
   };
}
