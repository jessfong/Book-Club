// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require("firebase-functions");

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require("firebase-admin");
admin.initializeApp();

exports.invitesListener = functions.database
  .ref("/Invites/{inviteUID}/")
  .onCreate(async (snapshot, context) => {
    const inviteData = snapshot.val();
    const bookClubId = inviteData.bookClubId; // id of bookclub user is invited to
    const userId = inviteData.userId; // id of the user invited

    const getBookClubPromise = admin
      .database()
      .ref(`BookClubs`)
      .orderByKey()
      .equalTo(bookClubId)
      .once("child_added");
    const getUserPromise = admin
      .database()
      .ref("Users")
      .orderByChild("userId")
      .equalTo(userId)
      .once("child_added");
    const results = await Promise.all([getBookClubPromise, getUserPromise]);

    let bookClubSnapshot = results[0].val();
    let userSnapshot = results[1].val();

    // Notification details.
    const payload = {
      data: {
        title: "New invite!",
        body: `You have been invited to ${bookClubSnapshot.name}`,
        bookClubId: bookClubId,
        notificationType: "invite"
      }
    };

    return admin.messaging().sendToDevice([userSnapshot.token], payload);
  });

exports.meetingsListener = functions.database
  .ref("/Meetings/{meetingUID}/")
  .onCreate(async (snapshot, context) => {
    const meetingData = snapshot.val();
    console.log(meetingData);
    const bookClubId = meetingData.bookClubId; // id of bookclub user is invited to

    const getBookClubPromise = admin
      .database()
      .ref(`BookClubs`)
      .orderByKey()
      .equalTo(bookClubId)
      .once("child_added");
    const getUserPromise = admin
      .database()
      .ref("Members")
      .orderByChild("bookClubId")
      .equalTo(bookClubId)
      .once("value");
    const results = await Promise.all([getBookClubPromise, getUserPromise]);

    let bookClubSnapshot = results[0].val();
    let userSnapshot = results[1].val();

    var userPromises = [];
    for (var userSnapshotId in userSnapshot) {
      userPromises.push(admin
        .database()
        .ref("Users")
        .orderByChild("userId")
        .equalTo(userSnapshot[userSnapshotId].userId)
        .once("child_added"));
    }

    const userResults = await Promise.all(userPromises);
    var tokens = [];
    for (var userResultIndex = 0; userResultIndex < userResults.length; userResultIndex++) {
      var userResult = userResults[userResultIndex].val();
      if (bookClubSnapshot.clubOwner !== userResult.userId) {
        tokens.push(userResult.token);
      }
    }

    // Notification details.
    const payload = {
      data: {
        title: "New Meeting!",
        body: `A new meeting has been created for ${bookClubSnapshot.name}!`,
        bookClubId: bookClubId,
        notificationType: "meeting",
        date: meetingData.date,
        startTime: meetingData.startTime,
        endTime: meetingData.endTime,
        location: meetingData.location,
        meetingUID: context.params.meetingUID,
        bookTitle: meetingData.bookTitle,
        bookAuthor: meetingData.bookAuthor
      }
    };

    return admin.messaging().sendToDevice(tokens, payload);
  });
