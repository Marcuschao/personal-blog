import { readFile, writeFile } from 'node:fs/promises';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import sharp from 'sharp';

const root = join(dirname(fileURLToPath(import.meta.url)), '..', 'public');
const source = join(root, 'icon-source.svg');

const sizes = [
  { name: 'apple-touch-icon.png', size: 180 },
  { name: 'icon-192.png', size: 192 },
  { name: 'icon-512.png', size: 512 },
];

const svg = await readFile(source);
for (const { name, size } of sizes) {
  const buf = await sharp(svg, { density: 288 }).resize(size, size).png().toBuffer();
  await writeFile(join(root, name), buf);
  console.log(`wrote ${name} (${size}x${size})`);
}
