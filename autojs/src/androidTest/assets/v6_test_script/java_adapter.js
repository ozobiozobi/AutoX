let f = new JavaAdapter(java.io.File,{
  openc(){
    return 'kk'
  },
  getPath(){
    return 'r/'
  }
},'/')

console.assert(f.openc() === "kk");

console.assert(f.path === "r/");