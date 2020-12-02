import * as joint from '../joint-dist/joint'
import { StyleManager, NodeStyle, Shape, ArrowStyle } from '../api/model-editor-styles';
import { normalizeAngle } from '../joint-dist/geometry';

export class ElementFactory {
    private styleManager: StyleManager
    private linkLabelEditorCallback : Function
    private graph: joint.dia.Graph
    private paper: joint.dia.Paper
    private nodeConstructors: Map<string, joint.dia.Cell.Constructor<joint.dia.Element>>
 
    constructor(graph: joint.dia.Graph, paper: joint.dia.Paper, styleManager: StyleManager, linkLabelEditorCallback: (link: joint.dia.Link) => void) {
        this.styleManager = styleManager
        this.linkLabelEditorCallback = linkLabelEditorCallback
        this.graph = graph
        this.paper = paper
        this.nodeConstructors = new Map()
        this.populateNodeConstructors(styleManager)
    }

    private populateNodeConstructors(styleManager: StyleManager): void {
        this.styleManager.getAllNodeStyles().forEach((style: NodeStyle) => {
            if (style.compartments == 1) {
                this.nodeConstructors.set(style.name, joint.shapes.standard.HeaderedRectangle.define(
                    style.name,
                    { 
                        attrs: {
                            body: {
                                class : 'diaged-node',
                                refWidth: '100%',
                                refHeight: '100%',
                                strokeWidth: 2,
                                stroke: '#000000',
                                fill: '#FFFFFF'
                            },
                            header: {
                                refWidth: '100%',
                                height: 30,
                                strokeWidth: 2,
                                stroke: '#000000',
                                fill: '#FFFFFF'
                            },
                            headerText: {
                                textVerticalAnchor: 'middle',
                                textAnchor: 'middle',
                                refX: '50%',
                                refY: 15,
                                fontSize: 16,
                                fill: '#333333'
                            },
                            contents1: {
                                refX: '0',
                                refY: '30',
                                refWidth: '90%',
                                class : 'diaged-node-contents',
                                height : '25'
                            }
                        }
                    },
                    {
                        markup : [
                            {
                                tagName : 'rect',
                                selector : 'body'
                            },
                            {
                                tagName : 'rect',
                                selector : 'header'
                            },
                            {
                                tagName : 'text',
                                selector : 'headerText'
                            },
                            {
                                tagName : 'foreignObject',
                                selector : 'contents1' 
                            }
                        ]
                    }))
            } else if (style.compartments === 0 && style.shape === Shape.RECTANGLE) {
                this.nodeConstructors.set(style.name, joint.shapes.standard.Rectangle.define(
                    style.name, 
                    {
                        attrs: {
                            body: {
                                class : 'diaged-node',
                                refWidth: '100%',
                                refHeight: '100%',
                                strokeWidth: 2,
                                stroke: '#000000',
                                fill: '#FFFFFF'
                            },
                            label: {
                                textVerticalAnchor: 'middle',
                                textAnchor: 'middle',
                                refX: '50%',
                                refY: '50%',
                                fontSize: 14,
                                fill: '#333333'
                            }
                        }
                    }, 
                    {
                        markup: [{
                            tagName: 'rect',
                            selector: 'body',
                        }, {
                            tagName: 'text',
                            selector: 'label'
                        }]
                    }))
            } 
        })
    }

    public createNode(
            id: string, 
            type: string, 
            x: number, 
            y: number, 
            style: string, 
            label?: string): joint.dia.Element {

        let styleObject: NodeStyle = this.styleManager.getNodeStyle(style)
        let constructor : joint.dia.Cell.Constructor<joint.dia.Element> =  this.nodeConstructors.get(style)
        let nodeObject : joint.dia.Element = new constructor

        // create meta info companion object
        let metaInfoObject: Object = {
            id : id,
            type: type,
            label : label,
            parts : styleObject.compartments
        }
        nodeObject.set('meta', metaInfoObject)

        // update label
        if (label) {
            this.changeNodeLabel(nodeObject, label)
        }

    
        // resize
        nodeObject.resize(styleObject.width.value, styleObject.height.value)

        // position
        nodeObject.position(x, y)

        // Add to the graph
        nodeObject.addTo(this.graph)

        // add compartment specific meta information
        for (let i = 1; i <= styleObject.compartments; i++) {
            let comprtmentIdentification : string = 'nodecontents' + i + 'for' + id
            nodeObject.attr('contents' +  i + '/id', comprtmentIdentification)
            let comprt = document.getElementById(comprtmentIdentification)
            let table : HTMLTableElement = document.createElement('table')
            table.id = comprtmentIdentification + 'table'
            comprt.appendChild(table)
        }

        
        return nodeObject
    }

    public addCompartment(node: joint.dia.Element, id: string, key: string, value: string):void {
        const COMPARTMENT_HEIGHT_OFFSET = 18;
        const COMPARTMENT_WIDTH_OFFSET = 15;
        const COMPARTMENT_WIDTH_FACTOR = 9;

        let newHeight : number = node.getBBox().height + COMPARTMENT_HEIGHT_OFFSET
        let neededWith : number = (key.length + value.length) * COMPARTMENT_WIDTH_FACTOR + COMPARTMENT_WIDTH_OFFSET;
        let newWith = node.getBBox().width < neededWith ? neededWith :node.getBBox().width;
        node.resize(newWith, newHeight)
       
        node.attr('contents1/width', newWith);
        node.attr('contents1/height', newHeight - 30);
       
        let table : HTMLElement = document.getElementById('nodecontents1for' + id + 'table') // TODO support more compartments
        let row : HTMLTableRowElement = document.createElement('tr')
        let keyCell : HTMLTableDataCellElement = document.createElement('td')
        keyCell.textContent = key
        let separatorCell : HTMLTableDataCellElement = document.createElement('td')
        separatorCell.textContent = ':'
        let valueCell : HTMLTableDataCellElement = document.createElement('td')
        valueCell.textContent = value
        row.appendChild(keyCell)
        row.appendChild(separatorCell)
        row.appendChild(valueCell)
        table.appendChild(row)
    }


    public createEdge(id: string, type: string, source: joint.dia.Element, target: joint.dia.Element, style: string, label?: string): joint.dia.Link {
        let styleObject: ArrowStyle = this.styleManager.getEdgeStyle(style)
        let edgeObject: joint.dia.Link = this.provideJointJSLink(styleObject)
        if (source === target) {
            this.makeEndoLink(edgeObject, source)
        } else {
            edgeObject.source(source)
            edgeObject.target(target)
        }
        let metaInfoObject: Object = {
            id : id,
            type: type,
        }
        edgeObject.set('meta', metaInfoObject)
        edgeObject.addTo(this.graph)
        if (label) {
            edgeObject.appendLabel({
                attrs: {
                    text: {
                        text: label
                    }
                }
            });
        }
        let edgeView : joint.dia.CellView = edgeObject.findView(this.paper)
        edgeView.addTools(this.createLinkTools(edgeObject, this.linkLabelEditorCallback))
        edgeView.hideTools()
        return edgeObject
    }

    private makeEndoLink(edge: joint.dia.Link, node: joint.dia.Element) {
        edge.source(node, {
            anchor: {
                name: 'left'
            }
        });
        edge.target(node, {
            anchor: {
                name: 'top'
            }
        })
        var bbox = node.getBBox();
        var topLeft = bbox.topLeft();
        var width = bbox.width / 2;
        var height = bbox.height / 2;
        edge.vertices(
            [    
                 new joint.g.Point(topLeft.x - width,topLeft.y + height),
                 new joint.g.Point(topLeft.x - width,topLeft.y - height),
                 new joint.g.Point(topLeft.x + width,topLeft.y - height)
            ]
        );
        
    }

    public changeNodeLabel(element: joint.dia.Element, value: string) {
        let metaOb : any = element.get('meta')
        metaOb['label'] = value
        element.attr('label/text', value)
        if (element.attr('headerText')) {
            element.attr('headerText/text', value)
        } else {
            element.attr('label/text', value)
        }
    }

    public changeEdgeLabel(link: joint.dia.Link, value: string) {
        let metObj : any = link.get('meta')
        metObj['label'] = value;
        link.removeLabel(-1);
        link.appendLabel({
            attrs: {
                text: {
                    text: value
                }
            }
        });
    }


    private provideJointJSLink(style: ArrowStyle): joint.dia.Link {
        var result : joint.dia.Link = new joint.shapes.standard.Link();
        // TODO further styling
        return result
    }


    private createLinkTools(link: joint.dia.Link, linkLabelEditorCallback: Function): joint.dia.ToolsView {
        let verticesTool : joint.linkTools.Vertices = new joint.linkTools.Vertices();
        let segmentsTool : joint.linkTools.Segments = new joint.linkTools.Segments();
        let sourceArrowheadTool : joint.linkTools.SourceArrowhead = new joint.linkTools.SourceArrowhead();
        let targetArrowheadTool : joint.linkTools.TargetArrowhead = new joint.linkTools.TargetArrowhead();
        let sourceAnchorTool : joint.linkTools.SourceAnchor = new joint.linkTools.SourceAnchor();
        let targetAnchorTool : joint.linkTools.TargetAnchor = new joint.linkTools.TargetAnchor();
        let boundaryTool : joint.linkTools.Boundary = new joint.linkTools.Boundary();
//        var removeButton = new joint.linkTools.Remove();


        let EditButton = joint.linkTools.Button.extend({
            name: 'edit-button',
            options: {
                markup: [{
                tagName: 'circle',
                selector: 'button',
                attributes: {
                    'r': 8,
                    'fill': '#FFFFFF',
                    'cursor': 'pointer'
                }
            }, {
            tagName: 'g',
            selector: 'icon',
            attributes: {
                'transform': 'matrix(0.01890721,0,0,0.0189998,-4.3573218,-4.1752943)',
            },
            children: [
                {
                    tagName: 'path',
                    attributes: {
                        d: 'M 328.883,89.125 436.473,196.714 164.133,469.054 56.604,361.465 Z M 518.113,63.177 470.132,15.196 c -18.543,-18.543 -48.653,-18.543 -67.259,0 l -45.961,45.961 107.59,107.59 53.611,-53.611 c 14.382,-14.383 14.382,-37.577 0,-51.959 z M 0.3,512.69 c -1.958,8.812 5.998,16.708 14.811,14.565 L 135.002,498.186 27.473,390.597 Z',
                        stroke: 'black',
                        fill: 'black'
                    }
                }
            ]
            }],
                distance: 60,
                offset: 0,
                action: function(evt: any) {
                    linkLabelEditorCallback(link)
                }
            }
        });

        let infoButton = new EditButton();

        let toolsView : joint.dia.ToolsView = new joint.dia.ToolsView({
            tools: [
                verticesTool, segmentsTool,
                sourceArrowheadTool, targetArrowheadTool,
                sourceAnchorTool, targetAnchorTool,
                boundaryTool, infoButton
            ]
        });
        return toolsView;
    }

}


