import * as joint from '../joint-dist/joint'
import { ModelEditorEventSink } from '../api/model-editor-interface';
import { NodeStyle, ArrowStyle } from '../api/model-editor-styles';
import { getUpperLeftCorner, getCanvasRelativeMousePosition, drawLine, moveRect, moveLineTarget, addRect } from '../lib/svglib';
import { createDiv, createTextbox, createButton } from '../lib/htm-utils';


enum Mode {
    CURSOR, /* The default mode, the user can select elements with the cursor */
    NODE_PLACEMENT, /* The user is placing a node, a prototype visualization is following under the cursor and on click the node is placed */
    EDGE_CONNECT_SOURCE_SELECTION, /* The user wants to create an edge, in this mode he first selects an element */
    EDGE_CONNECT_TARGET_SELECTION,
    NODE_SELECTED,
    EDGE_SELECTED,
    OVERLAY_INTERACTION,
    MULTI_SELECT,
    SLECTION_BOX_DRAWING,
    NODE_DRAGGING,
    EDGE_DRAGGING,
    RESIZING
}

abstract class Overlay {
    public xpos: number
    public ypos: number
    protected container: HTMLDivElement
    protected element: HTMLElement

    constructor(container: HTMLDivElement, xpos: number, ypos: number) {
        this.xpos = xpos
        this.ypos = ypos
        this.container = container
    }

    protected abstract createElement(): HTMLElement
    
    public show(): Overlay {
        this.element = this.createElement()
        return this
    }
    public getElement(): HTMLElement {
        return this.element
    }

}


abstract class FormOverlay extends Overlay {
    private width: number
    private height: number
    private caption: string
  
    constructor (
        container: HTMLDivElement, 
        xpos: number, 
        ypos: number, 
        width: number, 
        height: number,
        caption: string) {
        super(container, xpos, ypos)
        this.width = width
        this.height = height
        this.caption = caption
    }

    protected createElement() {
        let element : HTMLDivElement = createDiv(this.container)
        element.style.position = 'absolute';
        element.style.width =  this.width.toString() + 'px';
        element.style.height =  this.height.toString() + 'px';
        element.style.left = this.xpos.toString() + 'px';
        element.style.top = this.ypos.toString() + 'px';

        let captionElemnt : HTMLDivElement = createDiv(element, null, 'overlay-caption')
        captionElemnt.textContent = this.caption;

        this.addTextBoxes(element)

        return element
    }

    protected abstract addTextBoxes(container: HTMLDivElement): void

   
  
}

class SimpleFormOverlay extends FormOverlay {

    private argument: string
    private callback : (argument: string) => void

    constructor (
        container: HTMLDivElement, 
        xpos: number, 
        ypos: number, 
        width: number, 
        height: number,
        caption: string,
        callback: (argument: string) => void,
        argument? : string) {
            super(container,xpos,ypos,width,height,caption)
            this.callback = callback
            this.argument = argument
        }

    protected addTextBoxes(container: HTMLDivElement) {
        let box : HTMLInputElement = createTextbox(container)
        if (this.argument) {
            box.value = this.argument
        }
        box.focus()
        box.addEventListener('keyup', (evt: KeyboardEvent) => {
            evt.preventDefault()
            if (evt.keyCode === ENTER_KEY_CODE) {
                this.callback(box.value)
            }
        })
    }
}

class KeyValueFormOverlay extends FormOverlay {
    private keyCaption : string
    private valueCaption: string
    private key: string
    private value: string
    private callback: (key: string, value: string) => void

    constructor(
        container: HTMLDivElement, 
        xpos: number, 
        ypos: number, 
        width: number, 
        height: number,
        caption: string,
        callback: (key: string, value: string) => void,
        keyCaption?: string,
        valueCaption?: string,
        key?: string,
        value?: string
    ) {
        super(container, xpos, ypos, width, height, caption)
        this.callback = callback
        this.keyCaption = keyCaption
        this.valueCaption = valueCaption
        this.key = key
        this.value = value
    }

    protected addTextBoxes(container: HTMLDivElement) {
        if (this.keyCaption) {
            let lbl : HTMLLabelElement = document.createElement('label')
            lbl.textContent = this.keyCaption
            container.appendChild(lbl)
        } 
        let keyTextbox : HTMLInputElement = createTextbox(container)
        if (this.key) {
            keyTextbox.value = this.key
        }
        if (this.valueCaption) {
            let lbl : HTMLLabelElement = document.createElement('label')
            lbl.textContent = this.keyCaption
            container.appendChild(lbl)
        } 
        let valueTextbox : HTMLInputElement = createTextbox(container)
        if (this.value) {
            valueTextbox.value = this.value
        }
        valueTextbox.addEventListener('keyup', (evt: KeyboardEvent) => {
            evt.preventDefault()
            if (evt.keyCode === ENTER_KEY_CODE) {
                this.callback(keyTextbox.value, valueTextbox.value)
            }
        })
        keyTextbox.focus()
    }


}

class ButtonOverlay extends Overlay {
    private caption: string
    private action: () => void

    constructor(container: HTMLDivElement, xpos: number, ypos: number, action: () => void, caption?: string) {
        super(container, xpos, ypos)
        this.action = action
        this.caption = caption
    }

    protected createElement() {
        let button : HTMLButtonElement = createButton(this.container, this.action, this.caption, null, 'add-compartments')
        button.style.position = 'absolute';
        button.style.left = this.xpos.toString() + 'px';
        button.style.top = this.ypos.toString() + 'px';
        button.classList.add("button-overlay")
        button.classList.add("add-compartment")
        return button
    }
}


const ESCAPE_KEY_CODE : number = 27
const ENTER_KEY_CODE : number = 13

/**
 * The InteractionManager manages basically all user interaction in the diagram editor.
 * Depending on the current mode it reacts on various events differently.
 */
export class InteractionManager {
    private callback : ModelEditorEventSink

    private currentMode: Mode = Mode.CURSOR
    private currentOverlay: Overlay
    private tempPane : HTMLDivElement
    private canvas : SVGGraphicsElement
    private cursorX: number
    private cursorY: number
    private decorator: SVGElement
    private currentStyle : NodeStyle | ArrowStyle
    private currentSelection : Array<joint.dia.Element>

    constructor(tempPane: HTMLDivElement, callback : ModelEditorEventSink) {
        this.currentSelection = []
        this.tempPane = tempPane
        this.callback = callback
        this.canvas = document.querySelector("#diaged-canvas-container svg");
        this.cursorX = 0
        this.cursorY = 0
        this.canvas.addEventListener('mousemove', (evt) => this.mouseMovedHandler(evt), false)
        window.addEventListener('keyup', (evt) => {
            if (evt.keyCode === ESCAPE_KEY_CODE) {
                this.abort()
            }
        }, false)
       // this.canvas.addEventListener('click', (evt) => {
        //    if (this.currentMode === Mode.NODE_PLACEMENT) {
         //       let cord: [number, number] = getUpperLeftCorner(this.decorator)
         //       this.canvas.removeChild(this.decorator)
         //       this.decorator = null
        //        this.currentMode = Mode.CURSOR
        //        this.nodePlaced(cord[0], cord[1],this.currentStyle as NodeStyle)
         //   } 
       // })
    }

    public canvasClicked() {
        if (this.currentMode === Mode.NODE_PLACEMENT) {
            let cord: [number, number] = getUpperLeftCorner(this.decorator)
            this.canvas.removeChild(this.decorator)
            this.decorator = null
            this.currentMode = Mode.CURSOR
            this.nodePlaced(cord[0], cord[1],this.currentStyle as NodeStyle)
        } else if (this.currentMode === Mode.NODE_SELECTED) {
            this.abort();
        }
    }

    public nodePlaced(x: number, y: number, style: NodeStyle) {
        this.callback.createNode(x, y, style.name)
    }

    public mouseMovedHandler(mouse: MouseEvent) {
        this.cursorX = mouse.clientX
        this.cursorY = mouse.clientY
        if (this.decorator !== null) {
            if (this.decorator instanceof SVGRectElement) {
                moveRect(this.decorator as SVGRectElement,  ( this.cursorX - this.decorator.width.baseVal.value / 2).toString(), ((this.cursorY - this.decorator.height.baseVal.value / 2)-80).toString()) // TODO do something about canvas offsets
            } else if (this.decorator instanceof SVGLineElement) {
                moveLineTarget(this.decorator as SVGLineElement, (this.cursorX - 1).toString(), (this.cursorY -1 ).toString())
            }
        }
    }

    public handleElementDoubleClick(element: joint.dia.ElementView) {
        let pos : joint.g.Point = element.getBBox().topLeft()
        let elemId : string = element.model.get('meta')['id']
        let labelText : string = element.model.get('meta')['label']
        if (!labelText) {
            labelText = ''
        }
        this.currentOverlay = new SimpleFormOverlay(this.tempPane, pos.x, pos.y, element.getBBox().width, element.getBBox().height, "Node Name", (label: string) => {
            this.callback.changeNodeLabel(elemId, label)
            this.abort()
        }, labelText).show()
        
    
        this.currentMode = Mode.OVERLAY_INTERACTION
    }

    private clearOverlays() {
        if (this.currentOverlay) {
            this.tempPane.removeChild(this.currentOverlay.getElement())
            this.currentOverlay = null
        }
    }

    public handleElementClick(element: joint.dia.ElementView) {
        // prior cleanup
        this.clearOverlays();
        if (this.currentMode === Mode.NODE_SELECTED && this.currentSelection.length > 0 ) {
            this.undecorateAsSelected(this.currentSelection[0])
            this.currentSelection.pop()
        }

        if (this.currentMode === Mode.CURSOR) {
            this.currentSelection.push(element.model)
            this.decorateAsSelected(element.model)
            this.currentMode = Mode.NODE_SELECTED
        } else if (this.currentMode === Mode.EDGE_CONNECT_SOURCE_SELECTION) {
            this.currentSelection.push(element.model)

            let src : joint.g.Point = element.model.getBBox().center()
            let trg : [number, number] = getCanvasRelativeMousePosition(this.canvas, this.cursorX, this.cursorY)
            this.decorator = drawLine(this.canvas, src.x.toString(), src.y.toString(), trg[0].toString(), trg[1].toString(), "black") // TODO we should draw the line such that it fits with the selected style...

            this.currentMode = Mode.EDGE_CONNECT_TARGET_SELECTION
        } else if (this.currentMode === Mode.EDGE_CONNECT_TARGET_SELECTION) {
            this.currentSelection.push(element.model)

            if (this.currentSelection.length == 2) {
                this.callback.createEdge(this.currentSelection[0].get('meta')['id'], this.currentSelection[1].get('meta')['id'], this.currentStyle.name)
            }
            this.abort()
        } 
    }

    private handleAddCompartmentClick(id: string) {
        let xpos : number = this.currentOverlay.xpos
        let ypos : number = this.currentOverlay.ypos
        this.tempPane.removeChild(this.currentOverlay.getElement())
        this.currentOverlay = null
        this.currentOverlay = new KeyValueFormOverlay(this.tempPane, xpos, ypos, 100, 50, 'Add Member', (key: string, value: string) => {
            this.callback.addCompartment(id, key, value)
            this.abort()
        }, 'key', 'value').show()
        this.currentMode = Mode.OVERLAY_INTERACTION
    }

   

    private decorateAsSelected(element : joint.dia.Element) {
       // element.attr('body/stroke', 'orange') // TODO would be better if this is controlled via a CSS class
        // TODO show tools
        let classList : SVGRectElement = element.attr('body/class')
        element.attr('body/class', classList + ' diaged-selected-element')


        let compartments : number = element.get('meta')['parts']
        if (compartments && compartments > 0) {
            let elemId : string =  element.get('meta')['id']
            // TODO handle as overlay
            let ctr : joint.g.Point = element.getBBox().center()
            let br : joint.g.Point = element.getBBox().bottomMiddle()
            let xpos : number = br.x - 11
            let ypos : number = br.y - 18
            this.currentOverlay = new ButtonOverlay(this.tempPane, xpos, ypos, () => {this.handleAddCompartmentClick(elemId)}, '+').show()
        
        }
    }

    

    private undecorateAsSelected(element : joint.dia.Element) {
       // element.attr('body/stroke', 'black')
       element.attr('body/class', 'diaged-node')
    }

    public handleLinkLabelEdit(link: joint.dia.Link) {
        let pos : joint.g.Point = link.getBBox().leftMiddle()
        let edgeid : string = link.get('meta')['id']
        this.currentOverlay = new SimpleFormOverlay(this.tempPane, pos.x + 10, pos.y, 100, 40, 'Edge Label', (value: string) => {
            this.callback.changeEdgeLabel(edgeid, value)
            this.abort()
        }, link.get('meta')['label']).show()
        this.currentMode = Mode.OVERLAY_INTERACTION
    }

    public handleSelectionToolClick() {
        this.abort()
    }

    public handleNodeToolSelected(style: NodeStyle) {
        this.currentStyle = style
        this.currentMode = Mode.NODE_PLACEMENT
        let mouseLocation : [number, number] = getCanvasRelativeMousePosition(this.canvas, this.cursorX, this.cursorY)
        this.decorator = addRect(this.canvas, mouseLocation[0].toString(), mouseLocation[1].toString(), "75", "50", "white", "black") // TODO actually style according to the current style.
    }

    public handleArrowToolSelected(style: ArrowStyle) {
        this.currentStyle = style
        this.currentMode = Mode.EDGE_CONNECT_SOURCE_SELECTION
        this.canvas.parentElement.classList.add('draw-line')

    }

    public abort() {
        this.canvas.parentElement.classList.remove('draw-line')
        if (this.decorator) {
            this.canvas.removeChild(this.decorator)
            this.decorator = null
        }
        if (this.currentOverlay) {
            this.tempPane.removeChild(this.currentOverlay.getElement())
            this.currentOverlay = null
        }
        
        this.currentSelection.forEach(element => this.undecorateAsSelected(element))
        this.currentSelection = []
        this.currentMode = Mode.CURSOR;
    }

}



