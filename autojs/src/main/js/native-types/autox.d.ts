
namespace Autox {
    interface ShellResult {
        code: number
        result: string
        error: string
    }
    interface Shizuku {
        isShizukuAlive(): boolean
        runRhinoScriptFile(path: string): string
        runRhinoScript(script: string): string
        runShizukuShellCommand(cmd: string): ShellResult
        openAccessibility(): void
    }
    interface PFileInterface {
        getPath(): string
    }
    interface Shell2 {
        exec(cmd: string): void
        execAndWaitFor(cmd: string): ShellResult
        exit(): void
        exitAndWaitFor(): void
        setCallback(callback: {
            onOutput: (out: string) => void
            onNewLine: (line: string) => void
        }): void
    }
    interface Shell {
        exec(cmd: string, root: boolean): ShellResult
        createShell(root: boolean): Shell2
    }
    interface Files {
        path(relativePath: string): string
        cwd(): string
        readAssets(path: string, encoding?: string): string
        join(parent: string, ...child: string[]): string
        read(path: string): string
        open(path: string, mode?: string, encoding?: string, bufferSize?: number): PFileInterface
        create(path: string): boolean
        createIfNotExists(path: string): boolean
        createWithDirs(path: string): boolean
        exists(path: string): boolean
        ensureDir(path: string): boolean
        read(path: string, encoding?: string): string
        readBytes(path: string): any
        write(path: string, text: string, encoding: string): void
        append(path: string, text: string, encoding?: string): void
        appendBytes(path: string, bytes: any): void
        writeBytes(path: string, bytes: any): string
        copy(pathFrom: string, pathTo: string): boolean
        renameWithoutExtension(path: string, newName: string): boolean
        rename(path: string, newName: string): boolean
        move(path: string, newPath: string): boolean
        getExtension(fileName: string): string | null
        getName(filePath: string): string
        getNameWithoutExtension(filePath: string): string
        remove(path: string): boolean
        removeDir(path: string): boolean
        getSdcardPath(): string
        listDir(path: string, filter?: (name: string) => boolean): string[]
        isFile(path: string): boolean
        isDir(path: string): boolean
        isEmptyDir(path: string): boolean
        join(parent: string, ...child: string[]): string
        getHumanReadableSize(bytes: number | bigint): string
        getSimplifiedPath(path: string): string
    }

    interface Media {
        scanFile(path: string): void
        playMusic(path: string, volume?: number, looping?: boolean): void
        musicSeekTo(m: number): void
        isMusicPlaying(): boolean
        pauseMusic(): void
        resumeMusic(): void
        getMusicDuration(): number
        getMusicCurrentPosition(): number
        stopMusic(): void
    }
    interface Ui {
        readonly layoutInflater: any
        bindingContext: any
        __proxy__: any
    }
    interface Threads {
        currentThread(): any
        start(runnable: Runnable): TimerThread
        exit(): void
        shutDownAll(): void
        lock(): any
        disposable(): any
        runTaskForThreadPool(runnable: BaseFunction): void
        atomic(value: number | bigint): any
        hasRunningThreads(): boolean
    }
    interface Image {
        mat: any
        height: number
        width: number
    }
    interface AppUtils {
        fileProviderAuthority: string | null
        launchPackage(packageName: string): boolean
        sendLocalBroadcastSync(intent: android.Intent)
        launchApp(appName: string): boolean
        getPackageName(appName: string): string | null
        getInstalledPackages(flags: number): Array<PackageInfo>
        getAppName(packageName: string): string | null
        uninstall(packageName: string)
        viewFile(path: string)
        editFile(path: string)
        getUriForFile(path: string): android.Uri
        openUrl(url: string)
        openAppSetting(packageName: string): boolean
    }
    interface ScriptExecution {
        getEngine(): ScriptEngine
        getConfig(): any
    }
    interface ScriptEngine {
        isDestroyed(): boolean
        forceStop()
        cwd(): string | null
        getSource(): ScriptSource
        emit(eventName: string, ...args: any[]): void
    }
    interface Engines {
        [key: string]: any
        stopAll(): void
        stopAllAndToast(): void
        myEngine(): ScriptEngine
        all(): ScriptEngine[]
        execScript(name: string, script: string, config?: EngineConfig): ScriptExecution
        execScriptFile(path: string, config?: EngineConfig): ScriptExecution
        execAutoFile(path: string, config?: EngineConfig): ScriptExecution
    }
    interface Runtime {
        shizuku: Shizuku
        shell: Shell
        files: Files
        media: Media
        ui: Ui
        threads: Threads
        floaty: any
        colors: any
        images: any
        bridges: any
        automator: Automator
        accessibilityBridge: any
        app: AppUtils
        engines: Engines
        dialogs: any
        events: Events
        plugins: any
        sensors: any
        getScreenMetrics(): any
        evalInContext(script: string, context: Object): any
        getUiHandler: () => any
        selector(): UiSelector
        getProperty(name: string): any
    }

    interface JsBridge {
        registerHandler: (name: string,
            handler: (data: string | null,
                callBack?: (data?: string) => void) => void) => void
        callHandler: (name: string, data?: string,
            callBack?: (data: string | null) => void) => void
    }
}