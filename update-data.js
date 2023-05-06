const fs = require('fs');
const crypto = require('crypto');
const data = require('./data/map.json');
const {createReadStream, createWriteStream} = require("fs");
const {createGzip} = require("zlib");

let changed = false
for (const world in data) {
    for (const island in data[world]) {
        for (const height in data[world][island].images) {
            for (const quality in data[world][island].images[height]) {
                if (typeof data[world][island].images[height][quality] !== 'object') continue;
                const url = data[world][island].images[height][quality].url.split('/');
                const file = `./data/${url[url.length - 2]}/${url[url.length - 1]}`;
                const checksum = getChecksum(file);
                if (checksum !== data[world][island].images[height][quality].sha256) {
                    data[world][island].images[height][quality].sha256 = checksum
                    changed = true
                }
            }
        }
        // Remove duplicate npcs
        data[world][island].npcs = data[world][island].npcs.filter((value, index, self) => {
            const result = index === self.findIndex((t) => (
                t.name === value.name && t.x === value.x && t.y === value.y && t.z === value.z
            ))
            if (!result) {
                console.log(`Removed duplicate npc "${value.name}"`)
                changed = true
            }
            return result
        })
    }
}

if (changed) fs.writeFileSync('./data/map.json', JSON.stringify(data, null, 2));
fs.writeFileSync('./data/map.json.sha256', getChecksum('./data/map.json'))
compressFile('./data/map.json')

function getChecksum(file) {
    const hash = crypto.createHash('sha256');
    const data = fs.readFileSync(file);
    hash.update(data);
    return hash.digest('hex');
}

function compressFile(filePath) {
    const stream = createReadStream(filePath);
    stream
        .pipe(createGzip())
        .pipe(createWriteStream(`${filePath}.gz`));
}