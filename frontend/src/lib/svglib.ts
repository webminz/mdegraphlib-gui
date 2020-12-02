
/**
 * 
 * Draws an SVG rectangle inside the given SVG canvas.
 * 
 * @param {*} container An svg-element that represents the canvas.
 * @param {*} x The x coordinate on the canvas.
 * @param {*} y The y coordinate on the canvas.
 * @param {*} width The width of the rectangle.
 * @param {*} height The height of the rectangle.
 * @param {*} fill The color to fill the rectangle. Either a color name or a #RGB code.
 * @param {*} stroke The color of the border of the rectangle. Either a color name or a #RGB code.
 */
export function addRect(container : SVGGraphicsElement, x : string, y : string, width : string, height : string, fill : string, stroke : string): SVGRectElement {
    let newElement : SVGRectElement = document.createElementNS("http://www.w3.org/2000/svg", 'rect');
    newElement.setAttribute("x",x);
    newElement.setAttribute("y",y);
    newElement.setAttribute("width",width);
    newElement.setAttribute("height",height);
    newElement.setAttribute("fill",fill);
    newElement.setAttribute("stroke",stroke);
    container.appendChild(newElement);
    return newElement;
}

/**
 * Moves an svg shape, i.e. an element that has x and y coordinates, e.g. rectangles, circles, texts etc.
 * 
 * @param {*} shape The shape to move.
 * @param {*} newX The new x coordinate.
 * @param {*} newY  The new y coordinate.
 */
export function moveRect(shape: SVGRectElement, newX: string, newY: string) {
    shape.setAttributeNS(null, "x", newX);
    shape.setAttributeNS(null, "y", newY);
}

/**
 * Adds an svg line element to the canvas.
 * 
 * @param {*} container The svg element representing the canvas.
 * @param {*} fromX The x coordinate of the source or starting point of the line.
 * @param {*} fromY The y coordinate of the source or starting point of the line.
 * @param {*} toX The x coordinate of the target or ending point of the line.
 * @param {*} toY The y coordinate of the target or ending point of the line.
 * @param {*} stroke The color of the line. Either a color name or a #RGB code.
 */
export function drawLine(container : SVGGraphicsElement, fromX: string, fromY: string, toX: string, toY: string, stroke: string): SVGLineElement {
    let newElement : SVGLineElement = document.createElementNS("http://www.w3.org/2000/svg", 'line');
    newElement.setAttribute("x1",fromX);
    newElement.setAttribute("y1", fromY);
    newElement.setAttribute("x2",toX);
    newElement.setAttribute("y2",toY);
    newElement.setAttribute("stroke", stroke);
    container.appendChild(newElement);
    return newElement;
}

/**
 * Moves a the target or ending point of a line to the given coordinates.
 * @param {*} shape The svg line to move.
 * @param {*} newX The x coordinate of the new target.
 * @param {*} newY  The y coordinate of the new target.
 */
export function moveLineTarget(shape: SVGLineElement, newX: string, newY: string): void {
    shape.setAttributeNS(null, "x2", newX);
    shape.setAttributeNS(null, "y2", newY);
}

/**
 * Translates the absolute mouse X and Y position from the screen into
 * a SVG canvas relative position.
 * @param canvas The svg element.
 * @param mouseX The absolute mouse X position on the screen.
 * @param mouseY The absolute mouse Y position on the screen.
 */
export function getCanvasRelativeMousePosition(canvas : SVGGraphicsElement, mouseX: number, mouseY: number): [number, number] {
    var CTM = canvas.getScreenCTM();
    return [
         (mouseX - CTM.e) / CTM.a,
         (mouseY - CTM.f) / CTM.d
    ];
}


export function getUpperLeftCorner(shape: SVGElement): [number, number] {
    if (shape instanceof SVGRect) {
        return [shape.x, shape.y]
    }
    return [
        parseInt(shape.getAttributeNS(null, "x")),
        parseInt(shape.getAttributeNS(null, "y"))];
}