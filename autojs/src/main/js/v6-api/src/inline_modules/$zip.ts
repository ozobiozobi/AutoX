import $files from "./files";
const ZipFile = Packages.net.lingala.zip4j.core.ZipFile;
const File = java.io.File;
const ArrayList = java.util.ArrayList;
const bridges = runtime.bridges
const Zip4jConstants = Packages.net.lingala.zip4j.util.Zip4jConstants;

function $zip() {

}


$zip.open = function (file: string) {
    const zip = new Zip(file);
    return zip;
}

$zip.zipFile = function (file: string, dest: string, options?: ZipOptions) {
    const zip = new Zip(dest);
    zip._zip.createZipFile(new File($files.path(file)), Zip.buildZipParameters(options));
    return zip;
}

$zip.zipDir = function (dir: string, dest: string, options?: ZipOptions) {
    const zip = new Zip(dest);
    zip._zip.createZipFileFromFolder($files.path(dir), Zip.buildZipParameters(options), false, -1);
    return zip;
}

$zip.zipFiles = function (fileList: string[], dest: string, options?: ZipOptions) {
    const list = new ArrayList();
    fileList.forEach(file => {
        list.add(new File($files.path(file)));
    });
    const zip = new Zip(dest);
    zip._zip.createZipFile(list, Zip.buildZipParameters(options));
    return zip;
}

$zip.unzip = function (zipFile: string, dest: string, options?: ZipOptions) {
    const zip = new Zip(zipFile);
    if (options && typeof (options.password) != 'undefined') {
        zip.setPassword(options.password);
    }
    zip.extractAll(dest, options);
}

export interface ZipOptions {
    password?: string
    compressionMethod?: string
    encryptionMethod?: string
    aesKeyStrength?: string
    encryptFiles?: boolean
    ignoreAttribute?: string[]
}
class Zip {
    _path: string
    _zip: any
    constructor(path: string) {
        this._path = $files.path(path);
        this._zip = new ZipFile(this._path);
    }
    static buildZipParameters(options?: ZipOptions) {
        const parameters = new Packages.net.lingala.zip4j.model.ZipParameters();
        if (!options) {
            return parameters;
        }
        options = Object.assign({}, options);
        if (typeof (options.password) == 'string') {
            options.password = new java.lang.String(options.password).toCharArray();
        }
        if (typeof (options.compressionMethod) == 'string') {
            options.compressionMethod = Zip.parseConstanst(options.compressionMethod);
        }
        if (typeof (options.encryptionMethod) == 'string') {
            options.encryptionMethod = Zip.parseConstanst(options.encryptionMethod);
        }
        if (typeof (options.aesKeyStrength) == 'string') {
            options.aesKeyStrength = Zip.parseConstanst(options.aesKeyStrength);
        }
        if (options.password !== undefined) {
            options.encryptFiles = true;
            options.encryptionMethod = options.encryptionMethod || Zip4jConstants.ENC_METHOD_AES;
            options.aesKeyStrength = options.aesKeyStrength || Zip4jConstants.AES_STRENGTH_256;
        }
        for (const key in options) {
            if (Object.prototype.hasOwnProperty.call(options, key)) {
                parameters[key] = (options as any)[key];
            }
        }
        return parameters;
    }
    static parseConstanst(name: string) {
        return Zip4jConstants[name.toUpperCase()];
    }
    static buildUnzipParameters(options?: ZipOptions) {
        const parameters = new Packages.net.lingala.zip4j.model.UnzipParameters();
        if (!options || !options.ignoreAttribute) {
            return parameters;
        }
        if (!Array.isArray(options.ignoreAttribute)) {
            throw new TypeError();
        }
        options.ignoreAttribute.forEach(i => {
            if (i === 'dateTime') {
                parameters.ignoreDateTimeAttributes = true;
            } else {
                const normName = i.substring(0, 1).toUpperCase() + i.substring(1);
                parameters['ignore' + normName + 'FileAttribute'] = true;
            }
        });
        return parameters;
    }
    addFile(file: string, options?: ZipOptions) {
        this._zip.addFile(new File($files.path(file)), Zip.buildZipParameters(options));
    }
    addFiles(fileList: string[], options?: ZipOptions) {
        const list = new ArrayList();
        fileList.forEach(file => {
            list.add(new File($files.path(file)));
        })
        this._zip.addFiles(list, Zip.buildZipParameters(options));
    }
    addFolder(file: string, options?: ZipOptions) {
        this._zip.addFolder(new File($files.path(file)), Zip.buildZipParameters(options));
    }
    extractAll(dest: string, options?: ZipOptions) {
        this._zip.extractAll($files.path(dest), Zip.buildUnzipParameters(options));
    }
    extractFile(file: string, dest: string, options?: ZipOptions, newFileName?: string | null) {
        newFileName = typeof (newFileName) == 'undefined' ? null : newFileName;
        this._zip.extractFile(file, $files.path(dest), Zip.buildUnzipParameters(options), newFileName);
    }
    setPassword(password: string) {
        this._zip.setPassword(password);
    }
    getFileHeader(file: string) {
        return this._zip.getFileHeader(file);
    }
    getFileHeaders() {
        return bridges.toArray(this._zip.getFileHeaders());
    }
    isEncrypted() {
        return this._zip.isEncrypted();
    }
    removeFile(file: string) {
        this._zip.removeFile(file);
    }
    isValidZipFile(): boolean {
        return this._zip.isValidZipFile();
    }
    getPath() {
        return this._path;
    }
}


export default $zip;