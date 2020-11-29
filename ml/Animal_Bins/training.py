import matplotlib.pyplot as plt
import numpy as np
import os
import shutil
from threading import Thread
import time
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

def create_drawings(cate, drawings):
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
            draw.line(cord, fill=(0,0,0), width=6)
            cord = []
        img.save('jpg/{}/test-{}.jpg'.format(cate, v), quality=100)

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
classes = ['bear','cow', 'dolphin',
'elephant','fish','frog','mouse','penguin','rabbit','sheep',
'snake','dog', 'cat', 'whale', 'horse',
           'bee', 'bird', 'duck']

total_training_iterations = 50

#create_drawings(classes)

try:
    shutil.rmtree('jpg')
    os.mkdir('jpg')
except OSError as e:
    print("Error: %s : %s" % ('jpg', e.strerror))
    os.mkdir('jpg')

drawings = []
for cate in classes:
    drawings.append(unpack_drawings('full-binary-{}.bin'.format(cate)))
    os.mkdir('jpg/{}'.format(cate))


# go thru first 1000 of each drawing...

#for y in range(10):
#    for c in range(5000):
#        drawing = next(drawings[y])

model = None

# Outerloop. How many times will we train?
for z in range(total_training_iterations):
    print('recreating drawing jpg...')
    threads = []
    for j in range(len(drawings)):
        t = Thread(target=create_drawings, args=(classes[j],drawings[j]))
        t.start()
        threads.append(t)
    for t in threads:
        t.join()

    data_dir = 'jpg'
    batch_size = 32
    img_height = 255
    img_width = 255

    num_classes = len(classes)

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

    data_augmentation = keras.Sequential(
      [
        layers.experimental.preprocessing.RandomFlip("horizontal",
                                                     input_shape=(img_height,
                                                                  img_width,
                                                                  3)),
        layers.experimental.preprocessing.RandomRotation(0.1),
        layers.experimental.preprocessing.RandomZoom(0.1),
      ]
    )

    class_names = train_ds.class_names
    print(class_names)

    AUTOTUNE = tf.data.experimental.AUTOTUNE

    train_ds = train_ds.cache().shuffle(200).prefetch(buffer_size=AUTOTUNE)
    val_ds = val_ds.cache().prefetch(buffer_size=AUTOTUNE)

    if model is None:
        model = Sequential([
          data_augmentation,
          layers.experimental.preprocessing.Rescaling(1./255),
          layers.Conv2D(16, 3, padding='same', activation='relu'),
          layers.MaxPooling2D(),
          layers.Conv2D(32, 3, padding='same', activation='relu'),
          layers.MaxPooling2D(),
          layers.Conv2D(64, 3, padding='same', activation='relu'),
          layers.MaxPooling2D(),
          layers.Dropout(0.2),
          layers.Flatten(),
          layers.Dense(128, activation='relu'),
          layers.Dense(num_classes)
        ])
        model.compile(optimizer='adam',
                      loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True),
                      metrics=['accuracy'])

    epochs=10
    history = model.fit(
      train_ds,
      validation_data=val_ds,
      epochs=epochs
    )

    model.save('model.h5');

    acc = history.history['accuracy']
    val_acc = history.history['val_accuracy']

    loss = history.history['loss']
    val_loss = history.history['val_loss']

    epochs_range = range(epochs)
