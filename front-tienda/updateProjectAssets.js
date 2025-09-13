import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const srcLogo = path.join(__dirname, 'src/assets/imgs/logoTienda.png');
const destLogo = path.join(__dirname, 'public', 'logoTienda.png');
const indexHtml = path.join(__dirname, 'public', 'index.html');

fs.copyFileSync(srcLogo, destLogo);

let htmlContent = fs.readFileSync(indexHtml, 'utf-8');
htmlContent = htmlContent.replace(/<title>.*<\/title>/, '<title>TempuroShop</title>');
htmlContent = htmlContent.replace(
  /<link rel="icon" href=".*">/,
  '<link rel="icon" href="%PUBLIC_URL%/logoTienda.png">'
);

fs.writeFileSync(indexHtml, htmlContent, 'utf-8');

console.log('Logo y título actualizados correctamente.');
