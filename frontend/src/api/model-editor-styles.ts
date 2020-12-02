
export enum Shape {
    RECTANGLE,
    ELLIPSIS,
    DIAMOND,
    CUBE,
    DATABASE,
    CLOUD,
    TRIANGLE,
    PENTAGON,
    IMAGE
} 

export enum Line {
    NONE,
    SOLID,
    DOTTED,
    DASHED,
    DOTTED_DASHED,
    DOUBLE
}

export enum Tip {
    NONE,
    FILLED_ARROW,
    OPEN_ARROW,
    FILLED_TRIANGLE,
    OPEN_TRIANGLE,
    ANGLE,
    CRAWFOOT,
    CROSS,
    FILLED_DIAMOND,
    OPEN_DIAMOND,
    FILLED_CIRCLE,
    OPEN_CIRCLE,
    BAR,
    FILLED_SQUARE,
    OPEN_SQUARE
}

export enum Location {
    INSIDE,
    ABOVE,
    BELOW
}

export interface Size {
    value: number
    unit: string
}

export interface Color {
    red: number
    green: number
    blue: number
    alpha: number
}

export const COLOR_WHITE : Color  = {
    red: 0,
    green: 0,
    blue: 0,
    alpha: 1
}

export const COLOR_BLACK : Color = {
    red: 255,
    green: 255,
    blue: 255,
    alpha: 1
}

export enum FontFace {
    REGULAR,
    BOLD,
    ITALIC
}

export enum FontDecoration {
    NONE,
    UNDERLINE,
    OVERLINE,
    STRIKETHROUGH
}


export interface NodeStyle {
    name: string
    width: Size
    height: Size
    shape: Shape
    compartments: number
    textColor: Color
    labelPosition: Location
    labelMargin: Size
    fontFamily: string
    fontSize: Size
    fontFace: FontFace
    fontDecoration: FontDecoration
    backgrondColor: Color
    borderColor: Color
    borderStyle: Line
    borderThickness: Size

}

export const DEFAULT_NODE_STLE : NodeStyle = {
    name: "default",
    width: { value: 75, unit: "px"},
    height: { value: 50, unit: "px"},
    shape: Shape.RECTANGLE,
    compartments: 1,
    textColor: COLOR_WHITE,
    labelPosition: Location.INSIDE,
    labelMargin: {value: 5, unit: "px"},
    fontFamily: "Arial, Helvetica, sans-serif",
    fontSize: { value: 12, unit: "pt"},
    fontFace: FontFace.REGULAR,
    fontDecoration: FontDecoration.NONE,
    backgrondColor: COLOR_WHITE,
    borderColor: COLOR_BLACK,
    borderStyle: Line.SOLID,
    borderThickness: {value: 1, unit: "1px"}
}

export interface ArrowStyle {
    name: string
    sourceTipStyle: Tip
    sourceTipLineColor: Color
    sourceTipFillColor: Color
    targetTipStyle: Tip
    targetTipLineColor: Color
    targetTipFillColor: Color
    lineType: Line
    lineWidth: Size
    linecolor: Color
    labelPosition: Location
    labelMargin: Size
    labelFontFamily: string
    labelFontSize: Size
    labelFontFace: FontFace
    labelFontDecoration: FontDecoration
}

export const DEFAULT_ARROW_STYLE : ArrowStyle = {
    name: "default",
    sourceTipStyle: Tip.NONE,
    sourceTipLineColor: COLOR_BLACK,
    sourceTipFillColor: COLOR_BLACK,
    targetTipStyle: Tip.FILLED_ARROW,
    targetTipFillColor: COLOR_BLACK,
    targetTipLineColor: COLOR_BLACK,
    lineType: Line.SOLID,
    lineWidth: {value: 1, unit: "px"},
    linecolor: COLOR_BLACK,
    labelPosition: Location.INSIDE,
    labelMargin: {value: 5, unit: "px"},
    labelFontFamily: "Arial, Helvetica, sans-serif",
    labelFontSize: {value: 12, unit: "pt"},
    labelFontFace: FontFace.REGULAR,
    labelFontDecoration: FontDecoration.NONE
}

export class StyleManager {
    private nodes: Map<string, NodeStyle>
    private edges: Map<string, ArrowStyle>
    constructor(nodeStyles: Array<NodeStyle>, edgeStyles: Array<ArrowStyle>) {
        this.nodes = new Map()
        this.edges = new Map()
        if (!nodeStyles || nodeStyles.length <= 0) {
            this.nodes.set("default", DEFAULT_NODE_STLE)
        } else {
            nodeStyles.forEach(style => this.nodes.set(style.name, style))
        }
        if (!edgeStyles || edgeStyles.length <= 0) {
            this.edges.set("default", DEFAULT_ARROW_STYLE)
        } else {
            edgeStyles.forEach(style => this.edges.set(style.name, style))
        }
    }
    public getNodeStyle(name: string): NodeStyle {
        return this.nodes.get(name)
    }
    public getEdgeStyle(name: string): ArrowStyle {
        return this.edges.get(name)
    }
    public getAllNodeStyles(): Array<NodeStyle> {
        let result : Array<NodeStyle> = []
        if (this.nodes.get('default')) {
            result.push(this.nodes.get('default'))
        }
        this.nodes.forEach((value, key) => {if (key !== 'default') {result.push(value)} }) 
        return result
    }
    public getAllEdgeStyles(): Array<ArrowStyle> {
        let result : Array<ArrowStyle> = []
        if (this.edges.get('default')) {
            result.push(this.edges.get('default'))
        }
        this.edges.forEach((value, key) => {if (key !== 'default') {result.push(value)} }) 
        return result
    }

}