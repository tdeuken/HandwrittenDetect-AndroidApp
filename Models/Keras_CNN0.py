'''
1. CNN have the advantage of using the spacial structure
2. A normal neural network kind of sucks because its an insane
amount of weights unless you use tiny images
'''

from keras.datasets import mnist
from keras.utils.np_utils import to_categorical
from keras.models import Sequential
from keras.layers import Conv2D, MaxPooling2D,Flatten, Dense
from tensorflow.contrib import lite

import os
import os.path as path



MODEL_NAME = 'mnist_covnet'

def save_model_convert(trainedModel):

    keras_file = os.getcwd()+"/K-CNN0.h5"
    trainedModel.save(keras_file)

    converter = lite.TFLiteConverter.from_keras_model_file(keras_file)
    print("Convert Done")
    tflite_model = converter.convert()
    open("K-CNN0.tflite","wb").write(tflite_model)


(X_train, y_train), (X_test, y_test) = mnist.load_data()

#Preprocessing Data

#Shape the data and say it has a depth of 1
X_train = X_train.reshape(60000, 28,28,1)
X_test = X_test.reshape(10000, 28,28,1)

#Convert to float for division
X_train = X_train.astype('float32')
X_test = X_test.astype('float32')

#Convert values to percentages
X_train /= 255.0
X_test /= 255.0

#Create the outputs and how many classes there will be -> 0-9 -> 10 classes
y_train = to_categorical(y_train, 10)
y_test = to_categorical(y_test, 10)

#Create the models


model = Sequential()
model.add(Conv2D(filters=64, kernel_size=3, strides=1, \
            padding='same', activation='relu', \
            input_shape=[28, 28, 1]))
    # 28*28*64
model.add(MaxPooling2D(pool_size=2, strides=2, padding='same'))
    # 14*14*64

model.add(Conv2D(filters=128, kernel_size=3, strides=1, \
            padding='same', activation='relu'))
    # 14*14*128
model.add(MaxPooling2D(pool_size=2, strides=2, padding='same'))
    # 7*7*128

model.add(Conv2D(filters=256, kernel_size=3, strides=1, \
            padding='same', activation='relu'))
    # 7*7*256
model.add(MaxPooling2D(pool_size=2, strides=2, padding='same'))
    # 4*4*256

model.add(Flatten())
model.add(Dense(1024, activation='relu'))
model.add(Dense(10, activation='softmax'))

#Compile it
model.compile(optimizer='Adadelta', loss='categorical_crossentropy', metrics=['accuracy'])

#Names of Input/Output nodes for saving
print("Output name is ", model.output.op.name)
print("Input name is ",model.input.op.name)

finalModel = model.fit(X_train, y_train, epochs=1, verbose=1, validation_data=(X_train, y_train))


save_model_convert(model)
