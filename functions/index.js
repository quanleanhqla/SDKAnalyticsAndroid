 'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const https = require('https');
var request = require('requestretry');

admin.initializeApp();

var database = admin.filestore();

exports.appUninstall = functions.analytics.event('app_remove').onLog(event => {
database.doc("Trial/Trial1").update({"timestamp": admin.filestore.Timestamp.now() });

return console.log('Uninstalled because app_remove event fired');
});

exports.sendAndroidUninstallToMobio = functions.analytics.event('app_remove').onLog((event) => {

    //console.log("Event is: " + JSON.stringify(event));

    function myRetryStrategy(err, response, body, options) {
        // retry the request if we had an error or if the response was a 'Bad Gateway'
        return !!err || response.statusCode === 503;
    }

    var mobioId = event.user.userProperties.mobio_id.value;
    var token_id = event.user.userProperties.token_id.value;
    // This is where the CleverTap ID of the user who uninstalled the app is passed as an identifier in the API call.
    const data = JSON.stringify({
       "d": [{
            "objectId": mobioId,
            "type": "event",
            "evtName" : "App Uninstalled",
            "evtData": {
            }
        }]
    });

    request({
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': token_id,
            'X-Merchant-Id': '1b99bdcf-d582-4f49-9715-1b61dfff3924',
            'User-Agent': 'analytics-android 1.0.0'
        },
        body: data,
        url: 'https://api-test1.mobio.vn/dynamic-event/api/v1.0/sync',

        // The below parameters are specific to request-retry
        maxAttempts: 5, // (default) try 5 times
        retryDelay: 2000, // (default) wait for 2s before trying again
        retryStrategy: myRetryStrategy // (default) retry on 5xx or network errors
    }, function (err, response, body) {
        // this callback will only be called when the request succeeded or after maxAttempts or on error
        if (response && response.statusCode === 200) {
            console.log("Response Body: " + JSON.stringify(body));
            console.log('The number of request attempts: ' + response.attempts);
            return 0;
        }else{
            console.log("err: " + err + " ,response: " + JSON.stringify(response) + " , body: " + JSON.stringify(body));
            return 1;
        }
    });


});
