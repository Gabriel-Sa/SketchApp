import struct
from struct import unpack
from PIL import Image, ImageDraw
import os
from threading import Thread
import time

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
inputs = []
size = 255
for i in range(size*size):
    inputs.append(0)
cord = []

classes = ['bat', 'bear', 'butterfly','camel','cow', 'crab', 'crocodile', 'dolphin',
'elephant','fish','frog','giraffe','hedgehog','kangaroo','lion','lobster',
'mermaid','monkey','mouse','octopus','owl','panda','parrot','penguin','rabbit','raccoon',
'rhinoceros','scorpion','shark','sheep','snail','snake','snowman','spider','squirrel','swan',
'teddy-bear','tiger','zebra','dog', 'cat', 'ant', 'whale', 'horse',
           'bee', 'bird', 'dragon', 'flamingo', 'duck']

for cate in classes:
    os.mkdir('jpg/{}'.format(cate))

def create_drawings(cate):
    print(cate)
    drawings = unpack_drawings('full-binary-{}.bin'.format(cate))
    for v in range(2000):
        img = Image.new('RGB', (255, 255), (255, 255, 255))
        draw = ImageDraw.Draw(img)
        inputs = []
        size = 255
        for i in range(size*size):
            inputs.append(0)
        cord = []
        drawing = next(drawings)
        for x, y in drawing['image']:
            length = len(x)
            for i in range(length):
                cord.append(x[i])
                cord.append(y[i])
                address = x[i] + (y[i]*(size-1))
                inputs[address - 1] = 1
            draw.line(cord, fill=(0,0,0), width=4)
            cord = []
        img.save('jpg/{}/test-{}.jpg'.format(cate, v), quality=100)
time.sleep(1)
threads = []
for cate in classes:
    t = Thread(target=create_drawings, args=(cate,))
    t.start()
    threads.append(t)

for t in threads:
    t.join()
