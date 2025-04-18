namespace Autox {

    interface EventEmitter {
        on(event: string, listener: Function): this
        once(event: string, listener: Function): this
        off(event: string, listener: Function): this
        emit(event: string, ...args: any[]): boolean
        removeAllListeners(event?: string): this
        removeListener(eventName: string, listener: Function): this
        setMaxListeners(n: number): this
        getMaxListeners(): number
    }
    interface Events extends EventEmitter {
        emitter(thread: Thread): EventEmitter
        observeKey(): void
        ensureHandler(): void
        ensureHandler(): void
        setKeyInterceptionEnabled(enabled: boolean): void
        setKeyInterceptionEnabled(key: string, enabled: boolean): void

        onKeyDown(keyName: string, listener: Function): this
        onceKeyDown(keyName: string, listener: Function): this
        removeAllKeyDownListeners(keyName: string): this
        onKeyUp(keyName: string, listener: Function): this
        onceKeyUp(keyName: string, listener: Function): this
        removeAllKeyUpListeners(keyName: string): this
        onTouch(listener: Function): this
        removeAllTouchListeners(): this
        getTouchEventTimeout(): number
        setTouchEventTimeout(timeout: number): void
        observeNotification(): void
        observeToast(): void
        observeGesture(): void
        onNotification(listener: Function): this
        onToast(listener: Function): this
    }
}