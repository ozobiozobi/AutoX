
let __runtime = runtime
let __context = context

exports = module.exports = function(cmd){
    return __runtime.shell.runShizukuShellCommand(cmd);
}
/*
exports.createShell = function() {

}*/

exports.isAlive = function(){
    return __runtime.shell.isShizukuAlive();
}