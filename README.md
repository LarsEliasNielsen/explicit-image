# explicit-image
Image upload with **Firebase** and **Google Vision API**.

Utilises Firebase and Google Vision API to annotate uploaded images.
- [Google Vision API](https://cloud.google.com/vision/)
  - [Label detection](https://cloud.google.com/vision/docs/label-tutorial)
  - [Safe search](https://cloud.google.com/vision/docs/requests-and-responses)
- [Firebase](https://firebase.google.com/)
  - [Authentication](https://firebase.google.com/docs/auth/)
  - [Database](https://firebase.google.com/docs/database/)
  - [Storage](https://firebase.google.com/docs/storage/)

This application is a demonstration of SafeSearch from Google Vision API with Firebase storage and realtime database. An image is annotated with the Vision API to determine the image content and then the image is stored in Firebase Storage and the data is saved in Firebase Realtime Database.

# Setup
You'll need access to:
- Google Cloud Platform project with billing enabled.
- Firebase free project ([Spark](https://firebase.google.com/pricing/))


### Enable Google Vision API
Enable Google Vision API in Google Cloud Platform (API Manager -> Library).

Add new credentials for your application; a simple API key **with no key restriction** is used here.


### Setup Firebase project
Go to your Firebase console and create a new Firebase project.
Add your `google-services.json` file to the project.

### Enable Google Sign-in method
Enable Google sign-in method in the Firebase console (Authentication > Sign-in Method > Google).
Under Web SDK configuration, you can get your web client id, which needs to be assigned to `webClientId` in the properties.


### Add properties
Create a new `explicitimage.properties` file in the root of the project to hold user information and API key.

The file must at least contain the following properties:

`key`: API key from Google Cloud Platform (API Manager -> Credentials).

`webClientId`: Web client id for Firebase project. Can be found in Firebase console under authentication or in your Google console under credentials.

`storageUrl`: Firebase Storage url (`gs://<your-firebase-storage-bucket>`)