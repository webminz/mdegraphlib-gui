
export interface ModelElement {
    id: string
    type?: string
    style? : string
}

export interface Node extends ModelElement {
    label?: string
    xpos: number
    ypos: number
    width?: number
    height?: number
    compartments?: Array<Compartment>
}

export interface Edge extends ModelElement {
    label?: string
    src: string
    trg: string
    srclabel?: string
    trglabel?: string
    anchors?: Array<[number, number]>
}

export interface Compartment extends ModelElement {
    owner: string
    key: string
    value: string
    pos?: number
}

export interface Annotation extends ModelElement {
    labelx: number
    labely: number
}

export interface ModelEditorListener {
    nodeCreated(n: Node): void
    edgeCreated(e: Edge): void
    // TODO New features
    //  createCompartment(c: Compartment): void
    //  createAnnotation(a: Annotation): void
  
    elementDeleted(id: string): void
    labelModified(id: string, value: string): void
    compartmentAdded(id: string, key: string, value: string): void
  
    // TODO implement in ElementFactory
    //  move(id: string, x: number, y: number): void
    //  resize(id: string, width: number, height: number): void
    //  rearrange(id: string, anchors: Array<[number, number]>): void
    //  changeStyle(id: string, style: string): void
  
  // TODO add notification system
  //  error(text: string, id?: string): void
}

export interface ModelEditorEventSink {
    registerHandler(handler: ModelEditorListener): void
    createNode(x: number, y: number, style: string): void
    createEdge(src: string, trg: string, style: string): void
    changeNodeLabel(id: string, value: string): void
    changeEdgeLabel(id: string, value: string): void
    addCompartment(id: string, key: string, value: string): void

}

