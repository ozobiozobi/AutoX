const storages = {
    create, remove
};
function create(name: string) {
    return new LocalStorage(name);
}

function remove(name: string) {
    create(name).clear();
}

export default storages;

class LocalStorage {
    private readonly _storage: any
    constructor(name: string) {
        this._storage = new com.stardust.autojs.core.storage.LocalStorage(context, name);
    }

    put(key: string, value: any) {
        if (typeof (value) == 'undefined') {
            throw new TypeError('value cannot be undefined');
        }
        this._storage.put(key, JSON.stringify(value));
    }
    get(key: string, defaultValue: any) {
        var value = this._storage.getString(key, null);
        if (!value) {
            return defaultValue;
        }
        return JSON.parse(value);
    }
    remove(key: string) {
        this._storage.remove(key);
    }
    contains(key: string) {
        return this._storage.contains(key);
    }
    clear() {
        this._storage.clear();
    }
}