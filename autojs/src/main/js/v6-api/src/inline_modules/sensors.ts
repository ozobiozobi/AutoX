
export interface Sensors extends Autox.EventEmitter {
    unregister(sensor: Autox.EventEmitter): void
    unregisterAll(): void
    ignoresUnsupportedSensor: boolean
}

var sensors = Object.create(runtime.sensors);
export default sensors as Sensors