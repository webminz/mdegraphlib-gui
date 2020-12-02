import { ModelEditorListener, ModelEditorEventSink } from "../api/model-editor-interface"


export class WebSocketEditorCallback implements ModelEditorEventSink {

    private handlers : Array<ModelEditorListener> = []
    private socket : WebSocket;

    public constructor(socket: WebSocket) {
        this.socket = socket;
        this.socket.addEventListener("message", (evt) => {
            this.handleServerMesage(evt);
        })
        this.socket.addEventListener("close", (evt) => {
            this.closeEditor(evt);
        })
        this.socket.addEventListener("error", (evt) => {
            this.handleServerError(evt);
        })
    }

    private handleServerMesage(evt: MessageEvent) {
        // TODO
    }

    private handleServerError(evt: Event) {
        // TODO
    }

    private closeEditor(evt: CloseEvent) {
        // TODO
    }

    public registerHandler(listener: ModelEditorListener) {
        this.handlers.push(listener)
    }

    public createNode(x: number, y: number, style: string) {
        this.handlers.forEach((handler) => {
           
        })
    }

    public createEdge(src: string, trg: string, style: string) {
        this.handlers.forEach((handler) => {
            
        })
    }

    public changeNodeLabel(id: string, value: string) {
        this.handlers.forEach((handler) => {
        })
    }
    
    public changeEdgeLabel(id: string, value: string) {
        this.handlers.forEach((handler) => {
        })
    }

    public addCompartment(id: string, key: string, value: string) {
        this.handlers.forEach((handler) => {
        })
    }

}