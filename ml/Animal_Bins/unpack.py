import struct
from struct import unpack
import numpy as np
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


def bresenham(cord):
    if len(cord) % 2 != 0:
        raise ValueError("Invalid number of inputs")
    retArray = []
    for i in range(0, len(cord) - 2, 2):
        x1 = cord[i]
        y1 = cord[i+1]
        x2 = cord[i+2]
        y2 = cord[i+3]
        m = 2 * (y2 - y1)
        m_error = m - (x2 - x1)
        y = y1
        for x in range(x1, x2+1):
            if x > 0:
                if x > 254:
                    x = 254
                if y > 254:
                    y = 254
                retArray.append(x)
                retArray.append(y)
            m_error = m_error + m
            if m_error >= 0:
                y = y + 1
                m_error = m_error - (2 * (x2 - x1))
    return retArray

def get_input(name):
    inputs = []
    cord = []
    retArray = []
    size = 255
    it = 0
    for drawing in unpack_drawings('full-binary-{}.bin'.format(name)):
        if it > 30000:
            break
        inputs = [0] * 65025
        for x, y in drawing['image']:
            for i in range(0, len(x)):
                cord.append(x[i])
                cord.append(y[i])
            ret = bresenham(cord)
            cord = []
            if len(ret) >= 2:
                for k in range(0, len(ret) - 2, 2):
                    address = ret[k] + (ret[k+1] * size)
                    inputs[address] = 1
        retArray.append(inputs)
        it = it + 1
    return retArray

if __name__ == '__main__':
# ------- Drawing Section ---------
# img = Image.new('RGB', (255, 255), (255, 255, 255))
# draw = ImageDraw.Draw(img)
# cord = []
# cate = input("Enter Category: ")
# val = int(input("Enter drawing #: "))
# drawings = unpack_drawings('full-binary-{}.bin'.format(cate))
#
# for v in range(val):
#     drawing = next(drawings)
#
# for x, y in drawing['image']:
#     length = len(x)
#     for i in range(length):
#         cord.append(x[i])
#         cord.append(y[i])
#     test = draw.line(cord, fill=(0,0,0), width=1)
#     cord = []
# img.save('test.jpg', quality=100)
