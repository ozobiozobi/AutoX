namespace Autox {
    interface UiSelector extends UiGlobalSelector {
        find(max?: number): UiObjectCollection
        findOne(timeout?: number): UiObject
        findOnce(index?: number): UiObject?;
        exists(): boolean
        untilFindOne(): UiObject
        waitFor(): void
        textMatches(regex: string): UiGlobalSelector
        convertRegex(regex: string): string
    }
    interface UiObject {
        parent(): UiObject
        child(i: number): UiObject
        indexInParent(): number
        find(selector: UiGlobalSelector): UiObjectCollection
        findOne(selector: UiGlobalSelector): UiObject?;
        children(): UiObjectCollection
        childCount(): number
        bounds(): Rect
        boundsInParent(): Rect
        drawingOrder(): number
        id(): string
        text(): string
        findByText(str): UiObjectCollection

        click(): boolean
        longClick(): boolean
        accessibilityFocus(): boolean
        clearAccessibilityFocus(): boolean
        focus(): boolean
        clearFocus(): boolean
        copy(): boolean
        paste(): boolean
        select(): boolean
        cut(): boolean
        collapse(): boolean
        expand(): boolean
        dismiss(): boolean
        show(): boolean
        scrollForward(): boolean
        scrollBackward(): boolean
        scrollUp(): boolean
        scrollDown(): boolean
        scrollLeft(): boolean
        scrollRight(): boolean
        contextClick(): boolean
        setSelection(s: number, e: number): boolean
        setText(text: string): boolean
        setProgress(value: number): boolean
        scrollTo(row: number, column: number): boolean

    }
    interface UiObjectCollection extends Array<UiObject> {
        empty(): boolean
        find(selector: UiGlobalSelector): UiObjectCollection
        findOne(selector: UiGlobalSelector): UiObject?;
    }
    interface UiGlobalSelector {
        id(id: string): this
        idContains(str: string): this
        idStartsWith(prefix: string): this
        idEndsWith(suffix: string): this
        idMatches(regex: string): this
        text(text: string): this
        textContains(str: string): this
        textStartsWith(prefix: string): this
        textEndsWith(suffix: string): this
        textMatches(regex: string): this
        desc(desc: string): this
        descContains(str: string): this
        descStartsWith(prefix: string): this
        descEndsWith(suffix: string): this
        descMatches(regex: string): this
        className(className: string): this
        classNameContains(str: string): this
        classNameStartsWith(prefix: string): this
        classNameEndsWith(suffix: string): this
        classNameMatches(regex: string): this
        packageName(packageName: string): this
        packageNameContains(str: string): this
        packageNameStartsWith(prefix: string): this
        packageNameEndsWith(suffix: string): this
        packageNameMatches(regex: string): this
        bounds(l: number, t: number, r: number, b: number): this
        boundsInside(l: number, t: number, r: number, b: number): this
        boundsContains(l: number, t: number, r: number, b: number): this
        drawingOrder(order: number): this
        //
        checkable(b?: boolean): this
        checked(b?: boolean): this
        focusable(b?: boolean): this
        focused(b?: boolean): this
        visibleToUser(b?: boolean): this
        accessibilityFocused(b?: boolean): this
        selected(b?: boolean): this
        clickable(b?: boolean): this
        longClickable(b?: boolean): this
        enabled(b?: boolean): this
        password(b?: boolean): this
        scrollable(b?: boolean): this
        editable(b?: boolean): this
        contentInvalid(b?: boolean): this
        contextClickable(b?: boolean): this
        multiLine(b?: boolean): this
        dismissable(b?: boolean): this
        //
        depth(d: number): this
        row(d: number): this
        rowCount(d: number): this
        rowSpan(d: number): this
        column(d: number): this
        columnCount(d: number): this
        columnSpan(d: number): this
        indexInParent(index: number): this
        filter(filter: (node: UiObject) => boolean): this
        findOf(node: UiObject, max?: number): UiObjectCollection
        findOneOf(node: UiObject): UiObject
        addFilter(filter: (node: UiObject) => boolean): this
        algorithm(algorithm: string): this
        findAndReturnList(node: UiObject, max: number): Array<UiObject>
    }

    interface Automator {
        [key: string]: any
        back(): void
        home(): void
        powerDialog(): void
        notifications(): void
        quickSettings(): void
        recents(): void
        splitScreen(): void
        takeScreenshot(): void
        lockScreen(): void
        swipe(x1: number, y1: number, x2: number, y2: number, duration: number): void
        gesture(id: number, duration: number, ...args: [number, number][]): void
    }
}