import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

from keras.utils.np_utils import to_categorical
from keras.models import Sequential
from keras.layers import *
from keras.optimizers import Adam
from keras.preprocessing.image import ImageDataGenerator
from keras.callbacks import LearningRateScheduler

data = np.load('data.npz')
X_train = data.f.train_X
y_train = data.f.train_y
X_test = data.f.test_X

scaler = StandardScaler().fit(X_train)
X_train = scaler.transform(X_train)
X_test = scaler.transform(X_test)

x_train, x_holdout, y_train, y_holdout = train_test_split(X_train, y_train, test_size=0.25, random_state=42)
x_train = x_train.reshape(-1, 64, 64, 1)
x_holdout = x_holdout.reshape(-1, 64, 64, 1)
X_test = X_test.reshape(-1, 64, 64, 1)

y_train = to_categorical(y_train)
y_holdout = to_categorical(y_holdout)

cnn_model = Sequential()

cnn_model.add(Conv2D(filters = 32, kernel_size = (5, 5), activation='relu', input_shape = (64, 64, 1)))
cnn_model.add(BatchNormalization())
cnn_model.add(Conv2D(filters = 32, kernel_size = (5, 5), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(MaxPool2D(strides=(2,2)))
cnn_model.add(Dropout(0.25))

cnn_model.add(Conv2D(filters = 64, kernel_size = (3, 3), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(Conv2D(filters = 64, kernel_size = (3, 3), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(MaxPool2D( strides=(2,2)))
cnn_model.add(Dropout(0.25))

cnn_model.add(Conv2D(filters = 64, kernel_size = (3, 3), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(Conv2D(filters = 64, kernel_size = (3, 3), activation='relu'))
cnn_model.add(BatchNormalization())
cnn_model.add(MaxPool2D(pool_size=(2,2), strides=(2,2)))
cnn_model.add(Dropout(0.25))

cnn_model.add(Flatten())
cnn_model.add(Dense(256, activation='relu'))
cnn_model.add(Dropout(0.25))
cnn_model.add(Dense(512, activation='relu'))
cnn_model.add(Dropout(0.5))
cnn_model.add(Dense(10, activation='softmax'))

datagen = ImageDataGenerator(rotation_range=10,
                            zoom_range = 0.1,
                            height_shift_range = 0.1,
                            width_shift_range = 0.1)

optimizer_Adam = Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=1e-08, decay=0.0)
cnn_model.compile(loss='categorical_crossentropy', optimizer = optimizer_Adam, metrics=["accuracy"])

lrate = LearningRateScheduler(lambda x: 1e-3 * 0.9 ** x)

history = cnn_model.fit_generator(datagen.flow(x_train, y_train, batch_size=64),
                           steps_per_epoch=1000,
                           epochs=50,
                           verbose=2,
                           validation_data=(x_holdout[:1000,:], y_holdout[:1000,:]),
                           callbacks=[lrate])

predictions_catg = cnn_model.predict(X_test, batch_size=64)
predictions = np.argmax(predictions_catg,axis=1)
df = pd.DataFrame(predictions, columns=['Label'])
df.to_csv('kaggle.csv', index_label='Id')