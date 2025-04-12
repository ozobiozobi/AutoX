
namespace javaModule {
    function loadClass(name: string): JavaClass
    async function invokeUi<T>(
        javaobj: any,
        methodName: string,
        args: any[]): Promise<T>
    async function invokeDefault<T>(
        javaobj: any,
        methodName: string,
        args: any[]): Promise<T>
    async function invokeIo<T>(
        javaobj: any,
        methodName: string,
        args: any[]): Promise<T>

}