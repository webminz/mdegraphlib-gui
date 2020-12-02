import * as joint from '../joint-dist/joint';
import {ElementFactory} from './element-factory';
import { Palette } from './palette';
import { StyleManager } from '../api/model-editor-styles';
import { ModelEditorListener, ModelEditorEventSink, Node, Edge } from '../api/model-editor-interface';
import { createDiv } from '../lib/htm-utils';
import { InteractionManager } from './interaction-manager';
import { ExtraControls } from './extra-controls';
import { WebSocketEditorCallback } from '../callbacks/ws-editor-callback';
import { StubEditorCallbak } from '../callbacks/stub-editor-callback';

const CANVAS_CONTAINER_ELEMENT_ID : string = 'diaged-canvas-container'
const TEMPORARY_ELEMENTS_CONTAINER_ELEMENT_ID : string = 'diaged-temp-pane'
const PALETTE_ELEMENTS_CONTAINER_ELEMENT_ID : string = 'diaged-palette'
const ADDITIONAL_CONTROL_ELEMENTS_CONTAINER_ELEMENT_ID : string = 'diaged-controls'

export class DiagramEditor implements ModelEditorListener {
    private container: HTMLElement
    private instanceId: string

    private styleManager: StyleManager
    private editorCallback: ModelEditorEventSink
    private interaction: InteractionManager
    private elementFactory: ElementFactory

    private graph: joint.dia.Graph
    private paper: joint.dia.Paper
    private nodes : Map<string, joint.dia.Element>
    private edges : Map<string, joint.dia.Link>

    private palette : Palette
    private extraControls : ExtraControls

    
    constructor(container: HTMLElement, instanceId: string, styleManager: StyleManager, callback: ModelEditorEventSink) {
        this.container = container;
        this.instanceId = instanceId;

        this.nodes = new Map();
        this.edges = new Map();
        
        let editorDiv : HTMLDivElement = createDiv(this.container, CANVAS_CONTAINER_ELEMENT_ID)
        
        this.graph = new joint.dia.Graph()
        this.paper = new joint.dia.Paper( {
            el: editorDiv,
            model: this.graph,
            width: editorDiv.getBoundingClientRect().width,
            height: editorDiv.getBoundingClientRect().height,
            gridSize: 1
        });

        this.styleManager = styleManager
        this.editorCallback = callback
        this.elementFactory = new ElementFactory(this.graph, this.paper, this.styleManager, (link) => {
            this.triggerLabelEditorForLink(link)
        })

        this.interaction = new InteractionManager(createDiv(this.container, TEMPORARY_ELEMENTS_CONTAINER_ELEMENT_ID), this.editorCallback)
        this.palette = new Palette(createDiv(this.container, PALETTE_ELEMENTS_CONTAINER_ELEMENT_ID), this.interaction, styleManager)
        this.extraControls = new ExtraControls(createDiv(this.container, ADDITIONAL_CONTROL_ELEMENTS_CONTAINER_ELEMENT_ID), this.interaction)

        this.paper.on('blank:pointerclick', () => {
            this.interaction.canvasClicked();
        })

        this.paper.on('element:pointerclick', (elementView) => {
            if (elementView instanceof joint.dia.ElementView) {
                this.interaction.handleElementClick(elementView as joint.dia.ElementView)
            }
        });

        this.paper.on('link:mouseenter', (linkView) => {
            if (linkView instanceof joint.dia.LinkView) {
                linkView.showTools();
            }
        });

        this.paper.on('link:mouseleave', (linkView) => {
            if (linkView instanceof joint.dia.LinkView) {
                linkView.hideTools();
            }
        });

        this.paper.on('element:pointerdblclick',(elementView) => {
            if (elementView instanceof joint.dia.ElementView) {
                this.interaction.handleElementDoubleClick(elementView as joint.dia.ElementView)
            }
        });
    }

    public labelModified(id: string, value: string) {
        if (this.nodes.has(id)) {
            this.elementFactory.changeNodeLabel(this.nodes.get(id), value)
        } else if (this.edges.has(id)) {
            this.elementFactory.changeEdgeLabel(this.edges.get(id), value)
        }
        // TODO else notification
    }

    public elementDeleted(id: string) {

        if (this.nodes.has(id)) {
            this.nodes.get(id).remove()
        } else if (this.edges.has(id)) {
           this.edges.get(id).remove()
        }
        // TODO else modification
    }

    public compartmentAdded(id: string, key: string, value: string) {
        let node : joint.dia.Element = this.nodes.get(id)
        this.elementFactory.addCompartment(node, id, key, value)
    }

    public nodeCreated(n: Node): void {
        let result : joint.dia.Element = this.elementFactory.createNode(n.id, n.type, n.xpos, n.ypos, n.style, n.label)
        this.nodes.set(n.id, result)
    }

    public edgeCreated(e: Edge) {
        let source : joint.dia.Element = this.nodes.get(e.src)
        let target : joint.dia.Element = this.nodes.get(e.trg)
        let result : joint.dia.Link = this.elementFactory.createEdge(e.id, e.type, source, target, e.style, e.label) // TODO src- and target label plus waypoints are missing
        this.edges.set(e.id, result)
    }

    public destroy() {
        this.graph.destroy()
        this.graph = null
        this.paper = null
        this.styleManager = null
        this.editorCallback = null
        this.interaction = null
        this.elementFactory = null
        this.palette = null
        this.container.removeChild(document.getElementById(CANVAS_CONTAINER_ELEMENT_ID))
        this.container.removeChild(document.getElementById(TEMPORARY_ELEMENTS_CONTAINER_ELEMENT_ID))
        this.container.removeChild(document.getElementById(PALETTE_ELEMENTS_CONTAINER_ELEMENT_ID))
        this.container.removeChild(document.getElementById(ADDITIONAL_CONTROL_ELEMENTS_CONTAINER_ELEMENT_ID))
    }

    public triggerLabelEditorForLink(link: joint.dia.Link) {
        this.interaction.handleLinkLabelEdit(link)
    }

}

export function initializeEditor(containerElement: HTMLElement, editorInstanceName: string, editorSocketUrl: string): DiagramEditor {

    if (editorSocketUrl) {
        // creates an online (live) editor that communicates with the backend over a web socket
        let ws: WebSocket = new WebSocket(editorSocketUrl);
        let callback: ModelEditorEventSink = new WebSocketEditorCallback(ws);
        let styleManager: StyleManager = new StyleManager([],[]); // TODO fill via Web Socket communication
        let editor: DiagramEditor = new DiagramEditor(containerElement,editorInstanceName,styleManager,callback);
        callback.registerHandler(editor);
        return editor;
    } else {
       // creates an offline editor, whose changes are lost when the window is closed, i.e. only for testing purposes   
       let callback: ModelEditorEventSink = new StubEditorCallbak();
       let styleManager: StyleManager = new StyleManager([],[]);
       let editor : DiagramEditor = new DiagramEditor(containerElement,editorInstanceName,styleManager,callback);
       callback.registerHandler(editor);
       return editor;
    }
}







