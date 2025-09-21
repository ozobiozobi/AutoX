
var img = images.read("./1.png");

images.save(img, "./1.jpg", "jpg", 80);
console.assert(files.exists("./1.jpg"), "save jpg failed");

var base64Str = images.toBase64(img);
console.assert(typeof base64Str === "string" && base64Str.length > 0, "toBase64 failed");

var img2 = images.fromBase64(base64Str);
console.assert(img2 instanceof Image, "fromBase64 failed");

var clip = images.clip(img, 50, 50, 100, 100);
console.assert(clip instanceof Image && clip.getWidth() === 100 && clip.getHeight() === 100, "clip failed");
clip.recycle();

var resize = images.resize(img, [200, 200]);
console.assert(resize instanceof Image && resize.getWidth() === 200 && resize.getHeight() === 200, "resize failed");
resize.recycle();

var concat = images.concat(img, img2, "TOP");
console.assert(concat instanceof Image && concat.getWidth() === img.getWidth()
    && concat.getHeight() === (img.getHeight() + img2.getHeight()), "concat failed");
concat.recycle();

var template = images.read("./template.png");
images.findImage(img, template);
var point = images.findImage(img, template);
console.assert(point != null && point.x == 158 && point.y == 210, "findImage failed");
template.recycle();

img2.recycle();
img.recycle();