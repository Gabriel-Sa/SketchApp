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

model = tf.keras.models.load_model('test_model.h5')
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()
open("converted_model.tflite", "wb").write(tflite_model)
