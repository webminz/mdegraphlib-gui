import { ModelEditorListener, ModelEditorEventSink } from "../api/model-editor-interface"

export class StubEditorCallbak implements ModelEditorEventSink {

    private idCounter : number = 0
    private handlers : Array<ModelEditorListener> = []

    public registerHandler(listener: ModelEditorListener) {
        this.handlers.push(listener)
    }

    public createNode(x: number, y: number, style: string) {
        this.handlers.forEach((handler) => {
            handler.nodeCreated({
                id : (this.idCounter++).toString(),
                xpos : x,
                ypos : y,
                type : 'Node',
                style : style 
            })
        })
    }

    public createEdge(src: string, trg: string, style: string) {
        this.handlers.forEach((handler) => {
            handler.edgeCreated({
                id: (this.idCounter++).toString(),
                src: src,
                trg: trg,
                style : style,
                type : 'edge'
            })
        })
    }

    public changeNodeLabel(id: string, value: string) {
        this.handlers.forEach((handler) => {
            handler.labelModified(id, value)
        })
    }
    
    public changeEdgeLabel(id: string, value: string) {
        this.handlers.forEach((handler) => {
            handler.labelModified(id, value)
        })
    }

    public addCompartment(id: string, key: string, value: string) {
        this.handlers.forEach((handler) => {
            handler.compartmentAdded(id, key, value)
        })
    }




}