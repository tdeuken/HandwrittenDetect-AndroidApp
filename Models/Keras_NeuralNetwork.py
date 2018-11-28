from keras.datasets import mnist
from keras.preprocessing.image import load_img, array_to_img
from keras.utils.np_utils import to_categorical
from keras.models import Sequential
from keras.layers import Dense
from tensorflow import keras
from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib
import os
import os.path as path
from tensorflow.contrib import lite


MODEL_NAME = 'mnist_covnet'

def save_model_convert(trainedModel):

    keras_file = os.getcwd()+"/K-NN.h5"
    print(keras_file)
    model.save(keras_file)

    converter = lite.TFLiteConverter.from_keras_model_file(keras_file)

    tflite_model = converter.convert()
    open("K-NN.tflite","wb").write(tflite_model)


(X_train, y_train), (X_test, y_test)= mnist.load_data()


#Preprocessing our Training Data
image_height, image_width = 28,28
#This Transforms the shape from being 28,28 to being one long ass line thats 28*28
X_train = X_train.reshape(60000, image_height*image_width)
X_test = X_test.reshape(10000, image_height*image_width)


#Convert numbers to floats to get them ready for division
X_train = X_train.astype('float32')
X_test = X_test.astype('float32')

#Make all the variables basically a percentage between 0-1
X_train /= 255.0
X_test /= 255.0

#Different class outputs. This case we will have 10
y_train = to_categorical(y_train, 10)
y_test = to_categorical(y_test, 10)

#Build the model
model = Sequential()

#Dense = fully connected nodes
#Argument 1: How many output nodes (512)
#Argument 2: Activation Function
#Argument 3: Input shape (28x28 = 784)
model.add(Dense(512, activation='relu', input_shape=(784,)))
model.add(Dense(512, activation='relu'))

#Final Layer, we want it to be one of the 10 classes
model.add(Dense(10, activation='softmax'))

#Compile this model
#Argument 1: Optimizer - example. Stocastic Gradient Desent. We will use adam
#Argument 2: Loss Function - example. L2 norm. We will use categorical_crossentropy (This allows for multiclass)
#Argument 3: Metrics - Show performance. We will use accuracy
model.compile(optimizer='Adadelta', loss = 'categorical_crossentropy', metrics = ['accuracy'])

#Check this if you want to see the summary with Parameters
#model.summary()

#Get the Input/Output node names for saving
outputNode = model.output.op.name
inputNode = model.input.op.name

print("Output name is ", outputNode)
print("Input name is ",inputNode)


#Time to train the model
#Arugment 1: The training data
#Argument 2: The true labels
#Argument 3: Amount of iterations through the data
#Argument 4: Validation data is the correct labels

model.fit(X_train, y_train, epochs=1, validation_data = (X_test, y_test))

print(model.get_weights())

save_model_convert(model)
print("Completed")
