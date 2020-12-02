import { InteractionManager } from "./interaction-manager";

/**
 * Provides some special controls, i.e. zooming, togggle Visibility of things
 */
export class ExtraControls {
    private element : HTMLDivElement
    private interaction: InteractionManager

    constructor (element: HTMLDivElement, interaction: InteractionManager) {
        this.element = element
        this.interaction = interaction
    }

}