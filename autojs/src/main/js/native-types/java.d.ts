declare namespace java {
    export interface javaObject {
        toString(): string;
    }
    export interface JavaClass {
        new(...args: any): javaObject;
        readonly name: string;
    }

}