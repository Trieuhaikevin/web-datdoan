from pathlib import Path
import zlib, struct

base = Path('frontend/src/main/resources/icons')
base.mkdir(parents=True, exist_ok=True)

def write_png(path, width, height, pixels):
    def chunk(type_, data):
        return struct.pack('!I', len(data)) + type_ + data + struct.pack('!I', zlib.crc32(type_ + data) & 0xffffffff)
    png = b'\x89PNG\r\n\x1a\n'
    ihdr = struct.pack('!IIBBBBB', width, height, 8, 6, 0, 0, 0)
    png += chunk(b'IHDR', ihdr)
    raw = b''
    for y in range(height):
        raw += b'\x00' + b''.join(pixels[y])
    png += chunk(b'IDAT', zlib.compress(raw, 9))
    png += chunk(b'IEND', b'')
    path.write_bytes(png)

def rgba(r,g,b,a=255):
    return bytes((r,g,b,a))

# App icon: plate with food (orange theme)
app = []
for y in range(32):
    row = []
    for x in range(32):
        if (x-16)**2 / 64 + (y-20)**2 / 16 < 1 and y > 16:
            row.append(rgba(255,255,255))
        elif 12 < x < 20 and 14 < y < 18:
            row.append(rgba(255,87,34))
        else:
            row.append(rgba(255,255,255,0))
    app.append(row)
write_png(base / 'app.png', 32, 32, app)

# Cart icon: shopping cart in orange
cart = []
for y in range(32):
    row = []
    for x in range(32):
        if 8 < x < 24 and 12 < y < 20:
            row.append(rgba(255,87,34))
        elif y == 8 and 12 < x < 20:
            row.append(rgba(255,87,34))
        elif (x in (10,22) and y in (22,24)) or (x in (11,21) and y in (21,23)):
            row.append(rgba(0,0,0))
        else:
            row.append(rgba(255,255,255,0))
    cart.append(row)
write_png(base / 'cart.png', 32, 32, cart)

print('Updated app.png and cart.png with orange theme')