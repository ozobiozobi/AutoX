var javax = Packages.javax;
var MessageDigest = java.security.MessageDigest;
var Base64 = android.util.Base64;
var Crypto = com.stardust.autojs.core.cypto.Crypto.INSTANCE;
var Cipher = javax.crypto.Cipher;
var SecretKeySpec = javax.crypto.spec.SecretKeySpec;
var KeyPairGenerator = java.security.KeyPairGenerator;
var X509EncodedKeySpec = java.security.spec.X509EncodedKeySpec;
var PKCS8EncodedKeySpec = java.security.spec.PKCS8EncodedKeySpec;
var KeyFactory = java.security.KeyFactory;
var CipherOutputStream = javax.crypto.CipherOutputStream;
var ByteArrayOutputStream = java.io.ByteArrayOutputStream;

function $cypto() {

}
export type Algorithm = 'MD5' | 'SHA-1' | 'SHA-256'
export interface Options {
    input?: 'string' | 'file' | 'base64' | 'hex'
    output?: 'string' | 'base64' | 'hex' | 'bytes' | 'file'
    encoding?: string
    dest?: string
}

$cypto.digest = function (message: string, algorithm: Algorithm, options?: Options): string {
    options = options || {};
    const instance = MessageDigest.getInstance(algorithm);
    $cypto._input(message, options, (bytes: unknown, start: number, length: number) => {
        instance.update(bytes, start, length);
    });
    const bytes = instance.digest();
    return $cypto._output(bytes, options, 'hex');
}

$cypto._input = function (input: string, options: Options, callback: any) {
    if (options.input == 'file') {
        const fis = new java.io.FileInputStream(input);
        const buffer = util.java.array('byte', 4096);
        while (true) {
            var r = fis.read(buffer);
            if (r > 0) {
                callback(buffer, 0, r);
            } else {
                break;
            }
        }
        return;
    }
    if (options.input == 'base64') {
        input = Base64.decode(input, Base64.NO_WRAP);
    } else if (options.input == 'hex') {
        input = $cypto._fromHex(input);
    } else {
        const encoding = options.encoding || "utf-8";
        if (typeof (input) == 'string') {
            input = new java.lang.String(input).getBytes(encoding);
        }
    }
    callback(input, 0, input.length);
}

$cypto._output = function (bytes: any, options: Options, defFormat: Options['output']) {
    const format = options.output || defFormat;
    if (format == 'base64') {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
    if (format == 'bytes') {
        return bytes;
    }
    const encoding = options.encoding || "utf-8";
    if (format == 'string') {
        return String(new java.lang.String(bytes, encoding));
    }
    return $cypto._toHex(bytes);
}

$cypto._toHex = function (bytes: unknown) {
    return Crypto.toHex(bytes);
}

$cypto._fromHex = function (bytes: unknown) {
    return Crypto.fromHex(bytes);
}



export type EncryptAlgorithm = 'AES/ECB/PKCS5padding' | 'RSA/ECB/PKCS1padding' | 'RSA'
$cypto.encrypt = function (data: string, key: any, algorithm: EncryptAlgorithm, options?: Options) {
    return $cypto._chiper(data, Cipher.ENCRYPT_MODE, key, algorithm, options);
}

$cypto.decrypt = function (data: any, key: any, algorithm: EncryptAlgorithm, options?: Options) {
    return $cypto._chiper(data, Cipher.DECRYPT_MODE, key, algorithm, options);
}

$cypto._chiper = function (data: string, mode: any, key: any, algorithm: EncryptAlgorithm, options?: Options) {
    options = options || {};
    const cipher = Cipher.getInstance(algorithm);
    cipher.init(mode, key.toKeySpec(algorithm));
    let os;
    const isFile = options.output == 'file' && options.dest;
    if (isFile) {
        os = new java.io.FileOutputStream(options.dest);
    } else {
        os = new ByteArrayOutputStream();
    }
    const cos = new CipherOutputStream(os, cipher);
    $cypto._input(data, options, (bytes: unknown, start: number, length: number) => {
        cos.write(bytes, start, length);
    });
    cos.close();
    os.close();
    if (!isFile) {
        const result = os.toByteArray();
        return $cypto._output(result, options, 'bytes');
    }
}

$cypto.generateKeyPair = function (algorithm: EncryptAlgorithm, length?: number) {
    const generator = KeyPairGenerator.getInstance(algorithm);
    length = length || 256;
    generator.initialize(length);
    const keyPair = generator.generateKeyPair();
    return new KeyPair(keyPair.getPublic().getEncoded(), keyPair.getPrivate().getEncoded());
}

class Key {
    keyPair: any;
    data: any

    constructor(data: string, options: any) {
        options = options || {};
        this.keyPair = options.keyPair;
        const bos = new java.io.ByteArrayOutputStream();
        $cypto._input(data, options, (bytes: any, start: number, length: number) => {
            bos.write(bytes, start, length);
        });
        this.data = bos.toByteArray();
    }
    toKeySpec(algorithm: string) {
        const i = algorithm.indexOf("/");
        if (i >= 0) {
            algorithm = algorithm.substring(0, i);
        }
        if (algorithm == 'RSA') {
            if (this.keyPair == 'public') {
                return KeyFactory.getInstance(algorithm).generatePublic(new X509EncodedKeySpec(this.data));
            }
            if (this.keyPair == 'private') {
                return KeyFactory.getInstance(algorithm).generatePrivate(new PKCS8EncodedKeySpec(this.data));
            }
            throw new Error()
        }
        return new SecretKeySpec(this.data, algorithm);
    }
    toString() {
        const data = Base64.encodeToString(this.data, Base64.NO_WRAP);
        if (this.keyPair) {
            return "Key[" + this.keyPair + "]{data='" + data + "'}";
        }
        return "Key{data='" + data + "'}";
    }
}

$cypto.Key = Key;

class KeyPair {
    publicKey: any
    privateKey: any

    constructor(publicKey: any, privateKey: any, options?: any) {
        options = Object.assign({}, options || {});
        options.keyPair = 'public';
        this.publicKey = new Key(publicKey, options);
        options.keyPair = 'private';
        this.privateKey = new Key(privateKey, options);
    }
}

export default $cypto;