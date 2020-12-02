
export function createDiv(container: HTMLElement, id?: string, className?: string, children?: Array<HTMLElement>): HTMLDivElement {
    let result : HTMLDivElement = document.createElement('div')
    if (id) {
        result.id = id
    }
    if (className) {
        result.classList.add(className)
    }
    if (children) {
        children.forEach(elem => result.appendChild(elem))
    }
    container.appendChild(result)
    return result
}

export function createTextbox(container: HTMLDivElement, id?: string, className?: string): HTMLInputElement {
    let result = document.createElement('input')
    if (id) {
        result.id = id
    }
    if (className) {
        result.classList.add(className)
    }
    container.appendChild(result)
    return result
}

export function createButton(container: HTMLDivElement, handler: () => void, caption: string, id?: string, className?: string): HTMLButtonElement {
    let result : HTMLButtonElement = document.createElement('button')
    result.textContent = caption
    result.addEventListener('click', (ev) => {
        handler()
    })
    if (id) {
        result.id = id
    }
    if (className) {
        result.classList.add(className)
    }
    container.appendChild(result)
    return result
}