import { setGlobal, setGlobalAnd$ } from './utils'
import shizuku from './shizuku'
import base64 from './base64'
import shell from './shell'
import './inline_modules/files'
import media, { Media } from './inline_modules/media'
import ui, { Ui } from './inline_modules/ui'
import selector from './inline_modules/selector'
import threads from './inline_modules/threads'
import floaty from './inline_modules/floaty'
import images from './images'
import automator from './inline_modules/automator'

type _threads = typeof threads
type _selector = typeof selector
type _shell = typeof shell
type _shizuku = typeof shizuku
type _base64 = typeof base64
type _floaty = typeof floaty
type _images = typeof images
type _automator = typeof automator
declare global {
    var shizuku: _shizuku
    var media: Media
    var ui: Ui
    var shell: _shell
    var base64: _base64
    var selector: _selector
    var threads: _threads
    var floaty: _floaty
    var images: _images
    var automator: _automator
}


setGlobalAnd$('selector', selector)
setGlobal('shizuku', shizuku)
setGlobalAnd$('ui', ui)
setGlobalAnd$('base64', base64)
setGlobalAnd$('shell', shell)
setGlobalAnd$("media", media)
setGlobalAnd$('threads', threads)
setGlobalAnd$('floaty', floaty)
setGlobalAnd$('images', images)
setGlobalAnd$('automator', automator)

setGlobal('KeyEvent', android.view.KeyEvent);
setGlobal('Paint', android.graphics.Paint);
setGlobal('Canvas', com.stardust.autojs.core.graphics.ScriptCanvas)
setGlobal('Image', com.stardust.autojs.core.image.ImageWrapper)
setGlobal('OkHttpClient', Packages["okhttp3"].OkHttpClient)
setGlobal('Intent', android.content.Intent)

export { shizuku }