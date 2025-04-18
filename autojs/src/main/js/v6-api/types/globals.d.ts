

declare var global = globalThis

declare var android, java, com, Packages, org
/**
 * @param message 要显示的信息
 */
declare function toast(message: string): void
/**
 * @param message 要显示的信息
 */
declare function toastLog(message: string): void

/**
 * 一个 android.content.Context 对象。
 */
declare var context: android.Context

declare var runtime: Autox.Runtime

declare var exit: (err?) => void

declare var util: any

declare var sleep: (ms: number) => void

declare var exit: (err?: Error) => void

declare var setClip: (text: string) => void
declare var getClip: () => string

declare var currentPackage: () => string | null

declare var currentActivity: () => string | null