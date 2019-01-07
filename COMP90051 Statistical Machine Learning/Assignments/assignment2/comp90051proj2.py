import pandas as pd
import numpy as np

from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split

from keras.utils.np_utils import to_categorical
from keras.models import Sequential
from keras.layers import *
from keras.optimizers import Adam
from keras.preprocessing.image import ImageDataGenerator
from keras.callbacks import LearningRateScheduler

#Load datasets
data = np.load('data.npz')
X_train = data.f.train_X
Y_train = data.f.train_y
X_test = data.f.test_X

#Data preprocessing
scaler_stand = StandardScaler().fit(X_train)
X_train = scaler_stand.transform(X_train)
X_test = scaler_stand.transform(X_test)
X_train = X_train.reshape(-1, 64, 64, 1)
X_test = X_test.reshape(-1, 64, 64, 1)

#Split the train dataset into train set and validation set
x_train, x_test, y_train, y_test = train_test_split(X_train, Y_train, test_size=0.25, random_state=42)

#One-hot encoding
y_train = to_categorical(y_train)
y_test = to_categorical(y_test)

#Setup thr model
cnn_model = Sequential()

cnn_model.add(Conv2D(filters=32, kernel_size=(5, 5), activation='relu', input_shape=(64, 64, 1)))
cnn_model.add(BatchNormalization())
cnn_model.add(Conv2D(filters=32, kernel_size=(5, 5), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(MaxPool2D(strides=(2, 2)))
cnn_model.add(Dropout(0.25))

cnn_model.add(Conv2D(filters=64, kernel_size=(3, 3), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(Conv2D(filters=64, kernel_size=(3, 3), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(MaxPool2D(strides=(2, 2)))
cnn_model.add(Dropout(0.25))

cnn_model.add(Conv2D(filters=64, kernel_size=(3, 3), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(Conv2D(filters=64, kernel_size=(3, 3), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(MaxPool2D(pool_size=(2, 2), strides=(2, 2)))
cnn_model.add(Dropout(0.25))

cnn_model.add(Flatten())
cnn_model.add(Dense(256, activation='relu'))
cnn_model.add(Dropout(0.25))
cnn_model.add(Dense(512, activation='relu'))
cnn_model.add(Dropout(0.5))
cnn_model.add(Dense(10, activation='softmax'))

#Data augmentation
dataaug = ImageDataGenerator(rotation_range=10, zoom_range=0.1, height_shift_range=0.1, width_shift_range=0.1)

#Optimizer from Keras document
optimizer_Adam = Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=1e-08, decay=0.0)
cnn_model.compile(optimizer=optimizer_Adam, loss='categorical_crossentropy', metrics=["accuracy"])

#Learning rate reduction from Keras
lrate = LearningRateScheduler(lambda x: 1e-3 * 0.9 ** x)

history = cnn_model.fit_generator(dataaug.flow(x_train, y_train, batch_size=64),
                                  steps_per_epoch=1000, epochs=50, verbose=2,
                                  validation_data=(x_test[:1000, :], y_test[:1000, :]),
                                  callbacks=[lrate])

#Prediction and write to .csv file
#One problem here is the generated file's Id start from 0, I have to manually change it.
predictions_catg = cnn_model.predict(X_test, batch_size=64)
predictions = np.argmax(predictions_catg, axis=1)
df = pd.DataFrame(predictions, columns=['Label'])
df.to_csv('kaggle.csv', index_label='Id')
