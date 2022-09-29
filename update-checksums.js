const fs = require('fs');
const crypto = require('crypto');
const data = require('./data/map.json');

for (const world in data) {
    for (const island in data[world]) {
        for (const height in data[world][island].images) {
            for (const quality in data[world][island].images[height]) {
                if (typeof data[world][island].images[height][quality] !== 'object') continue;
                const url = data[world][island].images[height][quality].url.split('/');
                const file = `./data/${url[url.length - 2]}/${url[url.length - 1]}`;
                data[world][island].images[height][quality].sha256 = getChecksum(file);
            }
        }
    }
}

fs.writeFileSync('./data/map.json', JSON.stringify(data, null, 2));

function getChecksum(file) {
    const hash = crypto.createHash('sha256');
    const data = fs.readFileSync(file);
    hash.update(data);
    return hash.digest('hex');
}