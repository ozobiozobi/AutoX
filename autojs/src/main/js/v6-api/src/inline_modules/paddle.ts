const paddleApi = new com.stardust.autojs.runtime.api.Paddle();
const paddle = {
    ocr, ocrText
}

function ocr(img: Autox.Image, path?: string): any[]
function ocr(img: Autox.Image, cpuThreadNum: number, useSlim: boolean): any[]
function ocr() {
    if (!arguments[0]) return []
    let result
    switch (arguments.length) {
        case 1:
            result = paddleApi.ocr(arguments[0])
            break
        case 2:
            result = paddleApi.ocr(arguments[0], arguments[1])
            break
        case 3:
            result = paddleApi.ocr(arguments[0], arguments[1], arguments[2])
    }
    return global.util.java.toJsArray(result)
}

function ocrText(img: Autox.Image): string[]
function ocrText(img: Autox.Image, cpuThreadNum: number, useSlim: boolean): string[]
function ocrText(image: Autox.Image) {
    if (!arguments[0]) return []
    let result: any[] = []
    switch (arguments.length) {
        case 1:
            result = paddle.ocr(arguments[0])
            break
        case 2:
            result = paddle.ocr(arguments[0], arguments[1])
            break
        case 3:
            result = paddle.ocr(arguments[0], arguments[1], arguments[2])
    }
    return result.map(e => e.words)
}

export default paddle