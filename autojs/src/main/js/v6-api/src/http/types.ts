
export interface Response {
    statusCode: number
    statusMessage: string
    headers: Record<string, string | string[]>
    body: {
        string(): string
        bytes(): Uint8Array
        json(): any
        contentType: string
    }
    request: {
        url(): string
        method(): string
    }
    url: string
    method: string
}

export interface HttpOptions {
    headers?: Record<string, string>
    method: string
    contentType?: string
    body?: any
    files?: any
}

export type K = (res: Response | null, ex?: Error) => void