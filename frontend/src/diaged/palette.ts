import { InteractionManager } from './interaction-manager';
import { StyleManager, NodeStyle, Shape, ArrowStyle } from '../api/model-editor-styles';
import { createDiv } from '../lib/htm-utils';


export class Palette {
    private interactionManager: InteractionManager
    private styleManager : StyleManager
    private element: HTMLDivElement 
    constructor(element: HTMLDivElement, interactionManager: InteractionManager, styleManager : StyleManager) {
        this.element = element
        this.interactionManager = interactionManager
        this.styleManager = styleManager
        this.createCursorTool()
        this.createNodeTools()
        this.createArrowTools()
    }

    private createCursorTool(): void {
        let button : HTMLDivElement = createDiv(this.element, 'diaged-cursor-tool', 'palette-button')
        let icon : HTMLElement = document.createElement('i')
        icon.classList.add('fas')
        icon.classList.add('fa-mouse-pointer')
        button.appendChild(icon)
        button.addEventListener('click', (evt) => {
            evt.preventDefault()
            this.interactionManager.handleSelectionToolClick()
        }, false)
        let divider : HTMLHRElement = document.createElement('hr')
        divider.classList.add('separator')
        this.element.appendChild(divider)
    }

    private createNodeTools(): void {
        this.styleManager.getAllNodeStyles().forEach((style) => {
            let button: HTMLDivElement = createDiv(this.element, ('diaged-node-tool-' + style.name), 'palette-button')
            button.appendChild(this.createIconForNodeStyle(style))
            button.addEventListener('click', (evt) => {
                evt.preventDefault()
                this.interactionManager.handleNodeToolSelected(style)
            }, false)
        })
        let divider : HTMLHRElement = document.createElement('hr')
        divider.classList.add('separator')
        this.element.appendChild(divider)
    }

    private createIconForNodeStyle(style: NodeStyle): HTMLElement {
        let result : HTMLElement = document.createElement('i')
        result.classList.add('far')
        if (style.shape === Shape.RECTANGLE) {
            result.classList.add('fa-square')
        } else if (style.shape === Shape.ELLIPSIS) {
            result.classList.add('fa-circle')
        }
        // TODO images for other shapes and colors
        return result
    }

    private createArrowTools(): void {
        this.styleManager.getAllEdgeStyles().forEach((style) => {
            let button: HTMLDivElement = createDiv(this.element, ('diaged-edge-tool-' + style.name), 'palette-button')
            button.appendChild(this.createIconForArrowStyle(style))
            button.addEventListener('click', (evt) => {
                evt.preventDefault()
                this.interactionManager.handleArrowToolSelected(style)
            }, false)
        })
    }

    private createIconForArrowStyle(style: ArrowStyle): HTMLElement {
        let result : HTMLElement = document.createElement('i');
        result.classList.add('fas');
        result.classList.add('fa-arrow-right')
        // TODO different arrow styles
        return result
    }
 }








