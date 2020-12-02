

export interface ModelInterface {
    createNode(type: string): string
    createEdge(src: string, trg: string, type: string): string
    deleteNode(id:string): string
    deleteEdge(id:string): string
    updateEdge(id: string, src: string, trg: string): string
    createAttribute(owner: string, key: string, value: string): string
    deleteAttribute(id: string): string
    updateAttribute(id: string, value: string): string
    createDiagram(type: string, elements: Array<string>): string
    deleteDiagram(id: string): string
}