
let __runtime = runtime
let __context = context

exports = module.exports = function(cmd){
    return __runtime.shell.runShizukuShellCommand(cmd);
}
/*
exports.createShell = function() {

}*/

exports.isAlive = function(){
    return __runtime.shizuku.isShizukuAlive();
}

exports.runRhinoScriptFile = function(path){
    let data =  __runtime.shizuku.runRhinoScriptFile(path);
    if (!data){
        return data;
    }else return JSON.parse(data);
}

exports.runRhinoScript = function(script){
    let data = __runtime.shizuku.runRhinoScript(script);
    if (!data){
       return data;
    }else return JSON.parse(data);
}