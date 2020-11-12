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

#from tensorflow.keras.layers.experimental import RandomFourierFeatures

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

class_names = ['bat', 'bear', 'butterfly','camel','cow', 'crab', 'crocodile', 'dolphin',
'elephant','fish','frog','giraffe','hedgehog','kangaroo','lion','lobster',
'mermaid','monkey','mouse','octopus','owl','panda','parrot','penguin','rabbit','raccoon',
'rhinoceros','scorpion','shark','sheep','snail','snake','snowman','spider','squirrel','swan',
'teddy-bear','tiger','zebra','dog', 'cat', 'ant', 'whale', 'horse',
           'bee', 'bird', 'dragon', 'flamingo', 'duck']    

model = keras.models.load_model('test_model.h5')

img = keras.preprocessing.image.load_img(
    'test.jpg', target_size=(255, 255)
)
img_array = keras.preprocessing.image.img_to_array(img)
img_array = tf.expand_dims(img_array, 0) # Create a batch

predictions = model.predict(img_array)
score = tf.nn.softmax(predictions[0])

print(
    "This image most likely belongs to {} with a {:.2f} percent confidence."
    .format(class_names[np.argmax(score)], 100 * np.max(score))
)
