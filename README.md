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


# Setup
### Enable Google Vision API
Enable Google Vision API in Google Cloud Platform (API Manager -> Library).

### Setup Firebase project
Go to your Firebase console and create a new Firebase project.
Add your `google-services.json` file to the project.

### Add properties
Create a new `explicitimage.properties` file in the root of the project to hold user information and API key.
The file must atleast contain the following properties:

`key`: API key from Google Cloud Platform (API Manager -> Credentials).

`user` and `password`: Username/email and password of user in Firebase Authentication.