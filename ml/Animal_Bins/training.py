import matplotlib.pyplot as plt
import numpy as np
import os
import PIL
import tensorflow as tf

from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.models import Sequential

import struct
from struct import unpack
from PIL import Image, ImageDraw

from tensorflow.keras.layers.experimental import RandomFourierFeatures

'''
Full training script. Will go thru and create images at 1000 per class and will
then train iteratively.
'''

'''
Unpacking the binary files into drawings
'''

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

# Init gpu for training
gpus = tf.config.experimental.list_physical_devices('GPU')
if gpus:
  try:
    # Currently, memory growth needs to be the same across GPUs
    for gpu in gpus:
      tf.config.experimental.set_memory_growth(gpu, True)
    logical_gpus = tf.config.experimental.list_logical_devices('GPU')
    print(len(gpus), "Physical GPUs,", len(logical_gpus), "Logical GPUs")
  except RuntimeError as e:
    # Memory growth must be set before GPUs have been initialized
   print(e)

# classes
classes = ['bat', 'bear', 'butterfly','camel','cow', 'crab', 'crocodile', 'dolphin',
'elephant','fish','frog','giraffe','hedgehog','kangaroo','lion','lobster',
'mermaid','monkey','mouse','octopus','owl','panda','parrot','penguin','rabbit','raccoon',
'rhinoceros','scorpion','shark','sheep','snail','snake','snowman','spider','squirrel','swan',
'teddy-bear','tiger','zebra','dog', 'cat', 'ant', 'whale', 'horse',
           'bee', 'bird', 'dragon', 'flamingo', 'duck']

total_training_iterations = 10

#create_drawings(classes)
drawings = []
for cate in classes:
    drawings.append(unpack_drawings('full-binary-{}.bin'.format(cate)))

drawing = drawings[0]

# go thru first 1000 of each drawing...

for y in range(10):
    for c in range(5000):
        drawing = next(drawings[y])


# Outerloop. How many times will we train?
for z in range(total_training_iterations):
    print('recreating drawing jpg...')
    l = 0
    for cate in classes:
        for v in range(2000):
            img = Image.new('RGB', (255, 255), (255, 255, 255))
            draw = ImageDraw.Draw(img)
            inputs = []
            size = 255
            for i in range(size*size):
                inputs.append(0)
            cord = []
            drawing = next(drawings[l])
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
        l = l + 1

    data_dir = 'jpg'
    batch_size = 32
    img_height = 255
    img_width = 255

    train_ds = tf.keras.preprocessing.image_dataset_from_directory(
      data_dir,
     validation_split=0.2,
        subset="training",
        seed=123,
        image_size=(img_height, img_width),
        batch_size=batch_size)

    val_ds = tf.keras.preprocessing.image_dataset_from_directory(
        data_dir,
        validation_split=0.2,
        subset="validation",
        seed=123,
        image_size=(img_height, img_width),
        batch_size=batch_size)

    class_names = train_ds.class_names
    print(class_names)

    AUTOTUNE = tf.data.experimental.AUTOTUNE

    train_ds = train_ds.cache().shuffle(1000).prefetch(buffer_size=AUTOTUNE)
    val_ds = val_ds.cache().prefetch(buffer_size=AUTOTUNE)

    model = tf.keras.models.load_model('test_model.h5')

    num_classes = len(classes)

    epochs=10
    history = model.fit(
      train_ds,
      validation_data=val_ds,
      epochs=epochs
    )

    model.save('test_model.h5');

    acc = history.history['accuracy']
    val_acc = history.history['val_accuracy']

    loss = history.history['loss']
    val_loss = history.history['val_loss']

    epochs_range = range(epochs)
