const File = java.io.File
// 创建两个文件夹与三个文件
const testDir = $files.path("./zip_test/")
const outDir = $files.path("./zip_out/")

$files.create(testDir);
$files.create(outDir);
$files.write(testDir + "/1.txt", "Hello, World");
$files.write(testDir + "/2.txt", "GoodBye, World");
$files.write(testDir + "/3.txt", "Autox.js");

// 1. 压缩文件夹
// 要压缩的文件夹路径
let dir = testDir;
// 压缩后的文件路径
let zipFile = outDir + '/未加密.zip';
$files.remove(zipFile);
$zip.zipDir(dir, zipFile);
console.assert(File(zipFile).isFile())

// 2.加密压缩文件夹
let encryptedZipFile = outDir + '/加密.zip';
$files.remove(encryptedZipFile);
$zip.zipDir(dir, encryptedZipFile, {
    password: 'Autox.js'
});
console.assert(File(encryptedZipFile).isFile())

// 3. 压缩单个文件
let zipSingleFie = outDir + '/单文件.zip'
$files.remove(zipSingleFie);
$zip.zipFile(testDir + '/1.txt', zipSingleFie);
console.assert(File(zipSingleFie).isFile())

// 4. 压缩多个文件
let zipMultiFile = outDir + '/多文件.zip';
$files.remove(zipMultiFile);
let fileList = [testDir + '/1.txt', testDir + '/2.txt']
$zip.zipFiles(fileList, zipMultiFile);
console.assert(File(zipMultiFile).isFile())

// 5. 解压文件
$zip.unzip(outDir + '/未加密.zip', outDir + '/未加密/');
console.assert($files.read(outDir + '/未加密/zip_test/1.txt') == 'Hello, World')

// 6. 解压加密的zip
$zip.unzip(outDir + '/加密.zip', outDir + '/加密/', {
    password: 'Autox.js'
});
console.assert($files.read(outDir + '/加密/zip_test/2.txt') == 'GoodBye, World')

// 7. 从压缩包删除文件
let z = $zip.open(outDir + '/多文件.zip');
z.removeFile('1.txt');

// 8. 为压缩包增加文件
z.addFile(testDir + '/3.txt');

$files.remove(testDir);
$files.remove(outDir);