/**
 * @packageDocumentation
 * 该模块提供基于无障碍相关方法，使用方法与旧版api略有不同，
 * @example
 * ```js
 * import { click } from 'accessibility';
 * 
 * await click(100, 100)
 * ```
 */

import { invokeDefault, android, loadClass } from '@/java'
const { automator, currentActivity: currentActivity2,
    currentPackage: currentPackage2,
    selector, createStrokeDescriptionArray } = Autox.accessibility;

const StrokeDescription = loadClass('android.accessibilityservice.GestureDescription$StrokeDescription')
export interface Point {
    x: number;
    y: number;
}

export interface GestureOp {
    points: Point[];
    duration: number;
    delay?: number;
}
export function back() {
    return invokeDefault<boolean>(automator, 'back')
}

export function click(x: number, y: number) {
    return invokeDefault<boolean>(automator, 'click', [x, y])
}
export async function clickText(text: string, index: number = 0) {
    const target = await invokeDefault(automator, 'text', [text, index])
    return invokeDefault<boolean>(automator, 'click', [target])
}
export function currentActivity(): string | null {
    return currentActivity2()
}
export function currentPackage(): string | null {
    return currentPackage2()
}
export function home() {
    return invokeDefault<boolean>(automator, 'home')
}
export async function inputText(text: string, index?: number) {
    const e = await invokeDefault(automator, 'editable', [text, index ? index : 0])
    return invokeDefault<boolean>(automator, 'appendText', [e, text])
}
export function lockScreen() {
    return invokeDefault<boolean>(automator, 'lockScreen')
}
export function longClick(x: number, y: number) {
    return invokeDefault<boolean>(automator, 'longClick', [x, y])

}
export function openNotifications() {
    return invokeDefault<boolean>(automator, 'notifications')
}
export function openQuickSettings() {
    return invokeDefault<boolean>(automator, 'quickSettings')
}
export function performGesture(points: Point[], duration: number, delay: number = 0) {
    return invokeDefault<boolean>(automator, 'gesture', [delay, duration, ...points])
}
export function performGestures(gestures: GestureOp[]) {
    const arr = createStrokeDescriptionArray() as any[]
    for (const gesture of gestures) {
        const path = new android.graphics.Path();
        const now = gesture.points[0]
        path.moveTo(now.x, now.y)
        for (let i = 1; i < gesture.points.length; i++) {
            const p = gesture.points[i]
            path.lineTo(p.x, p.y)
        }
        const stroke = new StrokeDescription(path, gesture.delay || 0, gesture.duration)
        arr.push(stroke)
    }
    return invokeDefault<boolean>(automator, 'gestures', [arr])
}

export function performGlobalAction() {

}
export function press(x: number, y: number, duration: number) {
    return invokeDefault<boolean>(automator, 'press', [x, y, duration])
}
/**
 * 返回一个ui选择器，选择器的阻塞方法目前会阻塞nodejs事件循环，
 * 请通过java模块相关方法解决
 * @template
 * ```js
 * import { select } from 'accessibility'
 * const a = select().id('abc').text('abc).findOnce()
 * if (a != null ){
 *  a.click()
 * }
 * ```
 */
export function select(): Autox.UiSelector {
    return selector()
}

export async function setText(text: string, index: number = 0) {
    const e = await invokeDefault(automator, 'editable', [text, index ? index : 0])
    return invokeDefault<boolean>(automator, 'setText', [e, text])
}

export function swipe(x1: number, y1: number, x2: number, y2: number, duration: number) {
    return invokeDefault<boolean>(automator, 'swipe', [x1, y1, x2, y2, duration])
}

export function takeScreenshot() {
    return invokeDefault<Autox.Image>(automator, 'takeScreenshot2Sync')
}
/**
 * 显示电源设置。
 */
export function togglePowerDialog() {
    return invokeDefault<boolean>(automator, 'powerDialog')
}
/**
 * 模拟最近任务键
 */
export function toggleRecents() {
    return invokeDefault<boolean>(automator, 'recents')
}