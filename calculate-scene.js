run()

function run() {
    if (process.argv.length < 6) {
        console.log('correct usage: calculate-scene.js topX topY bottomX bottomY')
        return
    }
    const args = process.argv.splice(2);
    const topX = Number.parseFloat(args[0])
    const topY = Number.parseFloat(args[1])
    const bottomX = Number.parseFloat(args[2])
    const bottomY = Number.parseFloat(args[3])
    console.log(`Camera position:  X: ${(topX + bottomX) / 2} Z: ${(topY + bottomY) / 2}`)
    console.log(`Field of view: ${Math.abs(topX - bottomX)}`)
    console.log(`Low resolution: ${Math.abs(topX - bottomX)}x${Math.abs(topY - bottomY)}`)
    console.log(`Medium resolution: ${Math.abs(topX - bottomX) * 2}x${Math.abs(topY - bottomY) * 2}`)
    console.log(`High resolution: ${Math.abs(topX - bottomX) * 4}x${Math.abs(topY - bottomY) * 4}`)
}