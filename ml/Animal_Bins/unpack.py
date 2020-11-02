import struct
from struct import unpack
from PIL import Image, ImageDraw


def unpack_drawing(file_handle):
    key_id, = unpack('Q', file_handle.read(8))
    country_code, = unpack('2s', file_handle.read(2))
    recognized, = unpack('b', file_handle.read(1))
    timestamp, = unpack('I', file_handle.read(4))
    n_strokes, = unpack('H', file_handle.read(2))
    image = []
    for i in range(n_strokes):
        n_points, = unpack('H', file_handle.read(2))
        fmt = str(n_points) + 'B'
        x = unpack(fmt, file_handle.read(n_points))
        y = unpack(fmt, file_handle.read(n_points))
        image.append((x, y))

    return {
        'key_id': key_id,
        'country_code': country_code,
        'recognized': recognized,
        'timestamp': timestamp,
        'image': image
    }


def unpack_drawings(filename):
    with open(filename, 'rb') as f:
        while True:
            try:
                yield unpack_drawing(f)
            except struct.error:
                break


img = Image.new('RGB', (255, 255), (255, 255, 255))
draw = ImageDraw.Draw(img)
cord = []
for drawing in unpack_drawings('full-binary-butterfly.bin'):
    for x, y in drawing['image']:
        length = len(x)
        for i in range(length):
            cord.append(x[i])
            cord.append(y[i])
        draw.line(cord, fill=(0,0,0), width=3)
    break

img.save('test.jpg', quality=95)
