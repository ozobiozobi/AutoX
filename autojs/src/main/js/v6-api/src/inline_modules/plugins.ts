
const r = eval("require");
function plugins() {
}

plugins.load = function (packageName: string): any {
    var plugin = runtime.plugins.load(packageName);
    var index = r(plugin.mainScriptPath);
    return index(plugin.unwrap());
}

export default plugins;