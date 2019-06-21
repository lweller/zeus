export class ConcurrentModificationError<T> extends Error {
    constructor(public currentVersion: T) {
        super('Some entity was modified concurrently. Use given current version and retry.');
    }
}