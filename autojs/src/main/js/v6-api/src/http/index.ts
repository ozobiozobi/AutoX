
import { HttpOptions, K, Response } from "./types";
declare const continuation: any;

const MutableOkHttp = com.stardust.autojs.core.http.MutableOkHttp
const { Callback, Request, RequestBody, MediaType, FormBody, MultipartBody } = Packages["okhttp3"]
var http = {
    get, post, postJson, request, buildRequest, client, postMultipart
};

const __okhttp__ = new MutableOkHttp();

function get(url: string): Response
function get(url: string, options: HttpOptions, callback: K): void
function get(url: string, options?: HttpOptions, callback?: K) {
    const o: HttpOptions = options || { method: "GET" };
    o.method = "GET";
    return http.request(url, options, callback);
}


function client() {
    return __okhttp__.client();
}

function post(url: string, data: any, options?: HttpOptions, callback?: K) {
    const o: HttpOptions = options || { method: "POST" };
    o.method = "POST";
    o.contentType = o.contentType || "application/x-www-form-urlencoded";
    if (data) {
        fillPostData(o, data);
    }
    return http.request(url, o, callback);
}

function postJson(url: string, data?: any, options?: HttpOptions, callback?: K) {
    const o: HttpOptions = options || { method: "POST" };
    o.contentType = "application/json";
    return http.post(url, data, o, callback);
}

function postMultipart(url: string, files?: any, options?: HttpOptions, callback?: K) {
    const o: HttpOptions = options || { method: "POST" };
    o.method = "POST";
    o.contentType = "multipart/form-data";
    o.files = files;
    return http.request(url, options, callback);
}


function request(url: string, options?: HttpOptions, callback?: K): Response | void {
    var cont: any = null;

    if (!callback && ui.isUiThread() && continuation.enabled) {
        cont = continuation.create();
    }
    var call = http.client().newCall(http.buildRequest(url, options));
    if (!callback && !cont) {
        return wrapResponse(call.execute());
    }
    call.enqueue(new Callback({
        onResponse: function (call: unknown, res: any) {
            res = wrapResponse(res);
            if (cont) {
                cont.resume(res);
            }
            if (callback) {
                callback(res);
            }
        },
        onFailure: function (call: unknown, ex: any) {
            if (cont) {
                cont.resumeError(ex);
            }
            if (callback) {
                callback(null, ex);
            }
        }
    }));
    if (cont) {
        return cont.await();
    }
}

function buildRequest(url: string, options?: HttpOptions) {
    options = options || { method: "GET" };
    var r = new Request.Builder();
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "http://" + url;
    }
    r.url(url);
    if (options.headers) {
        setHeaders(r, options.headers);
    }
    if (options.body) {
        r.method(options.method, parseBody(options, options.body));
    } else if (options.files) {
        r.method(options.method, parseMultipart(options.files));
    } else {
        r.method(options.method, null);
    }
    return r.build();
}

function parseMultipart(files: any) {
    var builder = new MultipartBody.Builder()
        .setType(MultipartBody.FORM);
    for (var key in files) {
        if (!Object.prototype.hasOwnProperty.call(files, key)) {
            continue;
        }
        var value = files[key];
        if (typeof (value) == 'string') {
            builder.addFormDataPart(key, value);
            continue;
        }
        var path, mimeType, fileName;
        if (typeof (value.getPath) == 'function') {
            path = value.getPath();
        } else if (value.length == 2) {
            fileName = value[0];
            path = value[1];
        } else if (value.length >= 3) {
            fileName = value[0];
            mimeType = value[1]
            path = value[2];
        }
        var file = new com.stardust.pio.PFile(path);
        fileName = fileName || file.getName();
        mimeType = mimeType || parseMimeType(file.getExtension());
        builder.addFormDataPart(key, fileName, RequestBody.create(MediaType.parse(mimeType), file));
    }
    return builder.build();
}

function parseMimeType(ext: string) {
    if (ext.length == 0) {
        return "application/octet-stream";
    }
    return android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
        || "application/octet-stream";
}

function fillPostData(options: HttpOptions, data: any) {
    if (options.contentType == "application/x-www-form-urlencoded") {
        var b = new FormBody.Builder();
        for (var key in data) {
            if (Object.prototype.hasOwnProperty.call(data, key)) {
                b.add(key, data[key]);
            }
        }
        options.body = b.build();
    } else if (options.contentType == "application/json") {
        options.body = JSON.stringify(data);
    } else {
        options.body = data;
    }
}

function setHeaders(r: any, headers: Record<string, string>) {
    for (var key in headers) {
        if (Object.prototype.hasOwnProperty.call(headers, key)) {
            var value = headers[key];
            if (Array.isArray(value)) {
                value.forEach(v => {
                    r.header(key, v);
                });
            } else {
                r.header(key, value);
            }
        }
    }
}

function parseBody(options: HttpOptions, body: any) {
    if (typeof (body) == "string") {
        body = RequestBody.create(MediaType.parse(options.contentType), body);
    } else if (body instanceof RequestBody) {
        return body;
    } else {
        body = new RequestBody({
            contentType: function () {
                return MediaType.parse(options.contentType);
            },
            writeTo: body
        });
    }
    return body;
}

function wrapResponse(res: any): Response {
    var headers = res.headers();
    const h: Response['headers'] = {};
    for (var i = 0; i < headers.size(); i++) {
        var name = headers.name(i);
        var value = headers.value(i);
        if (Object.prototype.hasOwnProperty.call(h, name)) {
            var origin = h[name];
            if (!Array.isArray(origin)) {
                h[name] = [origin];
            }
            (h[name] as string[]).push(value);
        } else {
            h[name] = value;
        }
    }

    var body = res.body();
    const body2: Response['body'] = {
        string: body.string.bind(body),
        bytes: body.bytes.bind(body),
        json: function () {
            return JSON.parse(body.string());
        },
        contentType: body.contentType()
    };
    const request = res.request();
    var r: Response = {
        statusCode: res.code(),
        statusMessage: res.message(),
        headers: h,
        body: body2,
        request,
        url: request.url(),
        method: request.method()
    };
    return r;
}

export default http;