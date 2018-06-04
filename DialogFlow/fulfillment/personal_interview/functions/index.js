'use strict';

const http = require('http');
var request = require('request');
const functions = require('firebase-functions');


exports.dialogflowFirebaseFulfillment = functions.https.onRequest((req, res) => {
  // Get the city and date from the request
  let name = req.body.queryResult.parameters['name']; // name is a required param
  let email = req.body.queryResult.parameters['email'];
  let strength = req.body.queryResult.parameters['strength'];
  let weakness = req.body.queryResult.parameters['weakness'];
  let salary = req.body.queryResult.parameters['salary'];
  let current_position = req.body.queryResult.parameters['current_position'];
  let current_responsibilities = req.body.queryResult.parameters['current_responsibilities'];
  let reason_for_changing = req.body.queryResult.parameters['reason_for_changing'];
  let goals = req.body.queryResult.parameters['goals'];

//https://hook.integromat.com/ifnw1l8pktmd8fw8ha31xtol8eefvl8d  
request.post(
    'https://hook.integromat.com/ifnw1l8pktmd8fw8ha31xtol8eefvl8d',
    { json: 
      { 
    	candidate_name: name, 
    	email_id: email,
    	strengths: strength,
    	weaknesses: weakness,
    	expected_salary: salary,
    	current_position: current_position,
    	current_responsibilities: current_responsibilities,
    	job_change_reason: reason_for_changing,
    	future_goals: goals
      } 
    },
    function (error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log(body);
            res.json({ 'fulfillmentText': 'Your data has been recorded. Thank you for the details. Good luck for the next interview!' });
        }
        else {
        	res.json({ 'fulfillmentText': 'There was an error in recoding your data. Please repeat the interview.' });
        }
    }
);

  

 
});
