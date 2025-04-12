
let mainTh = threads.currentThread()

let e = events.emitter(mainTh)
let t1, t2, t3
e.on('t1', () => {
    t1 = mainTh === threads.currentThread()
})

ui.post(() => {
    var uiTh = threads.currentThread()
    var e3 = events.emitter(uiTh)
    e3.on('test',() => {
        t3 = uiTh === threads.currentThread()
    })
    threads.start(() => {
        e3.emit('test')
    })
})

let th2 = threads.start(() => {
    e.emit('t1')
    let e2 = events.emitter(threads.currentThread())
    e2.on('t2', () => {
        t2 = th2 === threads.currentThread()
    })
    threads.start(() => {
        e2.emit('t2')
    })
    setTimeout(() => { }, 500)
})

setTimeout(() => {
    console.assert(t1 === true,"t1 = " + t1)
    console.assert(t2 === true,"t2 = " + t2)
    console.assert(t3 === true,"t3 = " + t3)
}, 1000)